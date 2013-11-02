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
    jQuery("<div id='scroll-indicator' style='border: 1px solid navy;"
        + "background: #334ECF; color: #FFFFFF; padding: 10px; position: fixed; display: none;"
        + "right: 25px; bottom: 25px; width: 75px;"
        + "'>Loading...</div>")
        .insertAfter(jQuery('div.page')); // Insert the indicator after the main div defined in border-template.htm

    // Current number of records loaded from server
    var offset = 0;

    // Indicates the number of records to return from server. The PageSize value
    // is passed in from the AjaxLiveScroller.java Page.
    var pageSize = $pageSize;

    // Flag indicating if data is being loaded from server
    var loading = false;

    function loadMoreData() {
        // Toggle flag to indicate that loading started
        loading = true;

        // Increment offset with the specified number of pages
        offset+=pageSize;

        // Show the scroll indicator
        jQuery('#scroll-indicator').css("display", "block");

        // Perform Ajax GET request. Note pageAction parameter which specifies the onScroll page method
        jQuery.get('${context}${path}?pageAction=onScroll&offset=' + offset, function(data){
            // If the server response contains no div element, unbind the window scroll event as no further data
            // will be returned
            if (data.indexOf("<div") < 0) {
                jQuery(window).unbind('scroll');
            } else {
                // Otherwise we add the new data after the last customer
                jQuery(".customer:last").after(data);
            }

            jQuery('#scroll-indicator').css("display", "none");

            // Toggle flag to indicate that loading is finished
            loading = false;
        });
    }

    // Bind a scroll event to the window to detect if more data needs to be loaded
    jQuery(window).bind('scroll', function() {

        // If we are currently retrieving data, don't load again
        if (loading) {
            return;
        }

        // When scrolling near the bottom of the page, load more data from server
        if (nearEndOfPage()) {
            // Incase we scrolled to end of page, move scrollbar slightly back
            jQuery(window).scrollTop(jQuery(window).scrollTop() - 1);
            loadMoreData();
        }
    });

    // Return true if scrolling is 85% at the end of the page
    function nearEndOfPage() {
        return jQuery(window).scrollTop() > getFetchThreshold();
    }

    // Return the threshold when more data should be fetched, equates to 85% of the document height
    function getFetchThreshold() {
        return 0.85 * jQuery(document).height() - jQuery(window).height();
    }
});
