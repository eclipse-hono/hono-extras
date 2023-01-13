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

package org.eclipse.hono.communication.api.handler;

import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.openapi.RouterBuilder;
import org.eclipse.hono.communication.api.config.DeviceCommandConstants;
import org.eclipse.hono.communication.api.service.DeviceCommandService;
import org.eclipse.hono.communication.core.http.HttpEndpointHandler;

import javax.enterprise.context.ApplicationScoped;

/**
 * Handler for device command endpoints
 */
@ApplicationScoped
public class DeviceCommandsHandler implements HttpEndpointHandler {

    private final DeviceCommandService commandService;

    public DeviceCommandsHandler(DeviceCommandService commandService) {
        this.commandService = commandService;
    }

    @Override
    public void addRoutes(RouterBuilder routerBuilder) {
        routerBuilder.operation(DeviceCommandConstants.POST_DEVICE_COMMAND_OP_ID)
                .handler(this::handlePostCommand);
    }

    public void handlePostCommand(RoutingContext routingContext) {
        commandService.postCommand(routingContext);
        // publish command and send response
    }
}
