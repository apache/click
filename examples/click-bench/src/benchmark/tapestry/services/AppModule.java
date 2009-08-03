package benchmark.tapestry.services;

import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.ioc.MappedConfiguration;

/**
 *
 */
public class AppModule {

    public static void contributeApplicationDefaults(
        MappedConfiguration<String, String> configuration) {

        configuration.add(SymbolConstants.OMIT_GENERATOR_META, "true");
        configuration.add(SymbolConstants.GZIP_COMPRESSION_ENABLED, "false");
        configuration.add(SymbolConstants.PRODUCTION_MODE, "true");
        configuration.add(SymbolConstants.FILE_CHECK_INTERVAL, "10 m");
        configuration.add("tapestry.page-pool.hard-limit", "120");
        configuration.add("tapestry.page-pool.soft-limit", "120");
    }
}
