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

package org.eclipse.hono.communication.api;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.eclipse.hono.communication.core.app.ApplicationConfig;
import org.eclipse.hono.communication.core.http.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import io.vertx.core.Vertx;


class ApplicationTest {

    private HttpServer httpServerMock;
    private Application application;

    @BeforeEach
    void setUp() {
        httpServerMock = mock(HttpServer.class);
        final Vertx vertxMock = mock(Vertx.class);
        final ApplicationConfig appConfigs = mock(ApplicationConfig.class);
        application = new Application(vertxMock, appConfigs, httpServerMock);

        verifyNoMoreInteractions(httpServerMock, appConfigs, vertxMock);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void doStart() {
        doNothing().when(httpServerMock).start();

        application.doStart();

        verify(httpServerMock, times(1)).start();

    }
}
