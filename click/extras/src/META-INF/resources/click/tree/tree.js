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
 * This script provides expand/collapse functionality to the Click Tree control.
 * When user click on a expand or collapse icon, the stylesheet class of the
 * element must be dynamically swapped. So if a expand icon is clicked, the
 * class 'expanded' must be swapped with 'collapsed' so that the new icon
 * can be displayed.
 *
 * Also when the expand/collapse icon is clicked, the tree should be hidden
 * or made visible, by swapping the class of the <ul> element from 'show'
 * to 'hide' and vice versa.
 *
 * This script must also handle the fact that some nodes are leaf nodes and
 * should be ignored.
 */

/* Defines the expand/collapse strings defined in Tree.java */
var expansionArray = ["expanded","collapsed"];

/* Defines the visibility strings defined in Tree.java */
var visibilityArray = ["show","hide"];

/* Defines lead string defined in Tree.java */
var leafString = "leaf";

/* Defines the postfix appended to the last node of a <ul> element */
var lastNodeString = "LastNode";

/*
 * Adds the class to the specified objects class list, if the class is not
 * already present in the class list of the object.
 */
function addClass(object,clazz){
    if(!classExists(object,clazz)) {
        object.className += object.className ? ' ' + clazz : clazz;
    }
}

/*
 * Removes only the specified class from the objects class list
 */
function removeClass(object,clazz){
    var rep=object.className.match(' ' + clazz) ? ' ' + clazz : clazz;
    object.className=object.className.replace(rep,'');
}

/*
 * Check if object already contains the specified class
 */
function classExists(object,clazz){
    return new RegExp('\\b' + clazz + '\\b').test(object.className);
}

/*
 * Removes str2 from str1 if str1 contains str2.
 */
function removeString(str1, str2) {
    var result = str1;
    var index = str1.indexOf(str2);
    if(index >= 0) {
        result = str1.substring(0, index);
    }
    return result;
}

/*
 * Stop the event propogation to other elements. Not sure if this is really needed?
 * Workaround since ie does not support stopPropagation.
 */
function stopPropagation(event) {
    if (window.event && window.event.cancelBubble !== null) {
        window.event.cancelBubble = true;
    }
    else {
        event.stopPropagation();
    }
}

function isClassExpanded(object) {

    currentClassName = object.className;
    if (currentClassName.indexOf(expansionArray[1]) >= 0) {
        return false;
    }
    return true;
}

/*
 * Invoked when user performs expand or collapse operation.
 */
function handleNodeExpansion(objectArg, event, expandId, iconId) {
    stopPropagation(event);

    var span = document.getElementById(expandId);

    //do not handle leaf nodes
    if(isLeaf(span)) {
        return;
    }

    var index = isClassExpanded(span) ? 0 : 1;

    var newClassName = expansionArray[1 - index];
    var oldClassName = expansionArray[index];

    handleIcons(iconId, newClassName);

    var currentClassName = span.className;
    var isLastNode = currentClassName.indexOf(lastNodeString) > 0 ? true : false;
    if(isLastNode) {
        newClassName = newClassName + lastNodeString;
        oldClassName = oldClassName + lastNodeString;
    }

    removeClass(span, oldClassName);
    addClass(span, newClassName);
    handleChildMenuIfExists(span, index);
}

/*
 * Hides or displays the <ul> element
 */
function handleChildMenuIfExists(objectArg, newIndex) {
    var menuList = objectArg.getElementsByTagName("ul");
    if(menuList.length <= 0) {
        return;
    }
    var childMenu = menuList[0];

    if(classExists(childMenu, visibilityArray[newIndex])) {
        removeClass(childMenu, visibilityArray[newIndex]);
    }
    addClass(childMenu, visibilityArray[1 - newIndex]);
}

/*
 * Handles the display of the expand/collapse icons
 */
function handleIcons(iconId, newClassName) {
    var span = document.getElementById(iconId);

    if(span==null) {
        return; //skip iconId target is null. This occurs when customIcon is set
    }
    if(isLeaf(span)) {
        return; //we do not handle leaf nodes
    }
    var currentClassName = span.className;
    removeClass(span, currentClassName);
    addClass(span, newClassName + "Icon");
}

/*
 * Check if the specified objectArg is a leaf node or not.
 */
function isLeaf(objectArg) {
    if(objectArg.className.indexOf(leafString) >= 0)
        return true;
    return false;
}
