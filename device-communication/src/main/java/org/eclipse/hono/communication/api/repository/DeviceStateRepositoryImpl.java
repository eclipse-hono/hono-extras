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

package org.eclipse.hono.communication.api.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.hono.communication.api.data.DeviceState;
import org.eclipse.hono.communication.api.data.DeviceStateEntity;
import org.eclipse.hono.communication.api.exception.DeviceNotFoundException;
import org.eclipse.hono.communication.api.service.database.DatabaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.Future;
import io.vertx.sqlclient.RowIterator;
import io.vertx.sqlclient.SqlConnection;
import io.vertx.sqlclient.templates.RowMapper;
import io.vertx.sqlclient.templates.SqlTemplate;

/**
 * Repository class for making CRUD operations for device state entities.
 */
@ApplicationScoped
public class DeviceStateRepositoryImpl implements DeviceStateRepository {

    private static final Logger log = LoggerFactory.getLogger(DeviceStateRepositoryImpl.class);
    private final String SQL_COUNT_STATES_WITH_PK_FILTER = "SELECT COUNT(*) AS total FROM device_status WHERE tenant_id = #{tenantId} AND device_id = #{deviceId}";
    private final String SQL_INSERT = "INSERT INTO device_status (id, tenant_id, device_id, update_time, binary_data) "
            +
            "VALUES (#{id}, #{tenantId}, #{deviceId}, #{updateTime}, #{binaryData}) RETURNING id";
    private final String SQL_LIST = "SELECT update_time, binary_data FROM device_status " +
            "WHERE device_id = #{deviceId} and tenant_id = #{tenantId} ORDER BY update_time DESC LIMIT #{limit}";

    private final String SQL_DELETE = "DELETE FROM device_status WHERE device_id = #{deviceId} AND tenant_id = #{tenantId} AND id NOT IN "
            +
            "(SELECT id FROM device_status WHERE device_id = #{deviceId} AND tenant_id = #{tenantId} ORDER BY update_time DESC LIMIT 9)";
    private final String deviceIdKey = "deviceId";
    private final String tenantIdKey = "tenantId";
    private final int MAX_LIMIT = 10;
    private final DatabaseService db;
    private final DeviceRepository deviceRepository;

    /**
     * Creates a new DeviceStateRepositoryImpl.
     *
     * @param db The database connection
     * @param deviceRepository The device repository interface
     */
    public DeviceStateRepositoryImpl(final DatabaseService db,
            final DeviceRepository deviceRepository) {
        this.db = db;
        this.deviceRepository = deviceRepository;
    }

    @Override
    public Future<List<DeviceState>> listAll(final String deviceId, final String tenantId, final int limit) {
        final int queryLimit = limit == 0 ? MAX_LIMIT : limit;
        return db.getDbClient().withConnection(
                sqlConnection -> deviceRepository.searchForDevice(deviceId, tenantId)
                        .compose(
                                counter -> {
                                    if (counter < 1) {
                                        throw new DeviceNotFoundException(
                                                String.format("Device with id %s and tenant id %s doesn't exist",
                                                        deviceId,
                                                        tenantId));
                                    }
                                    return SqlTemplate
                                            .forQuery(sqlConnection, SQL_LIST)
                                            .mapTo(DeviceStateEntity.class)
                                            .execute(Map.of(deviceIdKey, deviceId, tenantIdKey, tenantId, "limit",
                                                    queryLimit))
                                            .map(rowSet -> {
                                                final List<DeviceState> states = new ArrayList<>();
                                                rowSet.forEach(
                                                        entity -> states.add(new DeviceState(entity.getUpdateTime(),
                                                                entity.getBinaryData())));
                                                return states;
                                            })
                                            .onSuccess(success -> log.debug(
                                                    String.format("Listing all states for device %s and tenant %s",
                                                            deviceId, tenantId)))
                                            .onFailure(throwable -> log.error("Error: {}", throwable.getMessage()));
                                }));
    }

    @Override
    public Future<List<String>> listTenants() {
        return deviceRepository.listDistinctTenants();
    }

    @Override
    public Future<DeviceStateEntity> createNew(final DeviceStateEntity entity) {
        return db.getDbClient().withTransaction(
                sqlConnection -> deviceRepository.searchForDevice(entity.getDeviceId(), entity.getTenantId())
                        .compose(
                                deviceCounter -> {
                                    if (deviceCounter < 1) {
                                        throw new DeviceNotFoundException(
                                                String.format(
                                                        "Device with id %s and tenant id %s doesn't exist",
                                                        entity.getDeviceId(),
                                                        entity.getTenantId()));
                                    }
                                    return countStates(entity.getDeviceId(), entity.getTenantId()).compose(
                                            stateCounter -> {
                                                if (stateCounter >= 10) {
                                                    deleteStates(sqlConnection, entity);
                                                }
                                                return insert(sqlConnection, entity);
                                            });
                                })
                        .onFailure(error -> log.error(error.getMessage())));
    }

    private Future<Integer> countStates(final String deviceId, final String tenantId) {
        final RowMapper<Integer> rowMapper = row -> row.getInteger("total");
        return db.getDbClient().withConnection(
                sqlConnection -> SqlTemplate
                        .forQuery(sqlConnection, SQL_COUNT_STATES_WITH_PK_FILTER)
                        .mapTo(rowMapper)
                        .execute(Map.of(deviceIdKey, deviceId, tenantIdKey, tenantId)).map(rowSet -> {
                            final RowIterator<Integer> iterator = rowSet.iterator();
                            return iterator.next();
                        }));
    }

    private void deleteStates(final SqlConnection sqlConnection, final DeviceStateEntity entity) {
        SqlTemplate.forQuery(sqlConnection, SQL_DELETE)
                .execute(Map.of(deviceIdKey, entity.getDeviceId(), tenantIdKey, entity.getTenantId()));
    }

    /**
     * Inserts a new entity in to the db.
     *
     * @param sqlConnection The sql connection instance.
     * @param entity The instance to insert.
     * @return A Future of the created DeviceStateEntity.
     */
    private Future<DeviceStateEntity> insert(final SqlConnection sqlConnection, final DeviceStateEntity entity) {
        entity.setId(UUID.randomUUID().toString());
        return SqlTemplate
                .forUpdate(sqlConnection, SQL_INSERT)
                .mapFrom(DeviceStateEntity.class)
                .mapTo(DeviceStateEntity.class)
                .execute(entity)
                .map(rowSet -> {
                    final RowIterator<DeviceStateEntity> iterator = rowSet.iterator();
                    if (iterator.hasNext()) {
                        return entity;
                    } else {
                        throw new IllegalStateException(String.format("Can't create device state: %s", entity));
                    }
                })
                .onSuccess(success -> log.debug("Device state created successfully: {}", success))
                .onFailure(throwable -> log.error(throwable.getMessage()));

    }
}
