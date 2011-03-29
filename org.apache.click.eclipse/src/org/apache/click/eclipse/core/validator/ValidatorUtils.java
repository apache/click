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

import org.eclipse.core.resources.IFile;
import org.eclipse.wst.validation.internal.core.Message;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.eclipse.wst.validation.internal.provisional.core.IValidator;

/**
 * Provides utility methods for validation.
 * 
 * @author Naoki Takezoe
 */
public class ValidatorUtils {
	
	/**
	 * Creates the warning message.
	 * 
	 * @param validator the instance of IValidator
	 * @param reporter the instanceof IReporter
	 * @param file the instanceof IFile which will be marked
	 * @param id the message id
	 * @param params the message parameters
	 * @param start ths start offset or -1
	 * @param length the length or -1
	 * @param line the line number or -1
	 */
	public static void createWarningMessage(IValidator validator, IReporter reporter, IFile file, 
			String id, String[] params, int start, int length, int line){
		
		Message message = new Message();
		message.setSeverity(IMessage.NORMAL_SEVERITY);
		if(line>=0){
			message.setLineNo(line);
		}
		if(start>=0){
			message.setOffset(start);
			message.setLength(length);
		}
		message.setBundleName("net.sf.clickide.core.validator.validation");
		message.setId(id);
		message.setParams(params);
		message.setTargetObject(file);
		reporter.addMessage(validator, message);
	}

}
