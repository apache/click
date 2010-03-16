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
package org.apache.click.examples.interceptor;

import org.apache.click.Context;
import org.apache.click.Page;
import org.apache.click.PageInterceptor;
import org.apache.click.service.LogService;
import org.apache.click.util.ClickUtils;

/**
 * Provides a basic page profiling PageInterceptor example.
 * <p/>
 * This interceptor must be configured with "request" scope as it not thread
 * safe.
 */
public class ProfilingInterceptor implements PageInterceptor {

    private String pageName;
    private long startTime;
    private long checkpointTime;
    private long createDuration;
    private long processDuration;
    private long renderDuration;
    private long totalDuration;

    /**
     * @see PageInterceptor#preCreate(Class, Context)
     */
    public boolean preCreate(Class<? extends Page> pageClass, Context context) {
        checkpointTime = System.currentTimeMillis();
        startTime = checkpointTime;
        pageName = pageClass.getSimpleName();
        return true;
    }

    /**
     * @see PageInterceptor#postCreate(Page)
     */
    public boolean postCreate(Page page) {
        createDuration = System.currentTimeMillis() - checkpointTime;
        checkpointTime = System.currentTimeMillis();
        return true;
    }

    /**
     * @see PageInterceptor#preResponse(Page)
     */
    public boolean preResponse(Page page) {
        processDuration = System.currentTimeMillis() - checkpointTime;
        checkpointTime = System.currentTimeMillis();
        return true;
    }

    /**
     * @see PageInterceptor#postDestroy(Page)
     */
    public void postDestroy(Page page) {
        renderDuration = System.currentTimeMillis() - checkpointTime;
        totalDuration = System.currentTimeMillis() - startTime;

        LogService logService = ClickUtils.getLogService();
        if (logService.isInfoEnabled()) {
//            logService.info(this);
        }
    }

    /**
     * @see Object#toString()
     */
    public String toString() {
        return getClass().getSimpleName()
            + "[page=" + pageName
            + ", createDuration=" + createDuration
            + ", processDuration=" + processDuration
            + ", renderDuration=" + renderDuration
            + ", totalDuration=" + totalDuration
            + "]";
    }

}
