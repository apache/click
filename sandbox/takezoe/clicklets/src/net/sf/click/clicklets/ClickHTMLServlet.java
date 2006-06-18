package net.sf.click.clicklets;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.click.ClickServlet;
import net.sf.click.Page;
import net.sf.click.util.ErrorPage;

/**
 * 
 * @author Naoki Takezoe
 */
public class ClickHTMLServlet extends ClickServlet {
	
	private static final long serialVersionUID = -6933056629831756154L;
	
	private static Map timestamp = Collections.synchronizedMap(new HashMap());
	
	protected void processPage(Page page) throws Exception {
		
		if(page instanceof ErrorPage){
			super.processPage(page);
			return;
		}
		
		String templatePath = page.getTemplate();
		File inputFile = new File(getServletContext().getRealPath(templatePath));
		FileInputStream in = new FileInputStream(inputFile);
		List forms = null;
		synchronized (timestamp) {
			if(timestamp.get(inputFile)==null || ((Long)timestamp.get(inputFile)).longValue() != inputFile.lastModified()){
				File outputFile = new File(inputFile.getParentFile(), inputFile.getName() + ".vm");
				FileOutputStream out = new FileOutputStream(outputFile);
				
				forms = ClickHTMLCompiler.compile(page, in, out);
				timestamp.put(inputFile, new Long(inputFile.lastModified()));
			}
		}
		
		if(forms==null){
			forms = ClickHTMLCompiler.compile(page, in, null);
		}
		
		super.processPage(new ClickHTMLPage(page, forms));
	}

}
