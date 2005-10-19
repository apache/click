package click.cayenne;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;

import org.objectstyle.cayenne.access.DataDomain;
import org.objectstyle.cayenne.access.DataNode;
import org.objectstyle.cayenne.access.DbGenerator;
import org.objectstyle.cayenne.conf.Configuration;
import org.objectstyle.cayenne.conf.WebApplicationContextFilter;
import org.objectstyle.cayenne.map.DataMap;

/**
 * Initializes Cayenne runtime and creates a demo database schema.
 * 
 * @see WebApplicationContextFilter
 * 
 * @author Andrus Adamchik
 * @author Malcolm Edgar
 */
public class CayenneInitFilter extends WebApplicationContextFilter {

    /**
     * Initialize the Cayenne web application context filter to initialize
     * the Cayenne runtime and create the demo database schema.
     * 
     * @see #init(FilterConfig)
     */
    public synchronized void init(FilterConfig config) throws ServletException {
        super.init(config);

        try {
            DataDomain cayenneDomain = 
                Configuration.getSharedConfiguration().getDomain();
            DataMap dataMap = cayenneDomain.getMap("HRMap");
            DataNode dataNode = cayenneDomain.getNode("HRDB");

            initDatabaseScema(dataNode, dataMap);
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException("Error creating database", e);
        }
    }

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
}
