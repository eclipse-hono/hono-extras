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

package org.eclipse.hono.communication.core.app;

import java.util.Arrays;
import java.util.Base64;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import javax.enterprise.event.Observes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import io.vertx.core.Closeable;
import io.vertx.core.Vertx;
import io.vertx.core.impl.VertxInternal;
import io.vertx.core.impl.cpu.CpuCoreSensor;
import io.vertx.core.json.impl.JsonUtil;


/**
 * Abstract Service application class.
 */
public abstract class AbstractServiceApplication {

    /**
     * YAML file application configurations properties.
     */
    protected final ApplicationConfig appConfigs;
    /**
     * The vert.x instance managed by Quarkus.
     */
    protected final Vertx vertx;
    private final Logger log = LoggerFactory.getLogger(AbstractServiceApplication.class);
    private Closeable addedVertxCloseHook;


    /**
     * Creates a new AbstractServiceApplication.
     *
     * @param vertx      The quarkus Vertx instance
     * @param appConfigs The application configs
     */
    public AbstractServiceApplication(final Vertx vertx,
                                      final ApplicationConfig appConfigs) {
        this.vertx = vertx;
        this.appConfigs = appConfigs;
    }

    /**
     * Logs information about the JVM.
     */
    protected void logJvmDetails() {
        if (log.isInfoEnabled()) {

            final String base64Encoder = Base64.getEncoder() == JsonUtil.BASE64_ENCODER ? "legacy" : "URL safe";

            log.info("""
                            running on Java VM [version: {}, name: {}, vendor: {}, max memory: {}MiB, processors: {}] \
                            with vert.x using {} Base64 encoder\
                            """,
                    System.getProperty("java.version"),
                    System.getProperty("java.vm.name"),
                    System.getProperty("java.vm.vendor"),
                    Runtime.getRuntime().maxMemory() >> 20,
                    CpuCoreSensor.availableProcessors(),
                    base64Encoder);
        }
    }

    /**
     * Registers a close hook that will be notified when the Vertx instance is being closed.
     */
    private void registerVertxCloseHook() {
        if (vertx instanceof VertxInternal vertxInternal) {
            final Closeable closeHook = completion -> {
                final var stackTrace = Thread.currentThread().getStackTrace();
                final String s = Arrays.stream(stackTrace)
                        .skip(2)
                        .map(element -> "\tat %s.%s(%s:%d)".formatted(
                                element.getClassName(),
                                element.getMethodName(),
                                element.getFileName(),
                                element.getLineNumber()))
                        .collect(Collectors.joining(System.lineSeparator()));
                log.warn("managed vert.x instance has been closed unexpectedly{}{}", System.lineSeparator(), s);
                this.doStop();
                completion.complete();
            };
            vertxInternal.addCloseHook(closeHook);
            addedVertxCloseHook = closeHook;
        } else {
            log.debug("Vertx instance is not a VertxInternal, skipping close hook registration");
        }
    }

    /**
     * Starts this component.
     * <p>
     * This implementation
     * <ol>
     * <li>logs the VM details,</li>
     * <li>invokes {@link #doStart()}.</li>
     * </ol>
     *
     * @param ev The event indicating shutdown.
     */
    public void onStart(final @Observes StartupEvent ev) {

        logJvmDetails();
        registerVertxCloseHook();
        log.info("Starting component {}...", appConfigs.componentName);
        doStart();
    }

    /**
     * Invoked during start up.
     * <p>
     * Subclasses should override this method in order to initialize
     * the component.
     */
    protected void doStart() {
        // do nothing
    }

    /**
     * Do work on application stop signal.
     */
    protected void doStop() {
        // do nothing
    }


    /**
     * Stops this component.
     *
     * @param ev The event indicating shutdown.
     */
    public void onStop(final @Observes ShutdownEvent ev) {
        doStop();
        log.info("shutting down {}", appConfigs.componentName);
        if (addedVertxCloseHook != null && vertx instanceof VertxInternal vertxInternal) {
            vertxInternal.removeCloseHook(addedVertxCloseHook);
        }
        final CompletableFuture<Void> shutdown = new CompletableFuture<>();
        vertx.close(attempt -> {
            if (attempt.succeeded()) {
                shutdown.complete(null);
            } else {
                shutdown.completeExceptionally(attempt.cause());
            }
        });
        shutdown.join();
    }
}
