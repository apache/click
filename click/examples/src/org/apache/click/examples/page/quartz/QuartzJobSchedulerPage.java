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

import java.util.Collections;
import java.util.List;

import javax.servlet.ServletContext;

import org.apache.cayenne.query.Ordering;
import org.apache.click.ActionListener;
import org.apache.click.Context;
import org.apache.click.Control;
import org.apache.click.control.ActionButton;
import org.apache.click.control.ActionLink;
import org.apache.click.control.Column;
import org.apache.click.control.Decorator;
import org.apache.click.control.PageLink;
import org.apache.click.control.Table;
import org.apache.click.examples.page.BorderPage;
import org.apache.click.examples.quartz.JobAndSimpleTrigger;
import org.apache.click.examples.quartz.SchedulerService;
import org.apache.click.util.HtmlStringBuffer;
import org.quartz.Trigger;
import org.quartz.ee.servlet.QuartzInitializerListener;
import org.quartz.impl.StdSchedulerFactory;

/**
 * Provides a Quartz Job Schedule search page.
 */
public class QuartzJobSchedulerPage extends BorderPage {

    private static final long serialVersionUID = 1L;

    private static final String DATE_FORMAT = "{0,date,hh:mm a d MMM yy }";

    /** The auto bound refresh button. */
    private ActionButton refresh = new ActionButton("refresh");

    // Private Variables ------------------------------------------------------

    private Table table = new Table("table");
    private PageLink editLink = new PageLink("edit", QuartzJobAndTriggerPage.class);
    private ActionLink pauseLink = new ActionLink("pause");
    private ActionLink resumeLink = new ActionLink("resume");
    private ActionLink triggerLink = new ActionLink("trigger");
    private ActionLink interruptLink = new ActionLink("interrupt");
    private ActionLink deleteLink = new ActionLink("delete");
    private ActionButton newJob = new ActionButton("newJob");

    private SchedulerService schedulerService;

    // Constructor ------------------------------------------------------------

    public QuartzJobSchedulerPage() {
        // Add button
        addControl(refresh);

        // Table
        addControl(table);
        table.setAttribute("class", Table.CLASS_SIMPLE);
        table.setSortable(true);
        table.setStyle("margin-left", "0.25em;");

        // Define columns

        Column column = new Column("job.name", "Job Name");
        column.setTitleProperty("job.description");
        table.addColumn(column);

        final Column statusColumn = new Column("triggerStateAsString", "Status");
        statusColumn.setEscapeHtml(false);
        statusColumn.setDecorator(new Decorator() {
            public String render(Object row, Context context) {
                JobAndSimpleTrigger jobAndTrigger = (JobAndSimpleTrigger) row;
                switch(jobAndTrigger.getTriggerState()){
                case Trigger.STATE_NONE:
                    return "<span style='color:red'>None</span>";
                case Trigger.STATE_NORMAL:
                    return "<span style='color:blue'>Normal</span>";
                case Trigger.STATE_PAUSED:
                    return "<span style='color:red'>Paused</span>";
                case Trigger.STATE_BLOCKED:
                    return "<span style='color:green'>Running</span>";
                case Trigger.STATE_COMPLETE:
                    return "<span style='color:black'>Complete</span>";
                case Trigger.STATE_ERROR:
                    return "<span style='color:red'>Error</span>";
                }
                return "Unknown";
            }
        });
        table.addColumn(statusColumn);

        table.addColumn(new Column("interval"));

        column = new Column("trigger.nextFireTime", "Next Run");
        column.setFormat(DATE_FORMAT);
        table.addColumn(column);

        column = new Column("trigger.startTime", "First Run");
        column.setFormat(DATE_FORMAT);
        table.addColumn(column);

        column = new Column("trigger.previousFireTime", "Last Run");
        column.setFormat(DATE_FORMAT);
        table.addColumn(column);

        // Initialized action column links

        editLink.setAttribute("class", "actionIcon");
        editLink.setTitle("Edit Job");
        addControl(editLink);

        pauseLink.setAttribute("class", "actionIcon");
        pauseLink.setTitle("Pause Job");
        pauseLink.setActionListener(new ActionListener(){
            private static final long serialVersionUID = 1L;

            public boolean onAction(Control source) {
                String name = pauseLink.getValue();
                getSchedulerService().pauseJob(name);
                setFlashMessage("Paused job '" + name + "'");
                return true;
            }
        });
        addControl(pauseLink);

        interruptLink.setAttribute("class", "actionIcon");
        interruptLink.setTitle("Interrupt Running Job");
        interruptLink.setActionListener(new ActionListener(){
            private static final long serialVersionUID = 1L;

            public boolean onAction(Control source) {
                String name = interruptLink.getValue();
                if (getSchedulerService().interruptJob(name)) {
                    setFlashMessage("Interrupted job '" + name + "'");
                } else {
                    setFlashMessage("Could not interrupt job '" + name + "'");
                }
                return true;
            }
        });
        addControl(interruptLink);

        triggerLink.setAttribute("class", "actionIcon");
        triggerLink.setTitle("Trigger Job");
        triggerLink.setActionListener(new ActionListener(){
            private static final long serialVersionUID = 1L;

            public boolean onAction(Control source) {
                String name = triggerLink.getValue();
                getSchedulerService().triggerJob(name);
                setFlashMessage("Triggered job '" + name +  "'");
                return true;
            }
        });
        addControl(triggerLink);

        resumeLink.setAttribute("class", "actionIcon");
        resumeLink.setTitle("Resume Job");
        resumeLink.setActionListener(new ActionListener(){
            private static final long serialVersionUID = 1L;

            public boolean onAction(Control source) {
                String name = resumeLink.getValue();
                getSchedulerService().resumeJob(name);
                setFlashMessage("Resumed job '" + name + "'");
                return true;
            }
        });
        addControl(resumeLink);

        deleteLink.setAttribute("class", "actionIcon");
        deleteLink.setTitle("Delete Job");
        String confirmMessage = getMessage("deleteConfirm", "Job");
        deleteLink.setAttribute("onclick", "return window.confirm('" + confirmMessage + "')");
        deleteLink.setActionListener(new ActionListener(){
            private static final long serialVersionUID = 1L;

            public boolean onAction(Control source) {
                String name = deleteLink.getValue();
                if (getSchedulerService().deleteJob(name)) {
                    setFlashMessage("Deleted job '" + name + "'");
                } else {
                    setFlashMessage("Could not delete " + name);
                }
                return true;
            }
        });
        addControl(deleteLink);

        // Add table action column if user has edit or delete permissions
        final Column actionColumn = new Column("action");

        // Render action controls based on users permission
        actionColumn.setDecorator(new Decorator() {
            public String render(Object object, Context context) {
                JobAndSimpleTrigger jobAndTrigger = (JobAndSimpleTrigger) object;

                HtmlStringBuffer buffer = new HtmlStringBuffer();

                editLink.setParameter("job.name", jobAndTrigger.getJob().getName());
                editLink.render(buffer);

                buffer.append(" | ");
                deleteLink.setValue(jobAndTrigger.getJob().getName());
                deleteLink.render(buffer);

                if (!getSchedulerService().isPaused()) {

                    if (jobAndTrigger.getTriggerState() == Trigger.STATE_PAUSED) {
                        buffer.append(" | ");
                        resumeLink.setValue(jobAndTrigger.getJob().getName());
                        resumeLink.render(buffer);

                    } else {
                        buffer.append(" | ");
                        pauseLink.setValue(jobAndTrigger.getJob().getName());
                        pauseLink.render(buffer);
                    }

                    buffer.append(" | ");
                    triggerLink.setValue(jobAndTrigger.getJob().getName());
                    triggerLink.render(buffer);

                    if (jobAndTrigger.getTriggerState() == Trigger.STATE_BLOCKED) {
                        buffer.append(" | ");
                        interruptLink.setValue(jobAndTrigger.getJob().getName());
                        interruptLink.render(buffer);
                    }
                }

                return buffer.toString();
            }

        });
        actionColumn.setSortable(false);
        table.addColumn(actionColumn);

        // Add Control Buttons.

        newJob.setActionListener(new ActionListener(){
            private static final long serialVersionUID = 1L;

            public boolean onAction(Control source) {
                setRedirect(QuartzJobAndTriggerPage.class);
                return false;
            }
        });
        addControl(newJob);
    }

    // Event Handlers ---------------------------------------------------------

    @Override
    public void onInit() {
        super.onInit();
        if (getSchedulerService() == null) {
            setFlashMessage(getMessage("jobSchedulerNotAvailableMsg"));
            newJob.setDisabled(true);
        }
    }

    @Override
    public void onRender() {
        super.onRender();

        if (getSchedulerService() != null) {
            List<JobAndSimpleTrigger> rowList = getSchedulerService().getJobAndTriggerList();
            Collections.sort(rowList, new Ordering("job.name", Ordering.ASC));
            table.setRowList(rowList);
        }
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
