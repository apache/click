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

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.TriggerUtils;
import org.quartz.impl.StdSchedulerFactory;

/**
 * Provides a service wrapper class around the Quartz scheduler class.
 */
public class SchedulerService {

    /** The scheduler group name. */
    private static final String GROUP_NAME = "click-examples";

    /** The scheduler trigger group name. */
    private static final String TRIGGER_GROUP_NAME = "click-examples-trigger-group";

    /** The quartz scheduler instance. */
    private final Scheduler scheduler;

    // Constructor ------------------------------------------------------------

    /**
     * Create a new scheduler service with the given Quartz Scheduler Factory.
     *
     * @param schedulerFactory the Quartz Scheduler Factory
     */
    public SchedulerService(StdSchedulerFactory schedulerFactory) {
        Validate.notNull(schedulerFactory, "Null schedulerFactory param");

        try {
            scheduler = schedulerFactory.getScheduler();

        } catch (SchedulerException se) {
            String msg = "Could not obtain Quartz Scheduler instance";
            throw new RuntimeException(msg);
        }
    }

    // Public Methods ---------------------------------------------------------

    /**
     * Schedule a new quartz job with the given job details, start and end date,
     * repeat count and repeat interval.
     *
     * @param jobDetail the quartz job detail
     * @param startDate the start date of the job
     * @param endDate the end date of the job
     * @param repeatCount the repeat count
     * @param repeatInterval the job repeat interval in milliseconds
     */
    public void scheduleJob(JobDetail jobDetail, Date startDate, Date endDate, int repeatCount, long repeatInterval) {
        Validate.notNull(jobDetail, "Null jobDetail parameter");

        // get a "nice round" time a few seconds in the future....
        long ts = TriggerUtils.getNextGivenSecondDate(null, 10).getTime();

        if (startDate == null || startDate.before(new Date())) {
            startDate = new Date(ts);
        }

        jobDetail.setGroup(GROUP_NAME);

        SimpleTrigger trigger =
            new SimpleTrigger(jobDetail.getName(),
                              TRIGGER_GROUP_NAME,
                              jobDetail.getName(),
                              jobDetail.getGroup(),
                              startDate,
                              endDate,
                              repeatCount,
                              repeatInterval);

        try {
            getScheduler().scheduleJob(jobDetail, trigger);

        } catch (SchedulerException se) {
            throw new RuntimeException( "Could not obtain schedule job " + jobDetail);
        }
    }

    /**
     * Return true if the scheduler is paused.
     *
     * @return true if the scheduler is paused
     */
    public boolean isPaused() {
        try {
            return getScheduler().isInStandbyMode();

        } catch (SchedulerException se) {
            throw new RuntimeException( "Could not determine if scheduler is in standby mode");
        }
    }

    /**
     * Pause all the scheduled jobs, and interrupt any currently executing jobs.
     */
    @SuppressWarnings("unchecked")
    public void pauseAll() {
        try {
            for (Iterator i = scheduler.getCurrentlyExecutingJobs().iterator(); i.hasNext();) {
                JobExecutionContext context = (JobExecutionContext) i.next();
                interruptJob(context.getJobDetail().getName());
            }

            getScheduler().pauseAll();

        } catch (SchedulerException se) {
            String msg = "Could not pause all jobs";
            throw new RuntimeException(msg, se);
        }
    }

    /**
     * Resume all paused jobs.
     */
    public void resumeAll() {
        try {
            getScheduler().resumeAll();

        } catch (SchedulerException se) {
            String msg = "Could not resume all jobs";
            throw new RuntimeException(msg, se);
        }
    }

    /**
     * Pause the job for the given name.
     *
     * @param jobName the name of the job to pause
     */
    public void pauseJob(String jobName) {
        try {
            getScheduler().pauseJob(jobName, GROUP_NAME);

        } catch (SchedulerException se) {
            String msg = "Could not pause Quartz Scheduler job: " + jobName;
            throw new RuntimeException(msg, se);
        }
    }

    /**
     * Resume the job for the given name.
     *
     * @param jobName the name of the job to resume
     */
    public void resumeJob(String jobName) {
        try {
            getScheduler().resumeJob(jobName, GROUP_NAME);

        } catch (SchedulerException se) {
            String msg = "Could not resume Quartz Scheduler job: " + jobName;
            throw new RuntimeException(msg, se);
        }
    }

    /**
     * Trigger the job for the given name.
     *
     * @param jobName the name of the job to trigger
     */
    public void triggerJob(String jobName) {
        try {
            getScheduler().triggerJob(jobName, GROUP_NAME);

        } catch (SchedulerException se) {
            String msg = "Could not resume Quartz Scheduler job: " + jobName;
            throw new RuntimeException(msg, se);
        }
    }

    /**
     * Interrupt the job for the given name, return true if the job was found.
     *
     * @param jobName the name of the job to interrupt
     */
    public boolean interruptJob(String jobName) {
        try {
            return getScheduler().interrupt(jobName, GROUP_NAME);

        } catch (SchedulerException se) {
            String msg = "Could not obtain Quartz Scheduler JobDetails";
            throw new RuntimeException(msg, se);
        }
    }

    /**
     * Delete the job for the given name, return true if the job was found.
     *
     * @param jobName the name of the job to delete
     * @return true if the Job was found and deleted.
     */
    public boolean deleteJob(String jobName) {
        try {
            return getScheduler().deleteJob(jobName, GROUP_NAME);

        } catch (SchedulerException se) {
            String msg = "Could not obtain Quartz Scheduler JobDetails";
            throw new RuntimeException(msg, se);
        }
    }

    /**
     * Return the list of scheduled JobDetails for the group name: "click-group".
     *
     * @return the list of scheduled JobDetails for the group name: "click-group"
     */
    public List<JobDetail> getJobDetailList() {
        try {
            List<JobDetail> list = new ArrayList<JobDetail>();

            String[] jobNames = getScheduler().getJobNames(GROUP_NAME);

            for (String jobName : jobNames) {
                list.add(getScheduler().getJobDetail(jobName, GROUP_NAME));
            }

            return list;

        } catch (SchedulerException se) {
            String msg = "Could not obtain Quartz Scheduler JobDetails";
            throw new RuntimeException(msg, se);
        }
    }

    /**
     * Return the JobDetail for the given name and group name: "click-group".
     *
     * @return the JobDetail for the given name and group name: "click-group".
     */
    public JobDetail getJobDetail(String jobName) {
        try {
            return getScheduler().getJobDetail(jobName, GROUP_NAME);

        } catch (SchedulerException se) {
            String msg = "Could not obtain Quartz Scheduler JobDetail";
            throw new RuntimeException(msg, se);
        }
    }

    /**
     * Return true if the scheduler has the named job.
     *
     * @param jobName the name of the job
     * @return true if the scheduler has the named job
     */
    public boolean hasJob(String jobName) {
        try {
            return (getScheduler().getJobDetail(jobName, GROUP_NAME) != null);

        } catch (SchedulerException se) {
            String msg = "Could not obtain Quartz Scheduler JobDetail";
            throw new RuntimeException(msg, se);
        }
    }

    /**
     * Return the list of JobDetail and Trigger associations.
     *
     * @return the list of JobDetail and Trigger associations
     */
    public List<JobAndSimpleTrigger> getJobAndTriggerList() {
        try {
            List<JobAndSimpleTrigger> list = new ArrayList<JobAndSimpleTrigger>();

            String[] jobNames = getScheduler().getJobNames(GROUP_NAME);

            for (int i = 0; i < jobNames.length; i++) {
                String jobName = jobNames[i];

                JobDetail jobDetail = getJobDetail(jobName);

                SimpleTrigger trigger  = (SimpleTrigger) getScheduler().getTrigger(jobName, TRIGGER_GROUP_NAME);

                list.add(new JobAndSimpleTrigger(jobDetail, trigger, getScheduler()));
            }

            return list;

        } catch (SchedulerException se) {
            String msg = "Could not obtain Quartz Scheduler JobAndTriggerList";
            throw new RuntimeException(msg, se);
        }
    }

    /**
     * Return the Job and Trigger for the given job name
     *
     * @param jobName the name of the job
     * @return the Job and Trigger for the given job name
     */
    public JobAndSimpleTrigger getJobAndTrigger(String jobName) {
        Validate.notNull(jobName, "Null jobName parameter");

        try {
            JobDetail jobDetail = getJobDetail(jobName);

            if (jobDetail != null) {
                SimpleTrigger trigger  = (SimpleTrigger)
                    getScheduler().getTrigger(jobName, TRIGGER_GROUP_NAME);

                return new JobAndSimpleTrigger(jobDetail, trigger, getScheduler());

            } else {
                return null;
            }

        } catch (SchedulerException se) {
            String msg = "Could not obtain Quartz Scheduler JobAndTrigger";
            throw new RuntimeException(msg, se);
        }
    }

    // Private Methods --------------------------------------------------------

    /**
     * Return the scheduler instance.
     *
     * @return the scheduler instance
     */
    private Scheduler getScheduler() {
        return scheduler;
    }

}
