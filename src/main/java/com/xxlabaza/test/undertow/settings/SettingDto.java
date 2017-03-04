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

import static lombok.AccessLevel.PRIVATE;

import lombok.Data;
import lombok.experimental.FieldDefaults;

/**
 * @author Artem Labazin <xxlabaza@gmail.com>
 * @since 01.03.2017
 */
@Data
@FieldDefaults(level = PRIVATE)
public class SettingDto {

    String key;

    String value;
}
