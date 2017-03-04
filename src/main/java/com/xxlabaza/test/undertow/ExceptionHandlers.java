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

import static io.undertow.server.handlers.ExceptionHandler.THROWABLE;
import static io.undertow.util.StatusCodes.INTERNAL_SERVER_ERROR;

import com.xxlabaza.test.undertow.user.UserController;
import io.undertow.Handlers;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.ExceptionHandler;
import lombok.extern.java.Log;

/**
 * @author Artem Labazin <xxlabaza@gmail.com>
 * @since 02.03.2017
 */
@Log
public final class ExceptionHandlers {

    public static ExceptionHandler wrap (HttpHandler httpHandler) {
        ExceptionHandler exceptionHandler = Handlers.exceptionHandler(httpHandler);

        UserController.exceptionHandlers(exceptionHandler);

        return exceptionHandler
                .addExceptionHandler(Throwable.class, (exchange) -> {
                                 Throwable exception = exchange.getAttachment(THROWABLE);
                                 exception.printStackTrace();
                                 exchange.setStatusCode(INTERNAL_SERVER_ERROR);
                                 exchange.setReasonPhrase("Oooops...");
                             });
    }

    private ExceptionHandlers () {
    }
}
