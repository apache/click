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
 * The ajax-select JavaScript template.
 */

// Wait until browser DOM is loaded
document.observe("dom:loaded", function() {

    // Observe the selected element's "change" event, which triggers the given function.
    // Note that $selector is a Velocity variable passed in from the AjaxSelect.java Page
    $('$selector').observe('change', function(event){

        // Retrieve the source of the event, in this case the Select control
        var select = Event.element(event);

        // $target, $context and $path are Velocity variables that are passed in
        // from the AjaxSelect.java Page
        new Ajax.Updater('$target', '$context$path', {
            method: 'get',
            parameters: {'pageAction' : 'onChangeCustomer', 'customerId' : select.value}
        });
    });
});
