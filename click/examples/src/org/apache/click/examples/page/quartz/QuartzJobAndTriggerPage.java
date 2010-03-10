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
package org.apache.click.examples.page.quartz;

import javax.servlet.ServletContext;

import org.apache.click.ActionListener;
import org.apache.click.Control;
import org.apache.click.control.FieldSet;
import org.apache.click.control.Form;
import org.apache.click.control.Option;
import org.apache.click.control.Select;
import org.apache.click.control.Submit;
import org.apache.click.control.TextField;
import org.apache.click.examples.page.BorderPage;
import org.apache.click.examples.quartz.JobAndSimpleTrigger;
import org.apache.click.examples.quartz.SchedulerService;
import org.apache.click.extras.control.DateField;
import org.quartz.JobDetail;
import org.quartz.ee.servlet.QuartzInitializerListener;
import org.quartz.impl.StdSchedulerFactory;

/**
 * Provides a Quartz Job and Simple Trigger edit page.
 */
public class QuartzJobAndTriggerPage extends BorderPage {

    private static final long serialVersionUID = 1L;

    private Form form = new Form("form");
    private TextField nameField = new TextField("name", true);
    private TextField classnameField = new TextField("classname", true);
    private TextField descriptionField = new TextField("description");
    private DateField startDateField = new DateField("startDate", true);
    private DateField endDateField = new DateField("endDate");
    private Select repeatCountField = new Select("repeatCount", "Repeat");
    private Select repeatIntervalField = new Select("repeatInterval");

    private SchedulerService schedulerService;

    // Constructor ------------------------------------------------------------

    public QuartzJobAndTriggerPage() {

        // Form
        addControl(form);
        form.setDefaultFieldSize(80);

        FieldSet fieldSet = new FieldSet("Job and Trigger");
        form.add(fieldSet);

        nameField.setMaxLength(80);
        fieldSet.add(nameField);

        fieldSet.add(classnameField);

        descriptionField.setMaxLength(120);
        fieldSet.add(descriptionField);

        fieldSet.add(startDateField);

        fieldSet.add(endDateField);

        repeatCountField.add(new Option("-1", "Run continuously"));
        repeatCountField.add(new Option("0", "Run once"));
        repeatCountField.add(new Option("1", "Repeat 1"));
        repeatCountField.add(new Option("2", "Repeat 2"));
        repeatCountField.add(new Option("3", "Repeat 3"));
        repeatCountField.add(new Option("4", "Repeat 4"));
        repeatCountField.add(new Option("5", "Repeat 5"));
        repeatCountField.add(new Option("10", "Repeat 10"));
        repeatCountField.add(new Option("20", "Repeat 20"));
        repeatCountField.setValue("-1");
        fieldSet.add(repeatCountField);

        repeatIntervalField.add(new Option("60000", "1 minute"));
        repeatIntervalField.add(new Option("120000", "2 minutes"));
        repeatIntervalField.add(new Option("300000", "5 minutes"));
        repeatIntervalField.add(new Option("600000", "10 minutes"));
        repeatIntervalField.add(new Option("900000", "15 minutes"));
        repeatIntervalField.add(new Option("1800000", "30 minutes"));
        repeatIntervalField.add(new Option("3600000", "1 hour"));
        repeatIntervalField.add(new Option("7200000", "2 hours"));
        repeatIntervalField.add(new Option("10800000", "3 hours"));
        repeatIntervalField.add(new Option("21600000", "6 hours"));
        repeatIntervalField.add(new Option("43200000", "12 hours"));
        repeatIntervalField.add(new Option("86400000", "24 hours"));
        repeatIntervalField.setValue("3600000");
        fieldSet.add(repeatIntervalField);

        Submit saveSubmit = new Submit("Save");
        saveSubmit.setActionListener(new ActionListener(){
            private static final long serialVersionUID = 1L;

            public boolean onAction(Control source) {
                return onSaveClick();
            }
        });
        form.add(saveSubmit);

        Submit cancelSubmit = new Submit("Cancel");
        cancelSubmit.setActionListener(new ActionListener(){
            private static final long serialVersionUID = 1L;

            public boolean onAction(Control source) {
                setRedirect(QuartzJobSchedulerPage.class);
                return false;
            }
        });
        form.add(cancelSubmit);
    }

    // Event Handlers ---------------------------------------------------------

    @Override
    public void onInit() {
        super.onInit();

        if (getSchedulerService() != null) {
            String name = getContext().getRequestParameter("job.name");
            if (name != null) {
                JobAndSimpleTrigger jat = getSchedulerService().getJobAndTrigger(name);

                if (jat != null) {
                    nameField.setReadonly(true);
                    nameField.setAttribute("class", "readonly");
                    form.copyFrom(jat.getJob());
                    classnameField.setValue(jat.getJob().getJobClass().getName());
                    startDateField.setDate(jat.getTrigger().getStartTime());
                    endDateField.setDate(jat.getTrigger().getEndTime());
                    repeatCountField.setValue("" + jat.getTrigger().getRepeatCount());
                    repeatIntervalField.setValue("" + jat.getTrigger().getRepeatInterval());
                }
            }

        } else {
            setFlashMessage(getMessage("jobSchedulerNotAvailableMsg"));
            form.setDisabled(true);
        }
    }

    @SuppressWarnings("unchecked")
    public boolean onSaveClick() {
        if (form.isValid()) {
            String name = nameField.getValue();

            Class jobClass = null;
            try {
                jobClass = Class.forName(classnameField.getValue());
            } catch (Exception e) {
                classnameField.setError("Could not find class for classname");
                return true;
            }

            if (getSchedulerService().getJobDetail(name) != null) {
                getSchedulerService().deleteJob(name);
            }

            JobDetail jobDetail = new JobDetail();
            form.copyTo(jobDetail);
            jobDetail.setJobClass(jobClass);

            long intervalMs = Long.parseLong(repeatIntervalField.getValue());

            getSchedulerService().scheduleJob(jobDetail,
                    startDateField.getDate(),
                    endDateField.getDate(),
                    Integer.parseInt(repeatCountField.getValue()),
                    intervalMs);

            setFlashMessage("Saved job '" + jobDetail.getName() + "'");

            setRedirect(QuartzJobSchedulerPage.class);
            return false;
        }
        return true;
    }

    // Protected Methods ------------------------------------------------------

    /**
     * Set a flash attribute message with the given message string.
     *
     * @param message the flash attribute message to display
     */
    protected void setFlashMessage(String message) {
        getContext().setFlashAttribute("flash", message);
    }

    /**
     * Return the scheduler service.
     *
     * @return the scheduler service
     */
    protected SchedulerService getSchedulerService() {
        if (schedulerService == null) {
            ServletContext servletContext = getContext().getServletContext();

            StdSchedulerFactory schedulerFactory = (StdSchedulerFactory)
                servletContext.getAttribute(QuartzInitializerListener.QUARTZ_FACTORY_KEY);

            if (schedulerFactory != null) {
                schedulerService = new SchedulerService(schedulerFactory);
            }
        }

        return schedulerService;
    }

}
