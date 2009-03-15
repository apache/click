/*
 * Copyright 2004-2008 Malcolm A. Edgar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.sf.click.service;

import java.util.Map;

/**
 *
 * @author Bob Schellink
 */
public interface ModuleService {
    
    public void loadModules(ConfigService configService) throws Exception;

    public void onInit(ConfigService configService);
    
    public void deployModules(ConfigService configService, String moduleName);
    
    public void loadModule(ConfigService configService, String moduleName);

    public void onDestroy(ConfigService configService);
    
    public Map getModules();
}