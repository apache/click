package net.sf.click.extensions;

import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import net.arnx.jsonic.JSON;
import net.sf.click.Page;

/**
 * The base class of JSON pages.
 * <p>
 * This page class does not have a template because response is written by this class.
 * So auto-mapping does not work for JSON pages.
 * You have to register extended page class to <tt>click.xml</tt>.
 * 
 * @author Naoki Takezoe
 */
public class AbstractJSONPage extends Page {
	
	protected String contentType = "application/x-javascript; charset=utf-8";
	protected Object contents;
	
	/**
	 * Sets the JavaBean which is encoded to JSON.
	 * 
	 * @param contents the JavaBean which is encoded to JSON
	 */
	protected void setContents(Object contents){
		if(contents == null){
			throw new IllegalArgumentException("contents shouldn't be null.");
		}
		this.contents = contents;
	}
	
	/**
	 * Sets the Content-Type header value.
	 * 
	 * @param contentType the content type,
	 *   default value is <code>"application/x-javascript; charset=utf-8"</code>.
	 */
	protected void setContentType(String contentType){
		if(contentType == null){
			throw new IllegalArgumentException("contentType shouldn't be null.");
		}
		this.contentType = contentType;
	}
	
	/**
	 * This method returns <code>null</code> to not render template,
	 * because this class writes JSON response.
	 */
	@Override public String getPath() {
		return null;
	}
	
	/**
	 * Writes JSON response in this method.
	 */
	@Override public void onRender() {
		if(this.contents == null){
			throw new IllegalStateException("JSON contents is not specified.");
		}
		
		HttpServletResponse res = getContext().getResponse();
		OutputStream out = null;
		
		try {
			res.setContentType(this.contentType);
			out = res.getOutputStream();
			out.write(JSON.encode(this.contents).getBytes("UTF-8"));
			
			res.flushBuffer();
			
		} catch(Exception ex){
			throw new RuntimeException(ex);
		} finally {
			ExtUtils.closeQuietly(out);
		}
	}

}
