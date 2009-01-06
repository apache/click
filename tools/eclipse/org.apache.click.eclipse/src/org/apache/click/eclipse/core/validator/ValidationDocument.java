/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.click.eclipse.core.validator;

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
