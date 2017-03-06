/*
 * Copyright 2017 Artem Labazin <xxlabaza@gmail.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.xxlabaza.test.undertow.util;

import static java.util.Collections.emptyMap;
import static java.util.logging.Level.INFO;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toSet;

import com.xxlabaza.test.undertow.util.functional.Pair;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.Collections;
import java.util.Set;
import java.util.stream.IntStream;
import lombok.SneakyThrows;
import lombok.extern.java.Log;

/**
 * @author Artem Labazin <xxlabaza@gmail.com>
 * @since 01.03.2017
 */
@Log
public final class MigrationUtil {

    private static final SessionUtil SESSION;

    private static final Path MIGRATION_FOLDER;

    static {
        SESSION = SessionUtil.getInstance();

        try {
            URI uri = MigrationUtil.class.getResource("/migration").toURI();
            MIGRATION_FOLDER = "jar".equals(uri.getScheme())
                               ? FileSystems.newFileSystem(uri, emptyMap()).getPath("/migration")
                               : Paths.get(uri);
        } catch (IOException | URISyntaxException ex) {
            throw new RuntimeException(ex);
        }
        if (!Files.exists(MIGRATION_FOLDER) || !Files.isDirectory(MIGRATION_FOLDER)) {
            throw new IllegalArgumentException("Migration folder doesn't exist");
        }
    }

    @SneakyThrows
    public static void migrate () {
        Set<Path> previousVersionFileNames;
        if (isSchemaVersionTableExists()) {
            log.info("Schema version table exists");
            previousVersionFileNames = checkPreviousVersions();
        } else {
            log.info("Schema version table doesn't exist");
            createSchemaVersionTable();
            log.info("Schema version table was created");
            previousVersionFileNames = Collections.emptySet();
        }
        log.log(INFO, "Found {0} previous version files", previousVersionFileNames.size());

        Files.walk(MIGRATION_FOLDER, 1)
                .filter(it -> it.getFileName().toString().endsWith(".sql"))
                .filter(it -> !previousVersionFileNames.contains(it))
                .sorted()
                .forEach(it -> {
                    log.log(INFO, "Processing {0} script file", it.getFileName());
                    String content = getFileContent(it);
                    SESSION.update(content);
                    createNewSchemaVersion(it);
                });
    }

    @SneakyThrows
    private static String getFileChecksum (Path file) {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA1");
        messageDigest.update(Files.readAllBytes(file));

        byte[] bytes = messageDigest.digest();
        return IntStream.range(0, bytes.length)
                .boxed()
                .map(it -> (bytes[it] & 0xff) + 0x100)
                .map(it -> Integer.toString(it, 16))
                .map(it -> it.substring(1))
                .collect(joining());
    }

    private static boolean isSchemaVersionTableExists () {
        return SESSION.fetchOne(
                "SELECT exists( " +
                "  SELECT * " +
                "  FROM \"information_schema\".\"tables\" " +
                "  WHERE " +
                "    \"table_schema\" = ? " +
                "    AND " +
                "    \"table_name\" = 'schema_version'" +
                ")",
                (resultSet) -> resultSet.getBoolean(1),
                SessionUtil.DATABASE_SCHEMA
        );
    }

    private static void createSchemaVersionTable () {
        SESSION.update(
                "CREATE TABLE \"schema_version\" ( " +
                "  \"version_rank\"      INTEGER                     NOT NULL, " +
                "  \"installed_rank\"    INTEGER                     NOT NULL, " +
                "  \"version\"           VARCHAR(50)                 NOT NULL, " +
                "  \"description\"       VARCHAR(200)                NOT NULL, " +
                "  \"type\"              VARCHAR(20)                 NOT NULL, " +
                "  \"script\"            VARCHAR(1000)               NOT NULL, " +
                "  \"checksum\"          CHAR(40), " +
                "  \"installed_by\"      VARCHAR(100)                NOT NULL, " +
                "  \"installed_on\"      TIMESTAMP   DEFAULT now()   NOT NULL, " +
                "  \"execution_time\"    INTEGER                     NOT NULL, " +
                "  \"success\"           BOOLEAN                     NOT NULL, " +
                "" +
                "  PRIMARY KEY (\"version\")" +
                ")"
        );
    }

    private static Set<Path> checkPreviousVersions () {
        return SESSION.fetchAll(
                "SELECT " +
                "  \"script\", " +
                "  \"checksum\" " +
                "FROM \"schema_version\" " +
                "WHERE \"success\" IS TRUE " +
                "ORDER BY \"version\" ASC",
                (resultSet) -> new Pair<>(resultSet.getString(1), resultSet.getString(2))
        )
                .stream()
                .unordered()
                .map(it -> {
                    Path file = MIGRATION_FOLDER.resolve(it._1());
                    if (!it._2().equals(getFileChecksum(file))) {
                        throw new RuntimeException();
                    }
                    return file;
                })
                .collect(toSet());
    }

    private static void createNewSchemaVersion (Path file) {
        String fileName = file.getFileName().toString();
        int index = fileName.indexOf("__");

        String version = fileName.substring(1, index);
        Integer rank = new Integer(version);
        String description = fileName.substring(index + 2, fileName.length() - 4).replaceAll("_", " ");
        String checksum = getFileChecksum(file);
        String installedBy = System.getProperty("user.name");
        SESSION.update(
                "INSERT INTO \"schema_version\" " +
                "(\"version_rank\", " +
                " \"installed_rank\", " +
                " \"version\", " +
                " \"description\", " +
                " \"type\", " +
                " \"script\", " +
                " \"checksum\", " +
                " \"installed_by\", " +
                " \"execution_time\", " +
                " \"success\"" +
                ") " +
                "VALUES " +
                "(?, ?, ?, ?, 'SQL', ?, ?, ?, 0, TRUE)",
                rank, rank, version, description, fileName, checksum, installedBy
        );
    }

    @SneakyThrows
    private static String getFileContent (Path path) {
        return String.join("\n", Files.readAllLines(path));
    }

    private MigrationUtil () {
    }
}
