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

    // Register a 'live' click handler on every link inside the table.
    // Note: the 'live' binding is a jQuery function that keeps the event bound even if the Table DOM is replaced
    // http://api.jquery.com/live/
    jQuery("#table td a").live('click', function(event){
        var callServer = true;
        if (jQuery(event.currentTarget).text()=="Delete") {
            callServer = window.confirm('Please confirm delete');
        }

        if (callServer) {
            // Make ajax request
            editOrDeleteCustomer(event);
        }

        // Prevent the default browser behavior of navigating to the link
        event.preventDefault();
    });

    // Register a 'live' click handler on the sorting links of the table header (<th>),
    // as well as the paging links on the <div> banner.
    // Note: the 'live' binding is a jQuery function that keeps the event bound even if the Table DOM is replaced
    // http://api.jquery.com/live/
    jQuery("#table th a, .pagelinks a").live('click', function(event){
        // Make ajax request
        sortTable(event);

        // Prevent the default browser behavior of navigating to the link
        return false;
    })
});

function editOrDeleteCustomer(event) {
    var link = jQuery(event.currentTarget);
    var url = link.attr('href');
    jQuery.get(url, function(data) {
        // Update the result div with the server response
        jQuery("#result").html("<p class='infoMsg'>" + data + "</p>");
    });
}

function sortTable(event) {
    var link = jQuery(event.currentTarget);
    var url = link.attr('href');
    jQuery.get(url, function(data) {
        // Update the table container with the new table
        jQuery("#tableContainer").html(data);
    });
}