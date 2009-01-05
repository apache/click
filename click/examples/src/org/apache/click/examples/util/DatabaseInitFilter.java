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

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.click.examples.domain.Customer;
import org.apache.click.examples.domain.PostCode;
import org.apache.click.examples.domain.SystemCode;
import org.apache.click.examples.domain.User;
import org.apache.click.util.ClickUtils;

import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.access.DataDomain;
import org.apache.cayenne.access.DataNode;
import org.apache.cayenne.access.DbGenerator;
import org.apache.cayenne.conf.Configuration;
import org.apache.cayenne.conf.ServletUtil;
import org.apache.cayenne.map.DataMap;
import org.apache.cayenne.query.SelectQuery;
import org.apache.commons.lang.WordUtils;

/**
 * Provides a database initialization filter. This servlet filter creates a
 * examples database schema using the Cayenne {@link DbGenerator} utility class,
 * and loads data files into the database.
 * <p/>
 * This filter also provides a customer reloading task which runs every 15
 * minutes.
 *
 * @author Malcolm Edgar
 */
public class DatabaseInitFilter implements Filter {

    private static final long RELOAD_TIMER_INTERVAL = 1000 * 60 * 5;

    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private Timer reloadTimer = new Timer(true);

    // --------------------------------------------------------- Public Methods

    /**
     * @see Filter#init(javax.servlet.FilterConfig)
     */
    public void init(FilterConfig config) throws ServletException {
        ServletUtil.initializeSharedConfiguration(config.getServletContext());

        try {
            DataDomain cayenneDomain =
                Configuration.getSharedConfiguration().getDomain();
            DataMap dataMap = cayenneDomain.getMap("cayenneMap");
            DataNode dataNode = cayenneDomain.getNode("cayenneNode");

            initDatabaseSchema(dataNode, dataMap);

            loadDatabase();

            reloadTimer.schedule(new ReloadTask(), 10000, RELOAD_TIMER_INTERVAL);

        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException("Error creating database", e);
        }
    }

 
    /**
     * @see Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
     */
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain filterChain) throws IOException, ServletException {
        filterChain.doFilter(request, response);
    }

    /**
     * Cancel the reload timer.
     *
     * @see Filter#destroy()
     */
    public void destroy() {
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

        dataContext.commitChanges();
    }

    private static void loadFile(String filename, DataContext dataContext,
            LineProcessor lineProcessor) throws IOException {

        InputStream is = ClickUtils.getResourceAsStream(filename, DatabaseInitFilter.class);

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

    private static interface LineProcessor {
        public void processLine(String line, DataContext dataContext);
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

    private static class ReloadTask extends TimerTask {

        public void run() {
            DataContext dataContext = null;
            try {
                dataContext = DataContext.createDataContext();

                SelectQuery query = new SelectQuery(Customer.class);
                List list = dataContext.performQuery(query);

                if (list.size() < 60) {
                    dataContext.deleteObjects(list);

                    loadCustomers(dataContext);

                    dataContext.commitChanges();
                }

            } catch (Throwable t) {
                t.printStackTrace();

                if (dataContext != null) {
                    dataContext.rollbackChanges();
                }
            }
        }

    }
}
