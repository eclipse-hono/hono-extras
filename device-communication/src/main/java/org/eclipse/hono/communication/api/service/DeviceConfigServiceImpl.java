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

package org.eclipse.hono.communication.api.service;


import io.vertx.core.Future;
import org.eclipse.hono.communication.api.data.DeviceConfigRequest;
import org.eclipse.hono.communication.api.data.DeviceConfigResponse;
import org.eclipse.hono.communication.api.data.ListDeviceConfigVersionsResponse;
import org.eclipse.hono.communication.api.mapper.DeviceConfigMapper;
import org.eclipse.hono.communication.api.repository.DeviceConfigsRepository;

import javax.enterprise.context.ApplicationScoped;


/**
 * Service for device commands
 */

@ApplicationScoped
public class DeviceConfigServiceImpl implements DeviceConfigService {

    private final DeviceConfigsRepository repository;
    private final DatabaseService db;
    private final DeviceConfigMapper mapper;

    public DeviceConfigServiceImpl(DeviceConfigsRepository repository,
                                   DatabaseService db,
                                   DeviceConfigMapper mapper) {

        this.repository = repository;
        this.db = db;
        this.mapper = mapper;
    }

    public Future<DeviceConfigResponse> modifyCloudToDeviceConfig(DeviceConfigRequest deviceConfig, String deviceId, String tenantId) {

        var entity = mapper.configRequestToDeviceConfigEntity(deviceConfig);
        entity.setDeviceId(deviceId);
        entity.setTenantId(tenantId);

        return db.getDbClient().withTransaction(
                        sqlConnection ->
                                repository.createNew(sqlConnection, entity))
                .map(mapper::deviceConfigEntityToResponse);
    }

    public Future<ListDeviceConfigVersionsResponse> listAll(String deviceId, String tenantId, int limit) {
        return db.getDbClient().withTransaction(
                sqlConnection -> repository.listAll(sqlConnection, deviceId, tenantId, limit)
                        .map(
                                result -> {
                                    var listConfig = new ListDeviceConfigVersionsResponse();
                                    listConfig.setDeviceConfigs(result);
                                    return listConfig;
                                }
                        )
        );

    }
}
