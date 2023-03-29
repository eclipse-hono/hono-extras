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

import java.util.Map;

import javax.inject.Singleton;

import org.eclipse.hono.communication.api.data.DeviceCommandRequest;
import org.eclipse.hono.communication.api.exception.DeviceNotFoundException;
import org.eclipse.hono.communication.api.repository.DeviceRepository;
import org.eclipse.hono.communication.api.service.DeviceServiceAbstract;
import org.eclipse.hono.communication.api.service.communication.InternalMessaging;
import org.eclipse.hono.communication.core.app.InternalMessagingConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

import io.vertx.core.Future;


/**
 * Service for device commands.
 */
@Singleton
public class DeviceCommandServiceImpl extends DeviceServiceAbstract implements DeviceCommandService {
    public static final String DEVICE_ID = "deviceId";
    public static final String TENANT_ID = "tenantId";
    public static final String SUBJECT = "subject";
    private final Logger log = LoggerFactory.getLogger(DeviceCommandServiceImpl.class);
    private final DeviceRepository deviceRepository;

    /**
     * Creates a new DeviceCommandServiceImpl.
     *
     * @param deviceRepository  The device repository interface
     * @param internalMessaging The internal messaging interface
     * @param messagingConfig   The internal messaging configs
     */
    public DeviceCommandServiceImpl(final DeviceRepository deviceRepository,
                                    final InternalMessaging internalMessaging,
                                    final InternalMessagingConfig messagingConfig) {

        super(messagingConfig, internalMessaging);
        this.deviceRepository = deviceRepository;
    }

    @Override
    public Future<Void> postCommand(final DeviceCommandRequest commandRequest, final String tenantId, final String deviceId) {
        return deviceRepository.searchForDevice(deviceId, tenantId)
                .compose(
                        counter -> {

                            if (counter < 1) {
                                throw new DeviceNotFoundException(String.format("Device with id %s and tenant id %s doesn't exist",
                                        deviceId,
                                        tenantId));
                            }
                            final String subject = Strings.isNullOrEmpty(commandRequest.getSubfolder()) ? "command" : commandRequest.getSubfolder();
                            final var topic = String.format(messagingConfig.getCommandTopicFormat(), tenantId);
                            final Map<String, String> attributes = Map.of(DEVICE_ID, deviceId, TENANT_ID, tenantId, SUBJECT, subject);
                            try {
                                final String commandJson = ow.writeValueAsString(commandRequest.getBinaryData());
                                internalMessaging.publish(topic, commandJson, attributes);
                                log.info("Command {} was published successfully to topic {}", commandJson, topic);
                            } catch (Exception ex) {
                                log.error("Command can't be published: {}", ex.getMessage());
                                return Future.failedFuture(ex);

                            }
                            return Future.succeededFuture();

                        }

                );

    }
}
