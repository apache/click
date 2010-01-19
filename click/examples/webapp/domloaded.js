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
 * This script was adapted from the following online articles:
 *
 * http://www.cherny.com/webdev/26/domloaded-object-literal-updated
 * http://simon.incutio.com/archive/2004/05/26/addLoadEvent 
 */

 /*
  * Usage: call addLoadEvent one or more times, passing in a function as
  * argument each time, for example:
  *
  *    function doSomething() {
  *       // method1
  *    }
  *    addLoadEvent(doSomething);
  *
  *    addLoadEvent(function() {
  *        // method2
  *    });
  */
addLoadEvent = (function(func){
    DomLoaded.load(func);
})

var DomLoaded =
{
  isReady: false,
	onload: [],
	loaded: function()
	{
		if (arguments.callee.done) return;
    //comment the line below and use isReady instead? TEST
		arguments.callee.done = true;
    DomLoaded.isReady = true;
		for (i = 0;i < DomLoaded.onload.length;i++) DomLoaded.onload[i]();

    // Remove event listener to avoid memory leak
    if (document.addEventListener) 
  		document.removeEventListener( "DOMContentLoaded", DomLoaded.loaded, false );

			// Remove script element used by IE hack
			//if( !window.frames.length ) {// don't remove if frames are present (#1187)
			//	var elem = document.getElementById("__ie_onload");
      //}
	},
	load: function(fireThis)
	{
    //If the dom is ready, execute the function and return.
    if(DomLoaded.isReady) return fireThis();
		this.onload.push(fireThis);
		if (document.addEventListener) 
			document.addEventListener("DOMContentLoaded", DomLoaded.loaded, false);
		if (/KHTML|WebKit/i.test(navigator.userAgent))
		{ 
			var _timer = setInterval(function()
			{
				if (/loaded|complete/.test(document.readyState))
				{
					clearInterval(_timer);
					delete _timer;
					DomLoaded.loaded();
				}
			}, 10);
		}
		/*@cc_on @*/
		/*@if (@_win32)
		var proto = "src='javascript:void(0)'";
		if (location.protocol == "https:") proto = "src=//0";
		document.write("<scr"+"ipt id=__ie_onload defer " + proto + "><\/scr"+"ipt>");
		var script = document.getElementById("__ie_onload");
		script.onreadystatechange = function() {
		    if (this.readyState == "complete") {
		        DomLoaded.loaded();
		    }
		};
		/*@end @*/
	   //window.onload = DomLoaded.loaded;
     //window.addEventListener("onload", DomLoaded.loaded, false);
     addEvent( window, "load", DomLoaded.loaded);

	}
};

// written by Dean Edwards, 2005
// with input from Tino Zijdel - crisp@xs4all.nl
// http://dean.edwards.name/weblog/2005/10/add-event/
// and
// http://therealcrisp.xs4all.nl/upload/addEvent_dean.html
function addEvent(element, type, handler)
{
	if (element.addEventListener)
		element.addEventListener(type, handler, false);
	else
	{
		if (!handler.$$guid) handler.$$guid = addEvent.guid++;
		if (!element.events) element.events = {};
		var handlers = element.events[type];
		if (!handlers)
		{
			handlers = element.events[type] = {};
			if (element['on' + type]) handlers[0] = element['on' + type];
			element['on' + type] = handleEvent;
		}
	
		handlers[handler.$$guid] = handler;
	}
}
addEvent.guid = 1;

function removeEvent(element, type, handler)
{
	if (element.removeEventListener)
		element.removeEventListener(type, handler, false);
	else if (element.events && element.events[type] && handler.$$guid)
		delete element.events[type][handler.$$guid];
}

function handleEvent(event)
{
	event = event || fixEvent(window.event);
	var returnValue = true;
	var handlers = this.events[event.type];

	for (var i in handlers)
	{
		if (!Object.prototype[i])
		{
			this.$$handler = handlers[i];
			if (this.$$handler(event) === false) returnValue = false;
		}
	}

	if (this.$$handler) this.$$handler = null;

	return returnValue;
}

function fixEvent(event)
{
	event.preventDefault = fixEvent.preventDefault;
	event.stopPropagation = fixEvent.stopPropagation;
	return event;
}
fixEvent.preventDefault = function()
{
	this.returnValue = false;
}
fixEvent.stopPropagation = function()
{
	this.cancelBubble = true;
}

// This little snippet fixes the problem that the onload attribute on the body-element will overwrite
// previous attached events on the window object for the onload event
if (!window.addEventListener)
{
	document.onreadystatechange = function()
	{
		if (window.onload && window.onload != handleEvent)
		{
			addEvent(window, 'load', window.onload);
			window.onload = handleEvent;
		}
	}
}