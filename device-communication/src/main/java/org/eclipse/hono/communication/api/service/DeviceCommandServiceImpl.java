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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.ext.web.RoutingContext;

/**
 * Service for device commands.
 */
@ApplicationScoped
public class DeviceCommandServiceImpl implements DeviceCommandService {
    private final Logger log = LoggerFactory.getLogger(DeviceCommandServiceImpl.class);


    @Override
    public void postCommand(final RoutingContext routingContext) {
        // TODO publish command and send response
        log.info("postCommand received");
        routingContext.response().setStatusCode(501).end();
    }
}
