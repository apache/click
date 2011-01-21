/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.click.examples.page.wizard;

import org.apache.click.Context;
import org.apache.click.examples.domain.Client;
import org.apache.commons.lang.math.NumberUtils;

/**
 *
 */
public class WizardUils {

    public static final String CLIENT_ATTR = "client";

    public static final String STEP_INDEX = "step_index";

    /**
     * Return the current Client instance in the session.
     *
     * @return the current Client instance in the session
     */
    public static Client getClientFromSession() {
        Context context = Context.getThreadLocalContext();
        Client client = (Client) context.getSessionAttribute(CLIENT_ATTR);
        return client;

    }

    /**
     * Save the Client instance in the session.
     *
     * @param client the client to store in the session
     */
    public static void saveClientInSession(Client client) {
        Context context = Context.getThreadLocalContext();
        context.setSessionAttribute(CLIENT_ATTR, client);
    }

    /**
     * Remove the currently stored Client instance from the session.
     */
    public static void removeClientFromSession() {
        Context context = Context.getThreadLocalContext();
        context.removeSessionAttribute(CLIENT_ATTR);
    }

    public static void saveActiveStepIndex(int index) {
        Context context = Context.getThreadLocalContext();
        context.setSessionAttribute(STEP_INDEX, index);
    }

    public static int restoreActiveStepIndex() {
        Context context = Context.getThreadLocalContext();
        Object value = context.getSessionAttribute(STEP_INDEX);
        int index = value == null ? 0 : (Integer) value;
        return index;
    }
}
