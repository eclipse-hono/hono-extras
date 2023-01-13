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


import io.vertx.codegen.annotations.Nullable;
import io.vertx.core.Future;
import org.eclipse.hono.communication.api.entity.DeviceConfigEntity;
import org.eclipse.hono.communication.api.entity.DeviceConfigRequest;
import org.eclipse.hono.communication.api.entity.ListDeviceConfigVersionsResponse;
import org.eclipse.hono.communication.api.mapper.DeviceConfigMapper;
import org.eclipse.hono.communication.api.repository.DeviceConfigsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;


/**
 * Service for device commands
 */

@ApplicationScoped
public class DeviceConfigServiceImpl implements DeviceConfigService {
    private final Logger log = LoggerFactory.getLogger(DeviceConfigServiceImpl.class);

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

    public Future<@Nullable DeviceConfigEntity> modifyCloudToDeviceConfig(DeviceConfigRequest deviceConfig, String deviceId, String tenantId) {

        var entity = mapper.configRequestToDeviceConfigEntity(deviceConfig);
        entity.setDeviceId(deviceId);
        entity.setTenantId(tenantId);

        return db.getDbClient().withTransaction(
                        sqlConnection -> {
                            return repository.createOrUpdate(sqlConnection, entity);
                        })
                .onSuccess(success -> log.info(success.toString()))
                .onFailure(error -> log.error(error.getMessage()));
    }

    public Future<ListDeviceConfigVersionsResponse> listAll(String deviceId, String tenantId, int limit) {
        return db.getDbClient().withTransaction(
                        sqlConnection -> {
                            return repository.listAll(sqlConnection, deviceId, tenantId, limit)
                                    .map(
                                            result -> {
                                                var listConfig = new ListDeviceConfigVersionsResponse();
                                                listConfig.setDeviceConfigs(result);
                                                return listConfig;
                                            }
                                    );
                        }
                )
                .onSuccess(success -> log.info(success.getDeviceConfigs().toString()))
                .onFailure(error -> log.error(error.getMessage()));
    }
}
