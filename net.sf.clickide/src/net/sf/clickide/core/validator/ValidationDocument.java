package net.sf.clickide.core.validator;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.eclipse.core.resources.IFile;

/**
 * 
 * @author Naoki Takezoe
 */
public class ValidationDocument {
	
	private String[] lines;
	
	/**
	 * The constructor.
	 * 
	 * @param file the <code>IFile</code> instance
	 * @throws Exception
	 */
	public ValidationDocument(IFile file) throws Exception {
		InputStream in = file.getContents();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		int len = 0;
		byte[] buf = new byte[1024 * 8];
		while((len = in.read(buf))!=-1){
			out.write(buf,0,len);
		}
		byte[] result = out.toByteArray();
		in.close();
		out.close();
		
		String source = new String(result, file.getCharset());
		source = source.replaceAll("\r\n", " \n");
		source = source.replaceAll("\r", "\n");
		
		this.lines = source.split("\n");
	}
	
	/**
	 * Returns the start offset of the specified line.
	 * 
	 * @param line the line number
	 * @return the start offset of the specified line
	 */
	public int getOffsetByLine(int line){
		int offset = 0;
		for(int i=0;i<line;i++){
			offset = offset + this.lines[i].length() + 1;
		}
		return offset;
	}
	
	/**
	 * Returns the length of the specified line.
	 * 
	 * @param line the line number
	 * @return the length of the specified line 
	 */
	public int getLineLength(int line){
		return this.lines[line].length();
	}

}
