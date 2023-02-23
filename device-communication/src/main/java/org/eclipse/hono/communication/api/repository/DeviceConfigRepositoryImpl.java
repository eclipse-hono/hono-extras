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

import com.google.common.base.Strings;
import io.vertx.core.Future;
import io.vertx.sqlclient.RowIterator;
import io.vertx.sqlclient.SqlConnection;
import io.vertx.sqlclient.templates.RowMapper;
import io.vertx.sqlclient.templates.SqlTemplate;
import org.eclipse.hono.communication.api.data.DeviceConfig;
import org.eclipse.hono.communication.api.data.DeviceConfigAckResponse;
import org.eclipse.hono.communication.api.data.DeviceConfigEntity;
import org.eclipse.hono.communication.api.exception.DeviceNotFoundException;
import org.eclipse.hono.communication.api.service.database.DatabaseService;
import org.eclipse.hono.communication.core.app.DatabaseConfig;
import org.graalvm.collections.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.util.*;

/**
 * Repository class for making CRUD operations for device config entities
 */
@ApplicationScoped
public class DeviceConfigRepositoryImpl implements DeviceConfigRepository {
    private final static String SQL_INSERT = "INSERT INTO device_configs (version, tenant_id, device_id, cloud_update_time, device_ack_time, binary_data) " +
            "VALUES (#{version}, #{tenantId}, #{deviceId}, #{cloudUpdateTime}, #{deviceAckTime}, #{binaryData}) RETURNING version";
    private final static String SQL_LIST = "SELECT version, cloud_update_time, device_ack_time, binary_data " +
            "FROM device_configs WHERE device_id = #{deviceId} and tenant_id =  #{tenantId} ORDER BY version DESC LIMIT #{limit}";
    private final static String SQL_DELETE_MIN_VERSION = "DELETE FROM device_configs WHERE device_id = #{deviceId} and tenant_id =  #{tenantId} " +
            "and version = (SELECT MIN(version) from  device_configs WHERE device_id = #{deviceId} and tenant_id =  #{tenantId})  RETURNING version";
    private final static String SQL_FIND_TOTAL_AND_MAX_VERSION = "SELECT COALESCE(COUNT(*), 0) as total, COALESCE(MAX(version), 0) as max_version from device_configs " +
            "WHERE device_id = #{deviceId} and tenant_id =  #{tenantId}";

    private final static String SQL_UPDATE_DEVICE_ACK_TIME = "UPDATE device_configs SET device_ack_time = #{deviceAckTime} " +
            "WHERE tenant_id = #{tenantId} AND device_id = #{deviceId} AND version = #{version}";

    private final static String SQL_UPDATE_DEVICE_ACK_TIME_FOR_MAX_VERSION = "UPDATE device_config SET device_ack_time = #{deviceAckTime} " +
            "WHERE tenant_id = #{tenantId} AND device_id = #{deviceId} AND version = " +
            "(SELECT MAX(version) FROM device_config WHERE tenant_id = #{tenantId} AND device_id = #{deviceId})";
    private final static String SQL_FIND_DEVICE_LATEST_CONFIG = "SELECT * FROM device_config" +
            "WHERE tenant_id = #{tenantId} AND device_id = #{deviceId} AND version = #{version}";

    private final static int MAX_LIMIT = 10;
    private final static Logger log = LoggerFactory.getLogger(DeviceConfigRepositoryImpl.class);

    private final DatabaseConfig databaseConfig;
    private final DatabaseService db;

    private final DeviceRepository deviceRepository;

    public DeviceConfigRepositoryImpl(DatabaseService db,
                                      DatabaseConfig databaseConfig,
                                      DeviceRepository deviceRepository) {

        this.databaseConfig = databaseConfig;
        this.db = db;
        this.deviceRepository = deviceRepository;
    }


    private Future<Pair<Integer, Integer>> findMaxVersionAndTotalEntries(SqlConnection sqlConnection, String deviceId, String tenantId) {
        final RowMapper<Pair<Integer, Integer>> ROW_MAPPER = row ->
                Pair.create(row.getInteger("total"), row.getInteger("max_version"));
        return SqlTemplate
                .forQuery(sqlConnection, SQL_FIND_TOTAL_AND_MAX_VERSION)
                .mapTo(ROW_MAPPER)
                .execute(Map.of("deviceId", deviceId, "tenantId", tenantId)).map(rowSet -> {
                    final RowIterator<Pair<Integer, Integer>> iterator = rowSet.iterator();
                    return iterator.next();
                });

    }

    /**
     * Lists all config versions for a specific device. Result is order by version desc
     *
     * @param deviceId The device id
     * @param tenantId The tenant id
     * @param limit    The number of config to show
     * @return A Future with a List of DeviceConfigs
     */
    public Future<List<DeviceConfig>> listAll(String deviceId, String tenantId, int limit) {
        int queryLimit = limit == 0 ? MAX_LIMIT : limit;
        return db.getDbClient().withConnection(
                sqlConnection -> deviceRepository.searchForDevice(deviceId, tenantId)
                        .compose(
                                counter -> {
                                    if (counter < 1) {
                                        throw new DeviceNotFoundException(String.format("Device with id %s and tenant id %s doesn't exist",
                                                deviceId,
                                                tenantId));
                                    }
                                    return SqlTemplate
                                            .forQuery(sqlConnection, SQL_LIST)
                                            .mapTo(DeviceConfig.class)
                                            .execute(Map.of("deviceId", deviceId, "tenantId", tenantId, "limit", queryLimit))
                                            .map(rowSet -> {
                                                final List<DeviceConfig> configs = new ArrayList<>();
                                                rowSet.forEach(configs::add);
                                                return configs;
                                            })
                                            .onSuccess(success -> log.info(
                                                    String.format("Listing all configs for device %s and tenant %s",
                                                            deviceId, tenantId)))
                                            .onFailure(throwable -> log.error("Error: {}", throwable.getMessage()));
                                }));
    }


    /**
     * Inserts a new entity in to the db.
     *
     * @param sqlConnection The sql connection instance
     * @param entity        The instance to insert
     * @return A Future of the created DeviceConfigEntity
     */
    private Future<DeviceConfigEntity> insert(SqlConnection sqlConnection, DeviceConfigEntity entity) {
        return SqlTemplate
                .forUpdate(sqlConnection, SQL_INSERT)
                .mapFrom(DeviceConfigEntity.class)
                .mapTo(DeviceConfigEntity.class)
                .execute(entity)
                .map(rowSet -> {
                    final RowIterator<DeviceConfigEntity> iterator = rowSet.iterator();
                    if (iterator.hasNext()) {
                        entity.setVersion(iterator.next().getVersion());
                        return entity;
                    } else {
                        throw new IllegalStateException(String.format("Can't create device config: %s", entity));
                    }
                })
                .onSuccess(success -> log.info("Device config created successfully: {}", success.toString()))
                .onFailure(throwable -> log.error(throwable.getMessage()));

    }

    /**
     * Delete the smallest config version
     *
     * @param sqlConnection The sql connection instance
     * @param entity        The device config for searching and deleting the smallest version
     * @return A Future of the deleted version
     */

    private Future<Integer> deleteMinVersion(SqlConnection sqlConnection, DeviceConfigEntity entity) {
        final RowMapper<Integer> ROW_MAPPER = row -> row.getInteger("version");
        return SqlTemplate
                .forQuery(sqlConnection, SQL_DELETE_MIN_VERSION)
                .mapFrom(DeviceConfigEntity.class)
                .mapTo(ROW_MAPPER)
                .execute(entity)
                .map(rowSet -> {
                    final RowIterator<Integer> iterator = rowSet.iterator();
                    return iterator.next();
                })
                .onSuccess(deletedVersion -> log.info("Device config version {} was deleted", deletedVersion));
    }


    /**
     * Creates a new config version and deletes the oldest version if the total num of versions in DB is bigger than the MAX_LIMIT.
     *
     * @param entity The instance to insert
     * @return A Future of the created DeviceConfigEntity
     */
    public Future<DeviceConfigEntity> createNew(DeviceConfigEntity entity) {
        return db.getDbClient().withTransaction(
                sqlConnection -> deviceRepository.searchForDevice(entity.getDeviceId(), entity.getTenantId())
                        .compose(
                                counter -> {
                                    if (counter < 1) {
                                        throw new DeviceNotFoundException(String.format("Device with id %s and tenant id %s doesn't exist",
                                                entity.getDeviceId(),
                                                entity.getTenantId()));
                                    }
                                    return findMaxVersionAndTotalEntries(sqlConnection, entity.getDeviceId(), entity.getTenantId())
                                            .compose(
                                                    values -> {
                                                        int total = values.getLeft();
                                                        int maxVersion = values.getRight();

                                                        entity.setVersion(maxVersion + 1);

                                                        if (total > MAX_LIMIT - 1) {
                                                            return deleteMinVersion(sqlConnection, entity).compose(
                                                                    ok -> insert(sqlConnection, entity)

                                                            );
                                                        }
                                                        return insert(sqlConnection, entity);
                                                    }
                                            );
                                }).onFailure(error -> log.error(error.getMessage())));
    }

    /**
     * Update the deviceAckTime field
     *
     * @param ack           The acknowledgment object
     * @param deviceAckTime The ack Time
     * @return Future of Void
     */
    @Override
    public Future<Void> updateDeviceAckTime(DeviceConfigAckResponse ack, String deviceAckTime) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("deviceId", ack.getDeviceId());
        parameters.put("tenantId", ack.getTenantId());
        parameters.put("deviceAckTime", deviceAckTime);
        String sqlCommand;
        if (Strings.isNullOrEmpty(ack.getVersion())) {
            sqlCommand = SQL_UPDATE_DEVICE_ACK_TIME_FOR_MAX_VERSION;
        } else {
            parameters.put("version", Integer.parseInt(ack.getVersion()));
            sqlCommand = SQL_UPDATE_DEVICE_ACK_TIME;
        }

        return db.getDbClient().withTransaction(
                sqlConnection -> SqlTemplate
                        .forQuery(sqlConnection, sqlCommand)
                        .execute(parameters)
                        .flatMap(rowSet -> {
                            if (rowSet.rowCount() > 0) {
                                return Future.succeededFuture();
                            } else {
                                var msg = "Device doesn't exist: %s".formatted(ack.toString());
                                log.error(msg);
                                throw new NoSuchElementException(msg);
                            }
                        }));
    }

    @Override
    public Future<DeviceConfigEntity> getDeviceLatestConfig(String deviceId, String tenantId) {

        return db.getDbClient().withConnection(
                sqlConnection -> findMaxVersionAndTotalEntries(sqlConnection, deviceId, tenantId)
                        .compose(
                                values -> {
                                    int total = values.getLeft();
                                    int maxVersion = values.getRight();

                                    if (total == 0) {
                                        return Future.failedFuture(new NoSuchElementException("No configs are found for device %s and tenant %s".formatted(deviceId, tenantId)));
                                    }
                                    Map<String, Object> parameters = new HashMap<>();
                                    parameters.put("deviceId", deviceId);
                                    parameters.put("tenantId", tenantId);
                                    parameters.put("version", maxVersion);

                                    return SqlTemplate
                                            .forQuery(sqlConnection, SQL_FIND_DEVICE_LATEST_CONFIG)
                                            .mapTo(DeviceConfigEntity.class)
                                            .execute(parameters).map(rowSet -> {
                                                final RowIterator<DeviceConfigEntity> iterator = rowSet.iterator();
                                                return iterator.next();

                                            });

                                }

                        ));
    }


    /**
     * List all distinct tenant ID's from device registrations table
     *
     * @return Future of List with all tenants
     */
    public Future<List<String>> listTenants() {
        return deviceRepository.listDistinctTenants();
    }
}