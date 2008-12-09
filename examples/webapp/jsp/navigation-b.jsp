<!--
   Licensed to the Apache Software Foundation (ASF) under one
   or more contributor license agreements.  See the NOTICE file
   distributed with this work for additional information
   regarding copyright ownership.  The ASF licenses this file
   to you under the Apache License, Version 2.0 (the
   "License"); you may not use this file except in compliance
   with the License.  You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing,
   software distributed under the License is distributed on an
   "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
   KIND, either express or implied.  See the License for the
   specific language governing permissions and limitations
   under the License.
-->

You are currently on page <span>NavigationB</span>.
<p>

<h4>Forward</h4>

To forward to <b>NavigationA</b> click <a href="${forwardLink.href}">here</a>.

<pre class="javaCode">
    setForward(<span class='st'>"/navigation-a.jsp"</span>);
</pre>

To forward to <b>NavigationA</b> passing the parameter ${forwardParamLink.value} click
<a href="${forwardParamLink.href}">here</a>.

<pre class="javaCode">
    getContext().getRequest().setAttribute(<span class='st'>"param"</span>, param);
    setForward(<span class='st'>"/navigation-a.jsp"</span>);
</pre>

<h4 style="margin-top:2em;">Redirect</h4>

To redirect to <b>NavigationA</b> click <a href="${redirectLink.href}">here</a>.

<pre class="javaCode">
    setRedirect(<span class='st'>"/navigation-a.jsp"</span>);
</pre>

To redirect to <b>NavigationA</b> passing the parameter ${redirectParamLink.value} click
<a href="${redirectParamLink.href}">here</a>.

<pre class="javaCode">
    setRedirect(<span class='st'>"/navigation-a.jsp?param="</span> + param);
</pre>


<p style="margin-top:2em;">
Take notice of the different URLs in your browser when you use forward and redirect.
