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

import io.vertx.core.Future;
import org.eclipse.hono.communication.api.data.DeviceCommandRequest;
import org.eclipse.hono.communication.api.exception.DeviceNotFoundException;
import org.eclipse.hono.communication.api.repository.DeviceRepository;
import org.eclipse.hono.communication.api.service.DeviceServiceAbstract;
import org.eclipse.hono.communication.api.service.communication.InternalMessaging;
import org.eclipse.hono.communication.core.app.InternalMessagingConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.util.Map;

/**
 * Service for device commands
 */
@Singleton
public class DeviceCommandServiceImpl extends DeviceServiceAbstract implements DeviceCommandService {
    private final Logger log = LoggerFactory.getLogger(DeviceCommandServiceImpl.class);
    private final DeviceRepository deviceRepository;

    public DeviceCommandServiceImpl(DeviceRepository deviceRepository,
                                    InternalMessaging internalMessaging,
                                    InternalMessagingConfig messagingConfig) {

        super(messagingConfig, internalMessaging);
        this.deviceRepository = deviceRepository;
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
        return deviceRepository.searchForDevice(deviceId, tenantId)
                .compose(
                        counter -> {

                            if (counter < 1) {
                                throw new DeviceNotFoundException(String.format("Device with id %s and tenant id %s doesn't exist",
                                        deviceId,
                                        tenantId));
                            }
                            var topic = String.format(messagingConfig.getCommandTopicFormat(), tenantId);
                            Map<String, String> attributes = Map.of("deviceId", deviceId, "tenantId", tenantId, "subject", "command");
                            try {
                                String commandJson = ow.writeValueAsString(commandRequest.getBinaryData());
                                internalMessaging.publish(topic, commandJson, attributes);
                                log.info("Command was published successfully");
                            } catch (Exception ex) {
                                log.error("Command can't be published: {}", ex.getMessage());
                                return Future.failedFuture(ex);

                            }
                            return Future.succeededFuture();

                        }

                );

    }
}
