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

package com.xxlabaza.test.undertow.util.functional;

import lombok.Value;

/**
 * @author Artem Labazin <xxlabaza@gmail.com>
 * @since 02.03.2017
 */
@Value
public class Triple<A, B, C> {

    A _1;

    B _2;

    C _3;

    public A _1 () {
        return _1;
    }

    public B _2 () {
        return _2;
    }

    public C _3 () {
        return _3;
    }
}
