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
package org.apache.click.eclipse.core.facet;

import java.util.Set;

import org.eclipse.wst.common.componentcore.datamodel.FacetInstallDataModelProvider;

/**
 * 
 * @author Naoki Takezoe
 */
public class ClickFacetInstallDataModelProvider extends FacetInstallDataModelProvider {
	
	public static final String ROOT_PACKAGE = "rootPackage";
	public static final String USE_SPRING = "useSpring";
	public static final String USE_CAYENNE = "useCayenne";
	public static final String USE_PERFORMANCE_FILTER = "usePerformanceFilter";
	
	public Set<String> getPropertyNames() {
		@SuppressWarnings("unchecked")
		Set<String> names = super.getPropertyNames();
		names.add(USE_SPRING);
		names.add(USE_CAYENNE);
		names.add(USE_PERFORMANCE_FILTER);
		names.add(ROOT_PACKAGE);
		return names;
	}

	public Object getDefaultProperty(String propertyName) {
		if (propertyName.equals(FACET_ID)) {
			return "click";
		} else if(propertyName.equals(USE_SPRING)){
			return new Boolean(false);
		} else if(propertyName.equals(USE_CAYENNE)){
			return new Boolean(false);
		} else if(propertyName.equals(USE_PERFORMANCE_FILTER)){
			return new Boolean(false);
		} else if(propertyName.equals(ROOT_PACKAGE)){
			return "";
		}
		return super.getDefaultProperty(propertyName);
	}
	
}
