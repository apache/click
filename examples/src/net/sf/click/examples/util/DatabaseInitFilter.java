package net.sf.click.examples.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import net.sf.click.examples.domain.Customer;
import net.sf.click.examples.domain.User;

import org.objectstyle.cayenne.access.DataContext;
import org.objectstyle.cayenne.access.DataDomain;
import org.objectstyle.cayenne.access.DataNode;
import org.objectstyle.cayenne.access.DbGenerator;
import org.objectstyle.cayenne.conf.Configuration;
import org.objectstyle.cayenne.conf.ServletUtil;
import org.objectstyle.cayenne.map.DataMap;

/**
 * Provides a database initialization filter. This servlet filter creates a
 * examples database schema using the Cayenne {@link DbGenerator} utility class.
 *
 * @author Malcolm Edgar
 * @author Andrus Adamchik
 */
public class DatabaseInitFilter implements Filter {

    private static final SimpleDateFormat FORMAT
        = new SimpleDateFormat("yyyy-MM-dd");

    // --------------------------------------------------------- Public Methods

    /**
     * @see Filter#init(javax.servlet.FilterConfig)
     */
    public void init(FilterConfig config) throws ServletException {
        ServletUtil.initializeSharedConfiguration(config.getServletContext());

        try {
            DataDomain cayenneDomain =
                Configuration.getSharedConfiguration().getDomain();
            DataMap dataMap = cayenneDomain.getMap("examplesMap");
            DataNode dataNode = cayenneDomain.getNode("examplesNode");

            initDatabaseScema(dataNode, dataMap);

            loadDatabase();

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
     * @see Filter#destroy()
     */
    public void destroy() {
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
    private void initDatabaseScema(DataNode dataNode, DataMap dataMap)
            throws Exception {

        DbGenerator generator = new DbGenerator(dataNode.getAdapter(), dataMap);
        generator.setShouldCreateFKConstraints(true);
        generator.setShouldCreatePKSupport(true);
        generator.setShouldCreateTables(true);
        generator.setShouldDropPKSupport(false);
        generator.setShouldDropTables(false);

        generator.runGenerator(dataNode.getDataSource());
    }

    private void loadDatabase() throws IOException {
        DataContext dataContext = DataContext.createDataContext();

        // Create some sample users
        User user = new User();
        user.setFullname("Ann Melan");
        user.setEmail("amelan@mycorp.com");
        user.setUsername("amelan");
        user.setPassword("password");
        dataContext.registerNewObject(user);

        user = new User();
        user.setFullname("Rodger Alan");
        user.setEmail("ralan@mycorp.com");
        user.setUsername("ralan");
        user.setPassword("password");
        dataContext.registerNewObject(user);

        user = new User();
        user.setFullname("David Henderson");
        user.setEmail("dhenderson@mycorp.com");
        user.setUsername("dhenderson");
        user.setPassword("password");
        dataContext.registerNewObject(user);

        // Load some customers
        InputStream is =
            DatabaseInitFilter.class.getResourceAsStream("customers.txt");

        BufferedReader reader =
            new BufferedReader(new InputStreamReader(is));

        String line = reader.readLine();
        while (line != null) {
            StringTokenizer tokenizer = new StringTokenizer(line, ",");

            Customer customer = new Customer();
            customer.setName(tokenizer.nextToken().trim());
            if (tokenizer.hasMoreTokens()) {
                customer.setEmail(tokenizer.nextToken().trim());
            }
            if (tokenizer.hasMoreTokens()) {
                customer.setAge(Integer.valueOf(tokenizer.nextToken().trim()));
            }
            if (tokenizer.hasMoreTokens()) {
                customer.setInvestments(tokenizer.nextToken().trim());
            }
            if (tokenizer.hasMoreTokens()) {
                customer.setHoldings(Double.valueOf(tokenizer.nextToken().trim()));
            }
            if (tokenizer.hasMoreTokens()) {
                customer.setDateJoined(createDate(tokenizer.nextToken().trim()));
            }
            if (tokenizer.hasMoreTokens()) {
                customer.setActive(Boolean.valueOf(tokenizer.nextToken().trim()));
            }

            dataContext.registerNewObject(customer);

            line = reader.readLine();
        }
 
        dataContext.commitChanges();
    }

    private static Date createDate(String pattern) {
        try {
            return FORMAT.parse(pattern);
        } catch (ParseException pe) {
            return null;
        }
    }
}
