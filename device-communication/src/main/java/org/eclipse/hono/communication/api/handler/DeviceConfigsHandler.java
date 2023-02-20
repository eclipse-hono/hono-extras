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

import io.vertx.core.Future;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.openapi.RouterBuilder;
import org.eclipse.hono.communication.api.config.DeviceConfigsConstants;
import org.eclipse.hono.communication.api.data.DeviceConfigRequest;
import org.eclipse.hono.communication.api.data.DeviceConfigResponse;
import org.eclipse.hono.communication.api.data.ListDeviceConfigVersionsResponse;
import org.eclipse.hono.communication.api.service.config.DeviceConfigService;
import org.eclipse.hono.communication.core.http.HttpEndpointHandler;
import org.eclipse.hono.communication.core.utils.ResponseUtils;

import javax.enterprise.context.ApplicationScoped;

/**
 * Handler for device config endpoints
 */
@ApplicationScoped
public class DeviceConfigsHandler implements HttpEndpointHandler {

    private final DeviceConfigService configService;

    public DeviceConfigsHandler(DeviceConfigService configService) {
        this.configService = configService;
    }


    @Override
    public void addRoutes(RouterBuilder routerBuilder) {
        routerBuilder.operation(DeviceConfigsConstants.LIST_CONFIG_VERSIONS_OP_ID)
                .handler(this::handleListConfigVersions);
        routerBuilder.operation(DeviceConfigsConstants.POST_MODIFY_DEVICE_CONFIG_OP_ID)
                .handler(this::handleModifyCloudToDeviceConfig);
    }

    public Future<DeviceConfigResponse> handleModifyCloudToDeviceConfig(RoutingContext routingContext) {
        var tenantId = routingContext.pathParam(DeviceConfigsConstants.TENANT_PATH_PARAMS);
        var deviceId = routingContext.pathParam(DeviceConfigsConstants.DEVICE_PATH_PARAMS);

        final DeviceConfigRequest deviceConfig = routingContext.body()
                .asJsonObject()
                .mapTo(DeviceConfigRequest.class);

        return configService.modifyCloudToDeviceConfig(deviceConfig, deviceId, tenantId)
                .onSuccess(result -> ResponseUtils.successResponse(routingContext, result))
                .onFailure(err -> ResponseUtils.errorResponse(routingContext, err));
    }

    public Future<ListDeviceConfigVersionsResponse> handleListConfigVersions(RoutingContext routingContext) {
        var numVersions = routingContext.queryParams().get(DeviceConfigsConstants.NUM_VERSION_QUERY_PARAMS);

        var limit = numVersions == null ? 0 : Integer.parseInt(numVersions);
        var tenantId = routingContext.pathParam(DeviceConfigsConstants.TENANT_PATH_PARAMS);
        var deviceId = routingContext.pathParam(DeviceConfigsConstants.DEVICE_PATH_PARAMS);

        return configService.listAll(deviceId, tenantId, limit)
                .onSuccess(result -> ResponseUtils.successResponse(routingContext, result))
                .onFailure(err -> ResponseUtils.errorResponse(routingContext, err));
    }
}
