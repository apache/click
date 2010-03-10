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
package org.apache.click.examples.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.access.DataDomain;
import org.apache.cayenne.access.DataNode;
import org.apache.cayenne.access.DbGenerator;
import org.apache.cayenne.conf.Configuration;
import org.apache.cayenne.conf.ServletUtil;
import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.query.SelectQuery;
import org.apache.click.examples.domain.Course;
import org.apache.click.examples.domain.Customer;
import org.apache.click.examples.domain.PostCode;
import org.apache.click.examples.domain.StudentHouse;
import org.apache.click.examples.domain.SystemCode;
import org.apache.click.examples.domain.User;
import org.apache.click.examples.quartz.ExampleJob;
import org.apache.click.examples.quartz.SchedulerService;
import org.apache.click.util.ClickUtils;
import org.apache.commons.lang.WordUtils;
import org.quartz.JobDetail;
import org.quartz.ee.servlet.QuartzInitializerListener;
import org.quartz.impl.StdSchedulerFactory;

/**
 * Provides a database initialization servlet context listener. This listener
 * creates a examples database schema using the Cayenne {@link DbGenerator}
 * utility class, and loads data files into the database. This class also
 * adds an ExampleJob to the Quartz Scheduler.
 * <p/>
 * This listener also provides a customer reloading task which runs every 15
 * minutes.
 */
public class DatabaseInitListener implements ServletContextListener {

    private static final long RELOAD_TIMER_INTERVAL = 1000 * 60 * 5;

    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private Timer reloadTimer = new Timer(true);

    // --------------------------------------------------------- Public Methods

    /**
     * @see ServletContextListener#contextInitialized(ServletContextEvent)
     */
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext servletContext = sce.getServletContext();

        ServletUtil.initializeSharedConfiguration(servletContext);

        try {
            DataDomain cayenneDomain =
                Configuration.getSharedConfiguration().getDomain();
            DataMap dataMap = cayenneDomain.getMap("cayenneMap");
            DataNode dataNode = cayenneDomain.getNode("cayenneNode");

            initDatabaseSchema(dataNode, dataMap);

            loadDatabase();

            StdSchedulerFactory schedulerFactory = (StdSchedulerFactory)
                servletContext.getAttribute(QuartzInitializerListener.QUARTZ_FACTORY_KEY);

            SchedulerService schedulerService = new SchedulerService(schedulerFactory);

            loadQuartzJobs(schedulerService);

            reloadTimer.schedule(new ReloadTask(schedulerService), 10000, RELOAD_TIMER_INTERVAL);


        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error creating database", e);
        }
    }

    /**
     * @see ServletContextListener#contextDestroyed(ServletContextEvent)
     */
    public void contextDestroyed(ServletContextEvent sce) {
        reloadTimer.cancel();
    }

    // -------------------------------------------------------- Private Methods

    /**
     * Create the demonstration database schema using the given Cayenne
     * DataNode and DataMap.
     *
     * @param dataNode the Cayenne DataNode
     * @param dataMap the Cayenne DataMap
     * @throws Exception
     */
    private void initDatabaseSchema(DataNode dataNode, DataMap dataMap)
            throws Exception {

        DbGenerator generator = new DbGenerator(dataNode.getAdapter(), dataMap);
        generator.setShouldCreateFKConstraints(true);
        generator.setShouldCreatePKSupport(true);
        generator.setShouldCreateTables(true);
        generator.setShouldDropPKSupport(false);
        generator.setShouldDropTables(false);

        generator.runGenerator(dataNode.getDataSource());
    }

    /**
     * Load data files into the database
     *
     * @throws IOException if an I/O error occurs
     */
    private void loadDatabase() throws IOException {
        final DataContext dataContext = DataContext.createDataContext();

        // Load users data file
        loadUsers(dataContext);

        // Load customers data file
        loadCustomers(dataContext);

        // Load customers data file
        loadSystemCodes(dataContext);

        // Load post codes data file
        loadPostCodes(dataContext);

        // Load course data file
        loadCourses(dataContext);

        // Load student houses data file
        loadStudentHouses(dataContext);

        dataContext.commitChanges();
    }

    private static void loadFile(String filename, DataContext dataContext,
            LineProcessor lineProcessor) throws IOException {

        InputStream is = ClickUtils.getResourceAsStream(filename, DatabaseInitListener.class);

        if (is == null) {
            throw new RuntimeException("classpath file not found: " + filename);
        }

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(is));

            String line = reader.readLine();
            while (line != null) {
                line = line.trim();

                if (line.length() > 0 && !line.startsWith("#")) {
                    lineProcessor.processLine(line, dataContext);
                }

                line = reader.readLine();
            }
        } finally {
            ClickUtils.close(reader);
        }
    }

    private void loadUsers(final DataContext dataContext) throws IOException {
        loadFile("users.txt", dataContext, new LineProcessor() {
            public void processLine(String line, DataContext context) {
                StringTokenizer tokenizer = new StringTokenizer(line, ",");

                User user = new User();

                user.setUsername(next(tokenizer));
                user.setPassword(next(tokenizer));
                user.setFullname(next(tokenizer));
                user.setEmail(next(tokenizer));

                context.registerNewObject(user);
            }
        });
    }

    private static void loadCustomers(final DataContext dataContext) throws IOException {
        // Load customers data file
        loadFile("customers.txt", dataContext, new LineProcessor() {
            public void processLine(String line, DataContext context) {
                StringTokenizer tokenizer = new StringTokenizer(line, ",");

                Customer customer = new Customer();
                customer.setName(next(tokenizer));
                if (tokenizer.hasMoreTokens()) {
                    customer.setEmail(next(tokenizer));
                }
                if (tokenizer.hasMoreTokens()) {
                    customer.setAge(Integer.valueOf(next(tokenizer)));
                }
                if (tokenizer.hasMoreTokens()) {
                    customer.setInvestments(next(tokenizer));
                }
                if (tokenizer.hasMoreTokens()) {
                    customer.setHoldings(Double.valueOf(next(tokenizer)));
                }
                if (tokenizer.hasMoreTokens()) {
                    customer.setDateJoined(createDate(next(tokenizer)));
                }
                if (tokenizer.hasMoreTokens()) {
                    customer.setActive(Boolean.valueOf(next(tokenizer)));
                }

                dataContext.registerNewObject(customer);
            }
        });
    }

    private void loadSystemCodes(final DataContext dataContext) throws IOException {
        loadFile("system-codes.txt", dataContext, new LineProcessor() {
            public void processLine(String line, DataContext context) {
                StringTokenizer tokenizer = new StringTokenizer(line, ",");

                SystemCode systemCode = new SystemCode();
                systemCode.setName(next(tokenizer));
                systemCode.setValue(next(tokenizer));
                systemCode.setLabel(next(tokenizer));
                systemCode.setOrderBy(Integer.valueOf((next(tokenizer))));

                dataContext.registerNewObject(systemCode);
            }
        });
    }

    private void loadPostCodes(final DataContext dataContext) throws IOException {
        loadFile("post-codes-australian.csv", dataContext, new LineProcessor() {
            public void processLine(String line, DataContext context) {
                if (!line.startsWith("Pcode")) {
                    StringTokenizer tokenizer = new StringTokenizer(line, ",");

                    String postcode = next(tokenizer);
                    String locality = WordUtils.capitalizeFully(next(tokenizer));
                    String state = next(tokenizer);

                    PostCode postCode = new PostCode();
                    postCode.setPostCode(postcode);
                    postCode.setLocality(locality);
                    postCode.setState(state);

                    dataContext.registerNewObject(postCode);
                }
            }
        });
    }

    private void loadCourses(final DataContext dataContext) throws IOException {
        loadFile("courses.txt", dataContext, new LineProcessor() {
            public void processLine(String line, DataContext context) {
                StringTokenizer tokenizer = new StringTokenizer(line, ",");

                Course course = new Course();

                course.setName(next(tokenizer));

                context.registerNewObject(course);
            }
        });
    }

    private void loadStudentHouses(final DataContext dataContext) throws IOException {
        loadFile("student-houses.txt", dataContext, new LineProcessor() {
            public void processLine(String line, DataContext context) {
                StringTokenizer tokenizer = new StringTokenizer(line, ",");

                StudentHouse studentHouse = new StudentHouse();

                studentHouse.setName(next(tokenizer));

                context.registerNewObject(studentHouse);
            }
        });
    }

    private static String next(StringTokenizer tokenizer) {
        String token = tokenizer.nextToken().trim();
        if (token.startsWith("\"")) {
            token = token.substring(1);
        }
        if (token.endsWith("\"")) {
            token = token.substring(0, token.length() - 1);
        }
        return token;
    }

    private static Date createDate(String pattern) {
        try {
            return FORMAT.parse(pattern);
        } catch (ParseException pe) {
            return null;
        }
    }

    private static void loadQuartzJobs(SchedulerService schedulerService) {

        // Create Submission Synchronize Job
        if (!schedulerService.hasJob(ExampleJob.class.getSimpleName())) {
            JobDetail jobDetail = new JobDetail();

            jobDetail.setName(ExampleJob.class.getSimpleName());
            jobDetail.setDescription("Demonstration job write Hello World");
            jobDetail.setJobClass(ExampleJob.class);

            // 5 minute interval
            final long fiveMinutesInMs = 24 * 60 * 60 * 1000;

            schedulerService.scheduleJob(jobDetail, new Date(), null, -1, fiveMinutesInMs);
        }
    }

    // Inner Classes ----------------------------------------------------------

    private static interface LineProcessor {
        public void processLine(String line, DataContext dataContext);
    }

    private static class ReloadTask extends TimerTask {

        private SchedulerService schedulerService;

        public ReloadTask(SchedulerService schedulerService) {
            this.schedulerService = schedulerService;
        }

        @SuppressWarnings("unchecked")
        public void run() {
            DataContext dataContext = null;
            try {
                dataContext = DataContext.createDataContext();

                SelectQuery query = new SelectQuery(Customer.class);
                List<Customer> list = dataContext.performQuery(query);

                if (list.size() < 60) {
                    dataContext.deleteObjects(list);

                    loadCustomers(dataContext);

                    dataContext.commitChanges();
                }

                loadQuartzJobs(schedulerService);

            } catch (Throwable t) {
                t.printStackTrace();

                if (dataContext != null) {
                    dataContext.rollbackChanges();
                }
            }
        }
    }
}
