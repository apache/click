<!doctype html>

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


    <%-- Menu --%>
    <table id="menuTable" border="0" width="100%" cellspacing="0" cellpadding="0" style="margin-top: 2px;">
    <tr>
      <td>
        <div class="menustyle" id="menu">
          <ul class="menubar" id="dmenu">
            <c:forEach items="${rootMenu.children}" var="topMenu">
              <li class="topitem">${topMenu}
                <ul class="submenu"
                  <c:forEach items="${topMenu.children}" var="subMenu">
                    ><li>${subMenu}</li
                  </c:forEach>
                ></ul>
              </li>
            </c:forEach>
            <li class="topitem"><a target="_blank" href="${context}/source-viewer.htm?filename=WEB-INF/classes/${srcPath}" title="Page Java source"><img border="0" class="link" alt="" src="${context}/assets/images/lightbulb1.png"/> Page Java</a>
            </li>
            <li class="topitem"><a target="_blank" href="${context}/source-viewer.htm?filename=${path}" title="Page Content source"><img border="0" class="link" alt="" src="${context}/assets/images/lightbulb2.png"/> Page HTML</a>
            </li>
          </ul>
        </div>
      </td>
    </tr>
    </table>
  </div>

  <%-- Page Content --%>
  <div class="content">
	  <h2>${title}</h2>
    <p/>
    <jsp:include page='${forward}' flush="true"/>
	</div>

</div>

${jsElements}

</body>
</html>
