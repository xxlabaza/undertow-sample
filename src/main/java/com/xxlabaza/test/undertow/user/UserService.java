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

import com.xxlabaza.test.undertow.settings.SettingsService;
import java.util.Collection;
import java.util.UUID;
import lombok.val;

/**
 * @author Artem Labazin <xxlabaza@gmail.com>
 * @since 28.02.2017
 */
public final class UserService {

    public static UserService getInstance () {
        return UserServiceHolder.HOLDER_INSTANCE;
    }

    private final UserRepository repository;

    private final SettingsService settingsService;

    private UserService () {
        repository = UserRepository.getInstance();
        settingsService = SettingsService.getInstance();
    }

    public User create (User user) {
        val uid = UUID.randomUUID().toString().replaceAll("-", "");
        user.setId(uid);
        return repository.save(user);
    }

    public User get (String uid) {
        User user = repository.findOne(uid);
        if (user == null) {
            throw new UserNotFoundException();
        }
        return user;
    }

    public Collection<User> getAll () {
        return repository.findAll();
    }

    public void delete (String uid) {
        User user = get(uid);
        repository.delete(user.getId());
        settingsService.delete(user.getId());
    }

    private static interface UserServiceHolder {

        public static final UserService HOLDER_INSTANCE = new UserService();
    }
}
