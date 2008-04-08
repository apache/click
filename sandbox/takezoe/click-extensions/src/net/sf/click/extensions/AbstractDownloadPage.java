package net.sf.click.extensions;

import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.click.Page;

/**
 * The base class of file download pages.
 * <p>
 * For example:
 * <pre>
 * <span class="kw">public</span> SampleDownloadPage <span class="kw">extends</span> DownloadPage {
 *   <span class="kw">public</span> SampleDownloadPage(){
 *     setFileName(<span class="st">"sample.txt"</span>);
 *     setContents(SampleDownloadPage.class.getResourceAsStream(<span class="st">"sample.txt"</span>));
 *   }
 * }</pre>
 * This page class does not have a template because response is written by this class.
 * So auto-mapping does not work for file download pages.
 * You have to register extended page class to <tt>click.xml</tt>.
 * 
 * @author Naoki Takezoe
 */
public abstract class AbstractDownloadPage extends Page {
	
	protected String contentType = "application/octet-stream";
	protected String contentDisposition = "attachment";
	protected String fileName;
	protected InputStream contents;
	
	/**
	 * Sets the Content-Type header value.
	 * 
	 * @param contentType the content type,
	 *   default value is <code>"application/octet-stream"</code>.
	 */
	protected void setContentType(String contentType){
		if(contentType == null){
			throw new IllegalArgumentException("contentType shouldn't be null.");
		}
		this.contentType = contentType;
	}
	
	/**
	 * Sets the download filename.
	 * 
	 * @param fileName the filename
	 */
	protected void setFileName(String fileName){
		this.fileName = fileName;
	}
	
	/**
	 * Sets the <code>InputStream</code> of donwload contents.
	 * 
	 * @param contents the donwload contents
	 */
	protected void setContents(InputStream contents){
		if(contents == null){
			throw new IllegalArgumentException("contents shouldn't be null.");
		}
		this.contents = contents;
	}
	
	/**
	 * Sets the Content-Disposition header value.
	 * 
	 * @param contentDisposition <code>"attachment"</code> or <code>"inline"</code>,
	 *    default value is <code>"attachment"</code>.
	 */
	protected void setContentDisposition(String contentDisposition){
		this.contentDisposition = contentDisposition;
	}

	/**
	 * This method returns <code>null</code> to not render template,
	 * because this class writes response contents for file download.
	 */
	@Override public String getPath() {
		return null;
	}

	/**
	 * Writes response for file download in this method.
	 */
	@Override public void onRender() {
		if(this.contents == null){
			throw new IllegalStateException("download contents is not specified.");
		}
		
		HttpServletRequest req = getContext().getRequest();
		HttpServletResponse res = getContext().getResponse();
		OutputStream out = null;
		String fileName = this.fileName;
		String contentDisposition = this.contentDisposition;
		
		try {
			res.setContentType(this.contentType);
			
			if(this.contentType != null){
				String userAgent = req.getHeader("USER-AGENT");
				if(userAgent.indexOf("MSIE") >= 0 && userAgent.indexOf("Opera") < 0){
					fileName = new String(fileName.getBytes("Windows-31J"), "ISO8859_1");
				} else {
					fileName = new String(fileName.getBytes("UTF-8"), "ISO8859_1");
				}
				contentDisposition = contentDisposition + "; filename=\"" + fileName + "\"";
			}
			res.setHeader("Content-Disposition", contentDisposition);
			
			out = res.getOutputStream();
			byte[] buf = new byte[1024 * 8];
			int length = 0;
			while((length = this.contents.read(buf)) >= 0){
				out.write(buf, 0, length);
			}
			
			res.flushBuffer();
			
		} catch(Exception ex){
			throw new RuntimeException(ex);
		} finally {
			ExtUtils.closeQuietly(this.contents);
			ExtUtils.closeQuietly(out);
		}
	}
	
}
