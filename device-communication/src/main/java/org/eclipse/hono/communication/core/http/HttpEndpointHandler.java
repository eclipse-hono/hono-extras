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

package org.eclipse.hono.communication.core.http;

import io.vertx.ext.web.openapi.RouterBuilder;

/**
 * An vertx endpoint that handles HTTP requests.
 */
public interface HttpEndpointHandler {
    /**
     * Adds custom routes for handling requests that this endpoint can handle.
     *
     * @param router The router to add the routes to.
     */
    void addRoutes(RouterBuilder router);

}
