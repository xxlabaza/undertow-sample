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

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;

import com.xxlabaza.test.undertow.util.functional.Pair;
import com.xxlabaza.test.undertow.util.functional.Triple;
import java.util.Map;

/**
 * @author Artem Labazin <xxlabaza@gmail.com>
 * @since 28.02.2017
 */
public final class SettingsService {

    public static SettingsService getInstance () {
        return SettingsServiceHolder.HOLDER_INSTANCE;
    }

    private final SettingsRepository repository;

    private SettingsService () {
        repository = SettingsRepository.getInstance();
    }

    public Map<String, String> create (String uid, String key, String value) {
        return repository.save(uid, key, value).stream().collect(toMap(Pair::_1, Pair::_2));
    }

    public Map<String, Map<String, String>> findAll () {
        return repository.findAll().stream()
                .collect(groupingBy(Triple::_1, toMap(Triple::_2, Triple::_3)));
    }

    public Map<String, String> findAll (String uid) {
        return repository.findAll(uid).stream().collect(toMap(Pair::_1, Pair::_2));
    }

    public void delete (String uid) {
        repository.delete(uid);
    }

    public void delete (String uid, String key) {
        repository.delete(uid, key);
    }

    private static interface SettingsServiceHolder {

        public static final SettingsService HOLDER_INSTANCE = new SettingsService();
    }
}
