/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2012 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * http://glassfish.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
package org.glassfish.jersey.server.internal;

import org.glassfish.jersey.internal.inject.Providers;
import org.glassfish.jersey.server.ApplicationHandler;
import org.glassfish.jersey.server.spi.AbstractContainerLifecycleListener;
import org.glassfish.jersey.server.spi.Container;
import org.glassfish.jersey.server.spi.ContainerLifecycleListener;

/**
 * Helper class to provide some common functionality related to {@link org.glassfish.jersey.server.ResourceConfig application configuration}.
 *
 * @author Jakub Podlesak (jakub.podlesak at oracle.com)
 */
public final class ConfigHelper {

    private static final ContainerLifecycleListener EMPTY_CONTAINER_LIFECYCLE_LISTENER =
            new AbstractContainerLifecycleListener() {
            };

    /**
     * Provides a single ContainerLifecycleListener instance based on the {@link ApplicationHandler application} configuration.
     * This method looks for providers implementing {@link ContainerLifecycleListener} interface and aggregates them into
     * a single umbrella listener instance that is returned.
     *
     * @param applicationHandler actual application from where to get the listener.
     * @return a single instance of a ContainerLifecycleListener, can not be null.
     */
    public static ContainerLifecycleListener getContainerLifecycleListener(final ApplicationHandler applicationHandler) {

        final Iterable<ContainerLifecycleListener> listeners = Providers.getAllProviders(applicationHandler.getServiceLocator(),
                ContainerLifecycleListener.class);
        return !listeners.iterator().hasNext() ? EMPTY_CONTAINER_LIFECYCLE_LISTENER : new ContainerLifecycleListener() {

            @Override
            public void onStartup(Container container) {
                for (ContainerLifecycleListener listener : listeners) {
                    listener.onStartup(container);
                }
            }

            @Override
            public void onReload(Container container) {
                for (ContainerLifecycleListener listener : listeners) {
                    listener.onReload(container);
                }
            }

            @Override
            public void onShutdown(Container container) {
                for (ContainerLifecycleListener listener : listeners) {
                    listener.onShutdown(container);
                }
            }
        };
    }
}
