package net.sf.click.extensions;

import java.io.Closeable;

/**
 * 
 * @author Naoki Takezoe
 */
public class ExtUtils {
	
	/**
	 * Closes <code>Closeable</code> without exception.
	 * 
	 * @param closeable the <code>Closeable</code> object to close.
	 */
	public static void closeQuietly(Closeable closeable){
		if(closeable != null){
			try {
				closeable.close();
			} catch(Throwable t){
			}
		}
	}
	
}
