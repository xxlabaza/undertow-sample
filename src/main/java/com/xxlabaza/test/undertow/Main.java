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
package com.xxlabaza.test.undertow;

import com.xxlabaza.test.undertow.settings.SettingsController;
import com.xxlabaza.test.undertow.user.UserController;
import com.xxlabaza.test.undertow.util.MigrationUtil;
import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.RoutingHandler;
import io.undertow.server.handlers.accesslog.AccessLogHandler;
import lombok.extern.java.Log;

/**
 * @author Artem Labazin <xxlabaza@gmail.com>
 * @since 28.02.2017
 */
@Log
public class Main {

    public static void main (String[] args) {
        MigrationUtil.migrate();

        RoutingHandler routingHandler = Handlers.routing()
                .addAll(UserController.handlers())
                .addAll(SettingsController.handlers());

        HttpHandler rootHandler = new AccessLogHandler(
                ExceptionHandlers.wrap(routingHandler),
                (message) -> log.info(message),
                "combined",
                Main.class.getClassLoader()
        );

        Undertow.builder()
                .addHttpListener(8080, "0.0.0.0")
                .setHandler(rootHandler)
                .build()
                .start();
    }
}
