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

package org.eclipse.hono.communication.api.service.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import io.vertx.core.Future;
import org.eclipse.hono.communication.api.data.DeviceCommandRequest;
import org.eclipse.hono.communication.api.exception.DeviceNotFoundException;
import org.eclipse.hono.communication.api.repository.DeviceRepository;
import org.eclipse.hono.communication.api.service.communication.InternalCommunication;
import org.eclipse.hono.communication.api.service.database.DatabaseService;
import org.eclipse.hono.communication.core.app.DatabaseConfig;
import org.eclipse.hono.communication.core.app.InternalCommunicationConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;

/**
 * Service for device commands
 */
@Singleton
public class DeviceCommandServiceImpl implements DeviceCommandService {
    private final static ObjectWriter ow = new ObjectMapper().writer();
    private final Logger log = LoggerFactory.getLogger(DeviceCommandServiceImpl.class);
    private final DatabaseService db;
    private final DatabaseConfig databaseConfig;
    private final DeviceRepository deviceRepository;
    private final InternalCommunication internalCommunication;
    private final InternalCommunicationConfig communicationConfig;

    public DeviceCommandServiceImpl(DatabaseService db,
                                    DatabaseConfig databaseConfig,
                                    DeviceRepository deviceRepository,
                                    InternalCommunication internalCommunication,
                                    InternalCommunicationConfig communicationConfig) {
        this.db = db;
        this.databaseConfig = databaseConfig;
        this.deviceRepository = deviceRepository;
        this.internalCommunication = internalCommunication;
        this.communicationConfig = communicationConfig;
    }

    /**
     * Handles device post commands
     *
     * @param commandRequest The command from request body
     * @param tenantId       Tenant id
     * @param deviceId       Device Id
     * @return Future of Void
     */
    public Future<Void> postCommand(DeviceCommandRequest commandRequest, String tenantId, String deviceId) {

        return db.getDbClient().withConnection(
                sqlConnection -> deviceRepository.searchForDevice(sqlConnection, deviceId, tenantId, databaseConfig)
                        .compose(
                                counter -> {

                                    if (counter < 1) {
                                        throw new DeviceNotFoundException(String.format("Device with id %s and tenant id %s doesn't exist",
                                                deviceId,
                                                tenantId));
                                    }
                                    var topic = String.format(communicationConfig.getCommandTopicFormat(), tenantId);
                                    try {
                                        String commandJson = ow.writeValueAsString(commandRequest);
                                        internalCommunication.publish(topic, commandJson);

                                    } catch (Exception ex) {
                                        log.error("Command can't be published: {}", ex.getMessage());

                                    }
                                    return Future.succeededFuture();

                                }
                        )
        );

    }
}
