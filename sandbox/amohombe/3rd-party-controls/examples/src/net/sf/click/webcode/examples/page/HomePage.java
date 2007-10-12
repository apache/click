package net.sf.click.webcode.examples.page;

import net.sf.click.Page;
import net.sf.click.control.PageLink;
import net.sf.click.webcode.examples.page.misc.CssImageMapPage;
import net.sf.click.webcode.examples.page.misc.ResizeableTextAreaPage;
import net.sf.click.webcode.examples.page.webcode.EditAreaPage;
import net.sf.click.webcode.examples.page.webcode.SyntaxHighlightPage;

/**
 * 
 */
public class HomePage extends Page {
    public String title = "Home Page";
    public PageLink editAreaLink = new PageLink(EditAreaPage.class);
    public PageLink shLink = new PageLink(SyntaxHighlightPage.class);
//    public PageLink resizeableTextAreaLink = new PageLink(ResizeableTextAreaPage.class);
//    public PageLink cssImageMapLink = new PageLink(CssImageMapPage.class);
//    public PageLink timeLink = new PageLink(TimePage.class);
    
    
}
