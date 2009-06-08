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

/**
 * The ajax-select Page JavaScript.
 */

// Wait until browser DOM is loaded
document.observe("dom:loaded", function() {

    // Observe the selected element's "change" event, which triggers the given function
    $('$selector').observe('change', function(event){
        var select = Event.element(event);
        onCustomerChange(select);
    });

    // This function uses an Ajax request to retrieve customer details that matches
    // the ID value of a select element
    function onCustomerChange(select) {
        // The Ajax url points to the AjaxCustomer Click Page
        new Ajax.Request('${context}/ajax/ajax-customer.htm', {
            method: 'get',
            parameters: {customerId : select.value},
            onSuccess: function(transport){
                // On a successful request, update the given target with the server response
                $('$target').update(transport.responseText);
            },
            onFailure: function(){
                alert('An error occurred.')
            }
        });
    }
});
