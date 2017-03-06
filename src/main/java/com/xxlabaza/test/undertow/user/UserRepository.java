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

package com.xxlabaza.test.undertow.user;

import com.xxlabaza.test.undertow.util.SessionUtil;
import com.xxlabaza.test.undertow.util.functional.Function;
import java.sql.ResultSet;
import java.util.List;

/**
 * @author Artem Labazin <xxlabaza@gmail.com>
 * @since 01.03.2017
 */
public final class UserRepository {

    public static UserRepository getInstance () {
        return UserRepositoryHolder.HOLDER_INSTANCE;
    }

    private final SessionUtil sessionUtil;

    private final Function<ResultSet, User> userMapper;

    private UserRepository () {
        sessionUtil = SessionUtil.getInstance();
        userMapper = (resultSet) -> new User(resultSet.getString(1), resultSet.getString(2));
    }

    public User save (User user) {
        return sessionUtil.update(
                "INSERT INTO \"users\" " +
                "(\"id\", \"name\") " +
                "VALUES " +
                "(?, ?) " +
                "RETURNING \"id\", \"name\"",
                userMapper,
                user.getId(), user.getName()
        );
    }

    public User findOne (String id) {
        return sessionUtil.fetchOne(
                "SELECT " +
                "  \"id\", " +
                "  \"name\" " +
                "FROM \"users\" " +
                "WHERE \"id\" = ?",
                userMapper,
                id
        );
    }

    public List<User> findAll () {
        return sessionUtil.fetchAll(
                "SELECT " +
                "  \"id\", " +
                "  \"name\" " +
                "FROM \"users\"",
                userMapper
        );
    }

    public void delete (String id) {
        sessionUtil.update(
                "DELETE FROM \"users\" " +
                "WHERE \"id\" = ?",
                id
        );
    }

    private static interface UserRepositoryHolder {

        public static final UserRepository HOLDER_INSTANCE = new UserRepository();
    }
}
