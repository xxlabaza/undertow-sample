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

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import lombok.SneakyThrows;

/**
 * @author Artem Labazin <xxlabaza@gmail.com>
 * @since 01.03.2017
 */
public final class PrepareStatementUtil {

    private static final Map<Class<?>, StatementSetter> SETTERS;

    static {
        SETTERS = new HashMap<>(11, 1.F);
        SETTERS.put(Integer.class,
                    (statement, index, obj) -> statement.setInt(index, (Integer) obj));
        SETTERS.put(Long.class,
                    (statement, index, obj) -> statement.setLong(index, (Long) obj));
        SETTERS.put(Short.class,
                    (statement, index, obj) -> statement.setShort(index, (Short) obj));
        SETTERS.put(Byte.class,
                    (statement, index, obj) -> statement.setByte(index, (Byte) obj));
        SETTERS.put(BigDecimal.class,
                    (statement, index, obj) -> statement.setBigDecimal(index, (BigDecimal) obj));
        SETTERS.put(Float.class,
                    (statement, index, obj) -> statement.setFloat(index, (Float) obj));
        SETTERS.put(Double.class,
                    (statement, index, obj) -> statement.setDouble(index, (Double) obj));
        SETTERS.put(Boolean.class,
                    (statement, index, obj) -> statement.setBoolean(index, (Boolean) obj));
        SETTERS.put(String.class,
                    (statement, index, obj) -> statement.setString(index, (String) obj));
        SETTERS.put(Date.class,
                    (statement, index, obj) -> statement.setDate(index, (Date) obj));
        SETTERS.put(Timestamp.class,
                    (statement, index, obj) -> statement.setTimestamp(index, (Timestamp) obj));
    }

    @SneakyThrows
    public static void prepareStatement (PreparedStatement prepareStatement, Object... args) {
        if (args == null) {
            return;
        }
        for (int index = 0; index < args.length; index++) {
            Object object = args[index];
            StatementSetter statementSetter = SETTERS.get(object.getClass());
            if (statementSetter == null) {
                throw new RuntimeException();
            }
            statementSetter.set(prepareStatement, index + 1, object);
        }
    }

    private PrepareStatementUtil () {
    }

    private static interface StatementSetter {

        void set (PreparedStatement statement, int index, Object obj) throws Exception;
    }
}
