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

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.hono.communication.api.data.DeviceConfig;
import org.eclipse.hono.communication.api.data.DeviceConfigRequest;
import org.eclipse.hono.communication.api.data.ListDeviceConfigVersionsResponse;
import org.eclipse.hono.communication.api.mapper.DeviceConfigMapper;
import org.eclipse.hono.communication.api.repository.DeviceConfigsRepository;

import io.vertx.core.Future;

/**
 * Service for device commands.
 */

@ApplicationScoped
public class DeviceConfigServiceImpl implements DeviceConfigService {

    private final DeviceConfigsRepository repository;
    private final DatabaseService db;
    private final DeviceConfigMapper mapper;

    /**
     * Creates a new DeviceConfigServiceImpl with all dependencies.
     *
     * @param repository The DeviceConfigsRepository
     * @param db         The database service
     * @param mapper     The DeviceConfigMapper
     */
    public DeviceConfigServiceImpl(final DeviceConfigsRepository repository,
                                   final DatabaseService db,
                                   final DeviceConfigMapper mapper) {

        this.repository = repository;
        this.db = db;
        this.mapper = mapper;
    }

    @Override
    public Future<DeviceConfig> modifyCloudToDeviceConfig(final DeviceConfigRequest deviceConfig, final String deviceId, final String tenantId) {

        final var entity = mapper.configRequestToDeviceConfigEntity(deviceConfig);
        entity.setDeviceId(deviceId);
        entity.setTenantId(tenantId);

        return db.getDbClient().withTransaction(
                        sqlConnection ->
                                repository.createNew(sqlConnection, entity))
                .map(mapper::deviceConfigEntityToConfig);
    }


    @Override
    public Future<ListDeviceConfigVersionsResponse> listAll(final String deviceId, final String tenantId, final int limit) {
        return db.getDbClient().withConnection(
                sqlConnection -> repository.listAll(sqlConnection, deviceId, tenantId, limit)
                        .map(
                                result -> {
                                    final var listConfig = new ListDeviceConfigVersionsResponse();
                                    listConfig.setDeviceConfigs(result);
                                    return listConfig;
                                }
                        )
        );

    }
}
