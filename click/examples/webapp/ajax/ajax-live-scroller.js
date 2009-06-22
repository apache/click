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

$(document).ready(function(){
    // Dynamically create a fixed scroll indicator at the bottom right corner of the screen
    jQuery("<div id='scroll-indicator' style='border: 1px solid white;"
        + "background: #EEEEEE; padding: 10px; position: fixed; display: none;"
        + "right: 25; bottom: 25; width: 100px;"

        // IE6 doesn't support fixed positioning. Workaround below
        + "_position:absolute;_top:expression(-25 + document.body.scrollTop+document.body.clientHeight-this.clientHeight);"

        + "'>Loading...</div>")
        .insertAfter(jQuery('table.page')); // Insert the indicator after the table defined in border-template.htm

    // Current number of records loaded from server
    var offset = 0;

    // Indicates the number of records to return from server. The PageSize value
    // is passed in from the AjaxLiveScroller.java Page.
    var pageSize = $pageSize;

    // Flag indicating if data is being loaded from server
    var loading = false;

    function loadMoreData() {
        // Increment offset with the specified number of pages
        offset+=pageSize;

        // Toggle flag to indicate that loading started
        loading = true;

        // Show the scroll indicator
        jQuery('#scroll-indicator').css("display", "block");

        jQuery.get('${context}${path}?offset=' + offset, function(data){
                // Toggle flag to indicate that loading is finished
                loading = false;

                // If the server returns the "LAST_RECORD" indicator, we
                // can unbind the window scroll event as no further data
                // will be returned
                if (data.indexOf("LAST_RECORD") >= 0) {
                    $(window).unbind('scroll');
                } else {
                    // Otherwise we add the new data after the last customer
                    $(".customer:last").after(data);
                }

                jQuery('#scroll-indicator').css("display", "none");
            });
    }

    // Bind a scroll event to the window to detect if more data needs to be loaded
    $(window).bind('scroll', function() {

        // If we are currently retrieving data, don't load again
        if (loading) {
            return;
        }

        // When scrolling near the bottom of the page, load more data from server
        if (nearEndOfPage()) {
            loadMoreData();
        }
    });

    // Return true if scrolling is 85% at the end of the page
    function nearEndOfPage() {
        return $(window).scrollTop() > (0.85 * $(document).height() - $(window).height());
    }
})
