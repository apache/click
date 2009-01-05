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

/*
 * This script provides select/deselect highlighting functionality to the Click 
 * CheckboxTree control. So when a checkbox is clicked the node's value 
 * must also be selected (highlighted) or deselected (not highlighted).
 * Clicking on a checkbox, the stylesheet class of the node's value must be 
 * dynamically swapped. So the class 'unselected' must be swapped with 
 * 'selected' and vice versa, so that the node value can be highlighted or not.
 *
 * Also if a node's value is clicked the checkbox must be selected or 
 * deselected.
 */

/* Defines the select/deselect defined in Tree.java */
var selectionArray = ["selected","unselected"];

/*
 * Replaces class1 with class2 on specified object. 
 * If the class1 is not a class of the object, class2 
 * is simply added
 */
function replaceOrAddClass(object, class1 ,class2) {
    if(classExists(object,class1)) {
         object.className = object.className.replace(class1,class2);
    } else if (!classExists(object,class2)) {
        addClass(object, class2);
    }
}

/*
 * Handles node selection events. This function
 * finds the specified selectId element, and checks if that
 * element's class value is "selected" or "unselected", before
 * swapping the value.
 */
function handleNodeSelection(objectArg, event, selectId, checkboxId, selectChildren) {
    stopPropagation(event);
    
    var span = document.getElementById(selectId);

    index = 0;
    if(classExists(span,selectionArray[1])) {
        index = 1;
    }
    handleCheckboxSelection(checkboxId, index, selectChildren);
}

/*
 * Handles checkbox selection events. This function
 * retrieves the specified checkbox with checkboxId
 * and check/unchecks based on the index value.
 */
function handleCheckboxSelection(checkboxId, index, selectChildren) {
    var checkbox = document.getElementById(checkboxId);
    var checked = (index == 1) ? true : false;
    
    var elements = null;
    if(selectChildren) {
        elements = checkbox.parentNode.getElementsByTagName("input");
    } else {        
        elements = new Array(checkbox);
    }
    handleCheckboxesSelection(elements, checked);
}

/*
 * Called when user clicks on the checkbox. This function 
 * finds the <span> holding the selection state and swaps
 * the classes
 */
function onCheckboxClick(checkbox, event, selectId, selectChildren) {
    stopPropagation(event);

    var elements = null;
    if(selectChildren) {
        elements = checkbox.parentNode.getElementsByTagName("input");
    } else {
        elements = new Array(checkbox);
    }
    handleCheckboxesSelection(elements, checkbox.checked);
}

/*
 * Selects/deselects all the specified checkboxes. 
 */
function handleCheckboxesSelection(checkboxes, checked) {
    for (var i=0; i<checkboxes.length; i++) {
        var input = checkboxes[i];
        if(input.type=='checkbox') {
            input.checked = checked;
            handleCheckboxSibling(input);
        }
    }
}

/*
 * Select/unselect the specified checkbox sibling element.
 */
function handleCheckboxSibling(checkbox) {
    //TODO get next sibling
    var text = checkbox.nextSibling;
    if(checkbox.checked) {
        replaceOrAddClass(text, selectionArray[1], selectionArray[0]);
    } else {
        replaceOrAddClass(text, selectionArray[0], selectionArray[1]);
    }
}