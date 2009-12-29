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
package org.apache.click.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Provides a Page field auto binding annotation.
 * <p/>
 * Note if a &#64;Bindable field's visibility is not public then Click will set
 * the field to be accessible using reflection. If the Java application server
 * has restricted security policies in place then this may cause a
 * SecurityException to be thrown. In these environments you can either modify
 * your fields visibility to be public or modify your servers Java security
 * policy.
 *
 * <h3>Bindable Example</h3>
 *
 * <pre class="prettyprint">
 * public class BindableDemo extends Page {
 *
 *     // ActionLink automatically added to Page control list
 *     &#64;Bindable protected ActionLink link = new ActionLink();
 *
 *     // Message string is automatically added to Page model
 *     &#64;Bindable protected String message;
 *
 *     public BindableDemo() {
 *
 *         // Listener method invoked when link clicked
 *         link.setActionListener(new ActionListener() {
 *             public boolean onAction(Control source) {
 *                 // message added to page mode and rendered in template
 *                 message = "I was clicked";
 *                 return true;
 *             }
 *         });
 *     }
 *
 * } </pre>
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Bindable {
}
