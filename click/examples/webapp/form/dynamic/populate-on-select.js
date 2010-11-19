// #*
// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.
// *#

/**
 * Prints out the selected option for each of the three Select Controls
 */
function printValues() {
    var log = document.getElementById('log');
    log.style.display = 'block';

    // Select ids will be passed in from Java
    var state = document.getElementById('${stateId}');
    var city = document.getElementById('${cityId}');
    var suburb = document.getElementById('${suburbId}');

    var html = 'Selected state: key -> ' + state.value + ', value -> ' + state.options[state.selectedIndex].text;
    html+="<br>" + 'Selected city: key -> ' + city.value  + ', value -> ' + city.options[city.selectedIndex].text;
    html+="<br>" + 'Selected suburb: key -> ' + suburb.value  + ', value -> ' + suburb.options[suburb.selectedIndex].text;
    log.innerHTML = html;
}

/**
 * This method is invoked when a Select control is changed.
 *
 * @id specifies the dependent Select control which value to clear
 * @form specified the form that must be submitted
 */
function handleChange(id, form) {
    var select=document.getElementById(id);
    if(select != null) {
        // Clear dependent Select value
        select.selectedIndex=0;
    }
    // Submit form
    form.submit();
}