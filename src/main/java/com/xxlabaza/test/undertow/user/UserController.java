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

import static io.undertow.util.Headers.CONTENT_TYPE;
import static io.undertow.util.StatusCodes.CREATED;
import static io.undertow.util.StatusCodes.NO_CONTENT;
import static java.nio.charset.StandardCharsets.UTF_8;

import com.xxlabaza.test.undertow.util.JsonUtil;
import io.undertow.Handlers;
import io.undertow.io.Receiver.FullStringCallback;
import io.undertow.predicate.Predicate;
import io.undertow.server.HttpHandler;
import io.undertow.server.RoutingHandler;
import io.undertow.server.handlers.ExceptionHandler;
import java.util.Collection;

/**
 * @author Artem Labazin <xxlabaza@gmail.com>
 * @since 28.02.2017
 */
public class UserController {

    public static RoutingHandler handlers () {
        Predicate containsId =
                (exchange) -> exchange.getQueryParameters().containsKey("id");

        Predicate noQueryParameters =
                (exchange) -> exchange.getQueryParameters().isEmpty();

        UserController controller = new UserController();
        return Handlers.routing()
                .post("/users", controller.createUser())
                .get("/users/{id}", containsId, controller.getUser())
                .get("/users", noQueryParameters, controller.getAllUsers())
                .delete("/users/{id}", containsId, controller.deleteUser());
    }

    public static void exceptionHandlers (ExceptionHandler exceptionHandler) {
        exceptionHandler
                .addExceptionHandler(UserNotFoundException.class, UserNotFoundException::handle);
    }

    private final UserService service;

    private UserController () {
        service = UserService.getInstance();
    }

    // BlockingHandler
    private HttpHandler createUser () {
        FullStringCallback callback = (exchange, payload) -> {
            User dto = JsonUtil.fromJson(payload, User.class);
            User user = service.create(dto);

            String response = JsonUtil.toJson(user);

            exchange.setStatusCode(CREATED);
            exchange.getResponseHeaders().put(CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send(response, UTF_8);
        };

        return (exchange) -> exchange.getRequestReceiver().receiveFullString(callback, UTF_8);
    }

    private HttpHandler getUser () {
        return (exchange) -> {
            String uid = exchange.getQueryParameters()
                    .get("id")
                    .getFirst();

            User user = service.get(uid);
            String response = JsonUtil.toJson(user);

            exchange.getResponseHeaders().put(CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send(response, UTF_8);
        };
    }

    private HttpHandler getAllUsers () {
        return (exchange) -> {
            Collection<User> users = service.getAll();
            String response = JsonUtil.toJson(users);

            exchange.getResponseHeaders().put(CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send(response, UTF_8);
        };
    }

    private HttpHandler deleteUser () {
        return (exchange) -> {
            String uid = exchange.getQueryParameters()
                    .get("id")
                    .getFirst();

            service.delete(uid);

            exchange.setStatusCode(NO_CONTENT);
        };
    }
}
