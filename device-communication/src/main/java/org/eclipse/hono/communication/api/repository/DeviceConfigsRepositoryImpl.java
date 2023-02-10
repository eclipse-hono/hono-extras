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

import io.vertx.core.Future;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.sqlclient.RowIterator;
import io.vertx.sqlclient.SqlConnection;
import io.vertx.sqlclient.templates.RowMapper;
import io.vertx.sqlclient.templates.SqlTemplate;
import org.eclipse.hono.communication.api.data.DeviceConfig;
import org.eclipse.hono.communication.api.data.DeviceConfigEntity;
import org.eclipse.hono.communication.core.app.DatabaseConfig;
import org.graalvm.collections.Pair;

import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Repository class for making CRUD operations for device config entities
 */
@ApplicationScoped
public class DeviceConfigsRepositoryImpl implements DeviceConfigsRepository {
    private final static String SQL_INSERT = "INSERT INTO \"deviceConfig\" (version, \"tenantId\", \"deviceId\", \"cloudUpdateTime\", \"deviceAckTime\", \"binaryData\") " +
            "VALUES (#{version}, #{tenantId}, #{deviceId}, #{cloudUpdateTime}, #{deviceAckTime}, #{binaryData}) RETURNING version";
    private final static String SQL_LIST = "SELECT version, \"cloudUpdateTime\", COALESCE(\"deviceAckTime\",'') AS  \"deviceAckTime\", \"binaryData\" " +
            "FROM \"deviceConfig\" WHERE \"deviceId\" = #{deviceId} and \"tenantId\" =  #{tenantId} ORDER BY version DESC LIMIT #{limit}";
    private final static String SQL_DELETE_MIN_VERSION = "DELETE FROM \"deviceConfig\" WHERE\"deviceId\" = #{deviceId} and \"tenantId\" =  #{tenantId} " +
            "and version = (SELECT MIN(version) from  \"deviceConfig\" WHERE \"deviceId\" = #{deviceId} and \"tenantId\" =  #{tenantId})  RETURNING version";
    private final static String SQL_FIND_TOTAL_AND_MAX_VERSION = "SELECT COALESCE(COUNT(*), 0) as total, COALESCE(MAX(version), 0) as max_version from \"deviceConfig\" " +
            "WHERE \"deviceId\" = #{deviceId} and \"tenantId\" =  #{tenantId}";

    private final static int MAX_LIMIT = 10;
    private final static Logger log = LoggerFactory.getLogger(DeviceConfigsRepositoryImpl.class);
    private String SQL_COUNT_DEVICES_WITH_PK_FILTER = "SELECT COUNT(*) as total FROM public.%s where %s = #{tenantId} and %s = #{deviceId}";


    public DeviceConfigsRepositoryImpl(DatabaseConfig databaseConfig) {

        SQL_COUNT_DEVICES_WITH_PK_FILTER = String.format(SQL_COUNT_DEVICES_WITH_PK_FILTER,
                databaseConfig.getDeviceRegistrationTableName(),
                databaseConfig.getDeviceRegistrationTenantIdColumn(),
                databaseConfig.getDeviceRegistrationDeviceIdColumn());
    }


    private Future<Integer> searchForDevice(SqlConnection sqlConnection, String deviceId, String tenantId) {
        final RowMapper<Integer> ROW_MAPPER = row -> row.getInteger("total");
        return SqlTemplate
                .forQuery(sqlConnection, SQL_COUNT_DEVICES_WITH_PK_FILTER)
                .mapTo(ROW_MAPPER)
                .execute(Map.of("deviceId", deviceId, "tenantId", tenantId)).map(rowSet -> {
                    final RowIterator<Integer> iterator = rowSet.iterator();
                    return iterator.next();
                });

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
     * @param sqlConnection The sql connection instance
     * @param deviceId      The device id
     * @param tenantId      The tenant id
     * @param limit         The number of config to show
     * @return A Future with a List of DeviceConfigs
     */
    public Future<List<DeviceConfig>> listAll(SqlConnection sqlConnection, String deviceId, String tenantId, int limit) {
        int queryLimit = limit == 0 ? MAX_LIMIT : limit;

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
                .onFailure(throwable -> log.error("Error: {}", throwable));
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
                .onSuccess(success -> log.info(String.format("Device config created successfully: %s", success.toString())))
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
                .onSuccess(deletedVersion -> log.info(String.format("Device config version %s was deleted", deletedVersion)));
    }


    /**
     * Creates a new config version and deletes the oldest version if the total num of versions in DB is bigger than the MAX_LIMIT.
     *
     * @param sqlConnection The sql connection instance
     * @param entity        The instance to insert
     * @return A Future of the created DeviceConfigEntity
     */
    public Future<DeviceConfigEntity> createNew(SqlConnection sqlConnection, DeviceConfigEntity entity) {
        return searchForDevice(sqlConnection, entity.getDeviceId(), entity.getTenantId())
                .compose(
                        counter -> {
                            if (counter < 1) {
                                throw new IllegalStateException(String.format("Device with id %s and tenant id %s doesn't exist",
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
                        }).onFailure(error -> log.error(error.getMessage()));
    }
}