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

/**
 * Device commands interface
 */
public interface DeviceCommandService {
    Future<Void> postCommand(DeviceCommandRequest commandRequest, String tenantId, String deviceId);
}