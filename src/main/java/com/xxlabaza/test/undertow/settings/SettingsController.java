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
import java.util.Map;

/**
 * @author Artem Labazin <xxlabaza@gmail.com>
 * @since 28.02.2017
 */
public class SettingsController {

    public static RoutingHandler handlers () {
        Predicate containsUid =
                (exchange) -> exchange.getQueryParameters().containsKey("uid");

        Predicate noQueryParameters =
                (exchange) -> exchange.getQueryParameters().isEmpty();

        Predicate containsUidAndKey =
                (exchange) -> exchange.getQueryParameters().containsKey("uid") &&
                                exchange.getQueryParameters().containsKey("key");

        SettingsController controller = new SettingsController();
        return Handlers.routing()
                .post("/settings/{uid}", containsUid, controller.addUserSetting())
                .get("/settings", noQueryParameters, controller.getAllSettings())
                .get("/settings/{uid}", containsUid, controller.getUserSettings())
                .delete("/settings/{uid}/{key}", containsUidAndKey, controller.deleteUserSetting());
    }

    private final SettingsService service;

    private SettingsController () {
        service = SettingsService.getInstance();
    }

    private HttpHandler addUserSetting () {
        FullStringCallback callback = (exchange, payload) -> {
            String uid = exchange.getQueryParameters()
                    .get("uid")
                    .getFirst();

            SettingDto dto = JsonUtil.fromJson(payload, SettingDto.class);
            Map<String, String> settings = service.create(uid, dto.getKey(), dto.getValue());

            String response = JsonUtil.toJson(settings);

            exchange.setStatusCode(CREATED);
            exchange.getResponseHeaders().put(CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send(response, UTF_8);
        };

        return (exchange) -> exchange.getRequestReceiver().receiveFullString(callback, UTF_8);
    }

    private HttpHandler getAllSettings () {
        return (exchange) -> {
            Map<String, Map<String, String>> settings = service.findAll();
            String response = JsonUtil.toJson(settings);

            exchange.getResponseHeaders().put(CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send(response, UTF_8);
        };
    }

    private HttpHandler getUserSettings () {
        return (exchange) -> {
            String uid = exchange.getQueryParameters()
                    .get("uid")
                    .getFirst();

            Map<String, String> settings = service.findAll(uid);
            String response = JsonUtil.toJson(settings);

            exchange.getResponseHeaders().put(CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send(response, UTF_8);
        };
    }

    private HttpHandler deleteUserSetting () {
        return (exchange) -> {
            String uid = exchange.getQueryParameters()
                    .get("uid")
                    .getFirst();
            String key = exchange.getQueryParameters()
                    .get("key")
                    .getFirst();

            service.delete(uid, key);

            exchange.setStatusCode(NO_CONTENT);
        };
    }
}
