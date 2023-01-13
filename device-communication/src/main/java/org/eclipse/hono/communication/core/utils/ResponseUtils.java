/*
 * ***********************************************************
 *  Copyright (c) 2023 Contributors to the Eclipse Foundation
 *  <p>
 *  See the NOTICE file(s) distributed with this work for additional
 *  information regarding copyright ownership.
 *  <p>
 *  This program and the accompanying materials are made available under the
 *  terms of the Eclipse Public License 2.0 which is available at
 *  http://www.eclipse.org/legal/epl-2.0
 *  <p>
 *  SPDX-License-Identifier: EPL-2.0
 * **********************************************************
 *
 */

package org.eclipse.hono.communication.core.utils;

import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.validation.BadRequestException;

import java.util.NoSuchElementException;

/**
 * HTTP Response utilities class
 */
public abstract class ResponseUtils {
    private static final String CONTENT_TYPE_HEADER = "Content-Type";
    private static final String APPLICATION_JSON_TYPE = "application/json";


    public static void successResponse(RoutingContext rc,
                                       Object response) {
        rc.response()
                .setStatusCode(200)
                .putHeader(CONTENT_TYPE_HEADER, APPLICATION_JSON_TYPE)
                .end(Json.encodePrettily(response));
    }

    /**
     * Build success response using 201 Created as its status code and response as its body
     *
     * @param rc       Routing context
     * @param response Response body
     */
    public static void createdResponse(RoutingContext rc,
                                       Object response) {
        rc.response()
                .setStatusCode(201)
                .putHeader(CONTENT_TYPE_HEADER, APPLICATION_JSON_TYPE)
                .end(Json.encodePrettily(response));
    }

    /**
     * Build success response using 204 No Content as its status code and no body
     *
     * @param rc Routing context
     */
    public static void noContentResponse(RoutingContext rc) {
        rc.response()
                .setStatusCode(204)
                .end();
    }

    /**
     * Build error response using 400 Bad Request, 404 Not Found or 500 Internal Server Error
     * as its status code and throwable as its body
     *
     * @param rc    Routing context
     * @param error Throwable exception
     */
    public static void errorResponse(RoutingContext rc, Throwable error) {
        final int status;
        final String message;


        if (error instanceof IllegalArgumentException
                || error instanceof IllegalStateException
                || error instanceof NullPointerException
                || error instanceof BadRequestException) {
            
            // Bad Request
            status = 400;
            message = error.getMessage();
        } else if (error instanceof NoSuchElementException) {
            // Not Found
            status = 404;
            message = error.getMessage();
        } else {
            // Internal Server Error
            status = 500;
            message = "Internal Server Error";
        }

        rc.response()
                .setStatusCode(status)
                .putHeader(CONTENT_TYPE_HEADER, APPLICATION_JSON_TYPE)
                .end(new JsonObject().put("error", message).encodePrettily());
    }


}
