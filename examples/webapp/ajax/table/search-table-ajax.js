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

// Register a function that is invoked as soon as the DOM is loaded
jQuery(document).ready(function() {

    // Register a 'click' handler on the submit button
    jQuery("#form_search").click(function(event){
        $.blockUI({ css: {
            border: 'none',
            padding: '15px',
            backgroundColor: '#000',
            '-webkit-border-radius': '10px',
            '-moz-border-radius': '10px',
            opacity: .5,
            color: '#fff',
            width: '300px'
        } });

        // Post form to server
        postForm(event);

        // Prevent the default browser behavior of navigating to the link
        return false;
    });

    // Register a 'click' handler on the clear button
    jQuery("#form_clear").click(function(event){

    	jQuery("#form_name").val('');

        $.blockUI({ css: {
            border: 'none',
            padding: '15px',
            backgroundColor: '#000',
            '-webkit-border-radius': '10px',
            '-moz-border-radius': '10px',
            opacity: .5,
            color: '#fff',
            width: '300px'
        } });

    	// Post form to server
    	postForm(event);

    	// Prevent the default browser behavior of navigating to the link
    	return false;
    });

	// Register a 'click' handler on the form
	jQuery("#form").submit(function(event){
        $.blockUI({ css: {
            border: 'none',
            padding: '15px',
            backgroundColor: '#000',
            '-webkit-border-radius': '10px',
            '-moz-border-radius': '10px',
            opacity: .5,
            color: '#fff',
            width: '300px'
        } });

		// Post form to server
		postForm(event);

		// Prevent the default browser behavior of navigating to the link
		return false;
	});

    // Register a 'live' click handler on the table paging links on the <div> banner.
    // Note: the 'live' binding is a jQuery function that keeps the event bound even if the Table DOM is replaced
    // http://api.jquery.com/live/
    jQuery(".paging-inline a").live('click', function(event){
        $.blockUI({ css: {
            border: 'none',
            padding: '15px',
            backgroundColor: '#000',
            '-webkit-border-radius': '10px',
            '-moz-border-radius': '10px',
            opacity: .5,
            color: '#fff',
            width: '300px'
        } });

        // Make ajax request
        tablePaging(event);

        // Prevent the default browser behavior of navigating to the link
        return false;
    });
})

function postForm(event) {
    // Retrieve the Form and submit button elements
    var form = jQuery("#form");
    var submit = jQuery(event.currentTarget);

    // The server URL can be retrieved from the Form 'action' event
    var url = form.attr('action');

    // Use jQuery serialize function to serialize the Form controls into key/value pairs
    // Note: the jQuery serialize function will *not* add the button name/value
    // that submitted the form. We will add the submit button name/value manually
    var formData = form.serialize();

    // Append the form ID attribute so that Click can identify the Ajax target Control
    formData+='&'+form.attr('id')+'=1';

    // Append the name/value pair of the Submit button that submitted the Form
    formData+='&'+submit.attr('name')+'='+submit.attr('value');

    jQuery.post(url, formData, function(data) {
        $.unblockUI();

        // Update the target div with the server response and style the div by adding a CSS class
        var div = jQuery('#tableContainer');
        div.html(data);
    });
}

function tablePaging(event) {
    var link = jQuery(event.currentTarget);
    var url = link.attr('href');
    jQuery.get(url, function(data) {
        $.unblockUI();

        // Update the table container with the new table
        jQuery("#tableContainer").html(data);
    });
}

