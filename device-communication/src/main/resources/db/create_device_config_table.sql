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


CREATE TABLE IF NOT EXISTS "deviceConfig"
(
    version           INT          not null,
    "tenantId"        VARCHAR(100) not null,
    "deviceId"        VARCHAR(100) not null,
    "cloudUpdateTime" VARCHAR(100) not null,
    "deviceAckTime"   VARCHAR(100),
    "binaryData"      VARCHAR      not null,

    PRIMARY KEY (version, "tenantId", "deviceId")
)