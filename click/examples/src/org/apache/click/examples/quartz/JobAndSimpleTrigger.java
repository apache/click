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
package org.apache.click.examples.quartz;

import java.util.Iterator;

import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;

/**
 * Provides a Quartz Job and Simple Trigger display class.
 */
public class JobAndSimpleTrigger {

    private static final long MS_TO_MINS = 1000L * 60;

    private final JobDetail jobDetail;
    private final SimpleTrigger trigger;
    private final Scheduler scheduler;

    public JobAndSimpleTrigger(JobDetail jobDetail, SimpleTrigger trigger, Scheduler scheduler) {
        this.jobDetail = jobDetail;
        this.trigger = trigger;
        this.scheduler = scheduler;
    }

    public JobDetail getJob() {
        return jobDetail;
    }

    public SimpleTrigger getTrigger() {
        return trigger;
    }

    public int getTriggerState(){
        try {
            return scheduler.getTriggerState(trigger.getName(), trigger.getGroup());
        } catch(Throwable th) {
            return Trigger.STATE_NONE;
        }
    }

    public String getTriggerStateAsString(){
        switch(getTriggerState()){
        case Trigger.STATE_NONE:
            return "None";
        case Trigger.STATE_NORMAL:
            return "Normal";
        case Trigger.STATE_PAUSED:
            return "Paused";
        case Trigger.STATE_BLOCKED:
            return "Running";
        case Trigger.STATE_COMPLETE:
            return "Complete";
        case Trigger.STATE_ERROR:
            return "Error";
        }
        return "Unknown";
    }

    public String getRepeat() {
        long count = trigger.getRepeatCount();

        if (count == -1) {
            return "Continuously";

        } else if (count == 0) {
            return "Run once";

        } else if (count == 1) {
            return "Repeat once";

        } else {
            return "Repeat " + count;
        }
    }

    public String getInterval() {
        long interval = trigger.getRepeatInterval() / MS_TO_MINS;

        if (interval == 1) {
            return "1 min";

        } else if (interval < 60) {
            return "" + interval + " mins";

        } else if (interval == 60) {
            return "1 hour";

        } else {
            return "" + interval / 60 + " hours";
        }
    }

    @SuppressWarnings("unchecked")
    public boolean isExecuting() throws SchedulerException {
        for (Iterator i = scheduler.getCurrentlyExecutingJobs().iterator(); i.hasNext();) {
            JobExecutionContext context = (JobExecutionContext) i.next();
            if (context.getJobDetail().getFullName().equals(jobDetail.getFullName())) {
                return true;
            }
        }
        return false;
    }
}
