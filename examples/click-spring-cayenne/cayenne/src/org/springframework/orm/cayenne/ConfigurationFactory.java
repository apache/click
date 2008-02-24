package org.springframework.orm.cayenne;

import javax.sql.DataSource;

import org.apache.log4j.Level;
import org.objectstyle.cayenne.conf.Configuration;
import org.objectstyle.cayenne.conf.DataSourceFactory;
import org.objectstyle.cayenne.conf.DefaultConfiguration;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * A singleton factory for Cayenne Configuration. Allows overriding Cayenne
 * default DataSource configuration.
 */
public class ConfigurationFactory implements FactoryBean, InitializingBean,
        DisposableBean {
    protected Configuration configuration;
    protected DataSource dataSource;

    /**
     * Builds Cayenne configuration object based on configured properties.
     */
    public void afterPropertiesSet() throws Exception {
        this.configuration = new DefaultConfiguration();
        if (dataSource != null) {
            configuration.setDataSourceFactory(new ExternalDataSourceFactory());
        }

        configuration.initialize();
    }

    /**
     * Shuts down underlying Configuration.
     */
    public void destroy() throws Exception {
        if (configuration != null) {
            configuration.shutdown();
        }
    }

    /**
     * Returns a DataSource property. If this property is set prior to
     * "afterPropertiesSet" call, this DataSource will replace any DataSources
     * configured in Cayenne.
     */
    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Object getObject() throws Exception {
        return configuration;
    }

    public Class getObjectType() {
        return Configuration.class;
    }

    public boolean isSingleton() {
        return true;
    }

    final class ExternalDataSourceFactory implements DataSourceFactory {

        public DataSource getDataSource(String location, Level logLevel)
                throws Exception {
            return dataSource;
        }

        public DataSource getDataSource(String location) throws Exception {
            return dataSource;
        }

        public void initializeWithParentConfiguration(Configuration conf) {
        }
    }
}