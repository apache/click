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

// This demo uses jQuery but also contains a DateField which depends on Prototype.
// Below we use jQuery.noConflict() in order for jQuery and Prototype to work together:
// http://docs.jquery.com/Using_jQuery_with_Other_Libraries
jQuery.noConflict();

// Generally it is not recommended to use two JavaScript libraries together. Instead
// it is highly recommended to use the third-party Click Calendar instead of DateField
// when using jQuery or another JS library besides Prototype. See http://code.google.com/p/click-calendar/

// Register a function that is invoked as soon as the DOM is loaded
jQuery(document).ready(function() {

    // Register a 'click' handler on the submit button
    jQuery("#form_save, #form_cancel").live('click', function(event){
        // Post form to server
        postForm(event);

        // Prevent the default browser behavior of navigating to the link
        return false;
    })
});

function postForm(event) {
    // Retrieve the Form and submit button elements
    var form = jQuery("#form");
    var submit = jQuery(event.currentTarget);

    // The server URL can be retrieved from the Form 'action' event
    var url = form.attr('action');
    var formData = form.serialize();
    formData+='&'+form.attr('id')+'=1';
    formData+='&'+submit.attr('name')+'='+submit.attr('value');

    jQuery.post(url, formData, function(data) {
        // Replace the entire Form with the server response
        form.replaceWith(data);

        // Replacing the Form with the Form from the Ajax response, means all
        // event bindings are lost. For example the DateField button won't show
        // the Calendar. Here we find the DateField setup script, and evaluate it
        var dateSetupScript = jQuery('#form_date-js-setup').html();
        eval(dateSetupScript);

        // Provide user feedback
        var feedbackDiv = jQuery('#feedback');

        // Check for form errors
        if(jQuery('#form-errors').length == 0) {
            feedbackDiv.removeClass('errorMsg').addClass('infoMsg');

            // Set feedback message depending on which button was clicked
            if (submit.attr('name') == 'save') {
                feedbackDiv.html("Form data saved");
            } else {
                feedbackDiv.html("Form cleared");
            }
        } else {
            feedbackDiv.removeClass('infoMsg').addClass('errorMsg');
            feedbackDiv.html("Form contains errors");
        }
    });
}
