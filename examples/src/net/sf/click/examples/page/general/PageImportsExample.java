package net.sf.click.examples.page.general;

import net.sf.click.examples.page.BorderPage;
import net.sf.click.util.ClickUtils;
import net.sf.click.util.PageImports;

public class PageImportsExample extends BorderPage {
    
    private static final String IMPORTS =
        "<link type=\"text/css\" rel=\"stylesheet\" href=\"{0}/assets/css/imports.css\" title=\"Style\"/>\n"
        + "<script type=\"text/javascript\" src=\"{0}/assets/js/imports.js\"></script>\n"
        + "<script type=\"text/javascript\">addLoadEvent(function() '{' initMenu(); '}');</script>";

    /**
     * Provides an optimized home page imports.
     *
     * @see net.sf.click.Page#getPageImports()
     */
    public PageImports getPageImports() {
        PageImports pageImports = super.getPageImports();

        String imports = ClickUtils.createHtmlImport(IMPORTS, getContext());

        pageImports.addImport(imports);
        pageImports.setInitialized(true);

        return pageImports;
    }
    
    /**
     * @see net.sf.click.Page#getTemplate()
     */
    public String getTemplate() {
        return getPath();
    }
    
}
