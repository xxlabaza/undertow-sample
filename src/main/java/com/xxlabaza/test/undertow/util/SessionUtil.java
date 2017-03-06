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

import com.xxlabaza.test.undertow.util.functional.Function;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.postgresql.ds.PGSimpleDataSource;

/**
 * @author Artem Labazin <xxlabaza@gmail.com>
 * @since 01.03.2017
 */
@Log
public final class SessionUtil {

    public static final String DATABASE_SCHEMA = "undertow_example";

    private static volatile SessionUtil instance;

    public static SessionUtil getInstance () {
        SessionUtil localInstance = instance;
        if (localInstance == null) {
            synchronized (SessionUtil.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new SessionUtil();
                }
            }
        }
        return localInstance;
    }

    private final DataSource dataSource;

    private SessionUtil () {
        PGSimpleDataSource source = new PGSimpleDataSource();

        source.setDatabaseName("test");
        source.setServerName("localhost");
        source.setPortNumber(5432);
        source.setUser("admin");
        source.setPassword("admin");
        source.setCurrentSchema(DATABASE_SCHEMA);

        dataSource = source;

        checkDataSource();
    }

    public <T> T fetchOne (String query, Function<ResultSet, T> mapper, Object... args) {
        return connect(query, (statement) -> {
                   ResultSet resultSet = statement.executeQuery();
                   resultSet.next();
                   return mapper.apply(resultSet);
               }, args);
    }

    public <T> List<T> fetchAll (String query, Function<ResultSet, T> mapper, Object... args) {
        return connect(query, (statement) -> {
                   List<T> result = new ArrayList<>();
                   ResultSet resultSet = statement.executeQuery();
                   while (resultSet.next()) {
                       result.add(mapper.apply(resultSet));
                   }
                   return result;
               }, args);
    }

    public int update (String query, Object... args) {
        return connect(query, (statement) -> statement.executeUpdate(), args);
    }

    public <T> T update (String query, Function<ResultSet, T> mapper, Object... args) {
        return connect(query, (statement) -> {
                   ResultSet resultSet = statement.executeQuery();
                   resultSet.next();
                   return mapper.apply(resultSet);
               }, args);
    }

    @SneakyThrows
    private void checkDataSource () {
        try (Connection connection = dataSource.getConnection()) {
            Statement statement = connection.createStatement();
            statement.execute("SELECT 1");
        }
        log.info("Database connection: SUCCESSFUL");
    }

    @SneakyThrows
    private <T> T connect (String query, Function<PreparedStatement, T> action, Object... args) {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement prepareStatement = connection.prepareStatement(query);
            PrepareStatementUtil.prepareStatement(prepareStatement, args);
            return action.apply(prepareStatement);
        }
    }
}
