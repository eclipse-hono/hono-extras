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

import org.eclipse.hono.communication.api.exception.DeviceNotFoundException;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.validation.BadRequestException;


/**
 * HTTP Response utilities class.
 */
public abstract class ResponseUtils {
    private static final String CONTENT_TYPE_HEADER = "Content-Type";
    private static final String APPLICATION_JSON_TYPE = "application/json";

    private ResponseUtils() {
    }

    /**
     * Build success response using 200 as its status code and response object as body.
     *
     * @param rc       The routing context
     * @param response The response object
     */
    public static void successResponse(final RoutingContext rc,
                                       final Object response) {
        rc.response()
                .setStatusCode(HttpResponseStatus.OK.code())
                .putHeader(CONTENT_TYPE_HEADER, APPLICATION_JSON_TYPE)
                .end(Json.encodePrettily(response));
    }


    /**
     * Build error response using 400 Bad Request, 404 Not Found or 500 Internal Server Error
     * as its status code and throwable as its body.
     *
     * @param rc    Routing context
     * @param error Throwable exception
     */
    public static void errorResponse(final RoutingContext rc, final Throwable error) {
        final int status;
        final String message;


        if (error instanceof IllegalArgumentException
                || error instanceof IllegalStateException
                || error instanceof NullPointerException
                || error instanceof BadRequestException) {

            // Bad Request
            status = HttpResponseStatus.BAD_REQUEST.code();
            message = error.getMessage();
        } else if (error instanceof DeviceNotFoundException) {
            // Not Found
            status = HttpResponseStatus.NOT_FOUND.code();
            message = error.getMessage();
        } else {
            // Internal Server Error
            status = HttpResponseStatus.INTERNAL_SERVER_ERROR.code();
            if (error != null) {
                message = String.format("Internal Server Error: %s", error.getMessage());
            } else {
                message = "Internal Server Error";
            }

        }

        rc.response()
                .setStatusCode(status)
                .putHeader(CONTENT_TYPE_HEADER, APPLICATION_JSON_TYPE)
                .end(new JsonObject().put("error", message).encodePrettily());
    }


}
