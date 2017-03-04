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

package com.xxlabaza.test.undertow.settings;

import com.xxlabaza.test.undertow.util.SessionUtil;
import com.xxlabaza.test.undertow.util.SessionUtil.ResultSetMapper;
import com.xxlabaza.test.undertow.util.functional.Pair;
import com.xxlabaza.test.undertow.util.functional.Triple;
import java.util.Collection;
import java.util.List;

/**
 * @author Artem Labazin <xxlabaza@gmail.com>
 * @since 02.03.2017
 */
public final class SettingsRepository {

    public static SettingsRepository getInstance () {
        return SettingsRepositoryHolder.HOLDER_INSTANCE;
    }

    private final SessionUtil sessionUtil;

    private final ResultSetMapper<Pair<String, String>> settingsMapper;

    private final ResultSetMapper<Triple<String, String, String>> allSettingsMapper;

    private SettingsRepository () {
        sessionUtil = SessionUtil.getInstance();
        settingsMapper = (resultSet) ->
                new Pair<>(resultSet.getString(1), resultSet.getString(2));
        allSettingsMapper = (resultSet) ->
                new Triple<>(resultSet.getString(1), resultSet.getString(2), resultSet.getString(3));
    }

    public Collection<Pair<String, String>> save (String uid, String key, String value) {
        sessionUtil.update(
                "INSERT INTO \"settings\" " +
                "(\"user\", \"key\", \"value\") " +
                "VALUES " +
                "(?, ?, ?)",
                uid, key, value
        );
        return findAll(uid);
    }

    public Collection<Triple<String, String, String>> findAll () {
        return sessionUtil.fetchAll(
                "SELECT " +
                "  \"user\", " +
                "  \"key\", " +
                "  \"value\" " +
                "FROM \"settings\"",
                allSettingsMapper
        );
    }

    public List<Pair<String, String>> findAll (String uid) {
        return sessionUtil.fetchAll(
                "SELECT " +
                "  \"key\", " +
                "  \"value\" " +
                "FROM \"settings\" " +
                "WHERE \"user\" = ?",
                settingsMapper,
                uid
        );
    }

    public void delete (String uid) {
        sessionUtil.update(
                "DELETE FROM \"settings\" " +
                "WHERE \"user\" = ?",
                uid
        );
    }

    public void delete (String uid, String key) {
        sessionUtil.update(
                "DELETE FROM \"settings\" " +
                "WHERE " +
                "  \"user\" = ? " +
                "  AND " +
                "  \"key\" = ?",
                uid, key
        );
    }

    private static interface SettingsRepositoryHolder {

        public static final SettingsRepository HOLDER_INSTANCE = new SettingsRepository();
    }
}
