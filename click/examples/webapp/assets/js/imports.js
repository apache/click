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

// Ensure Click namespace exists
if ( typeof Click == 'undefined' )
    Click = {};

/**
 * DomReady state variables.
 */
if ( typeof Click.domready == 'undefined' ) {
    Click.domready = {
        events: [],
        ready: false,
        run : function() {
            if ( !document.body ) {
              // If body is null run this function after timeout
              return setTimeout(arguments.callee, 13);
            }
            Click.domready.ready=true;
            var e;
            while(e = Click.domready.events.shift()) {
                e();
            }
        }
    }
};

/**
 * This function is based on work done by Dean Edwards, Diego Perini,
 * Matthias Miller, John Resig and Jesse Skinner.
 *
 * http://dean.edwards.name/weblog/2006/06/again/
 * http://simonwillison.net/2004/May/26/addLoadEvent/
 * http://javascript.nwbox.com/IEContentLoaded/
 * http://www.thefutureoftheweb.com/blog/adddomloadevent/
 * http://www.subprint.com/blog/demystifying-the-dom-ready-event-method/
 */
(function() {
    // Handle DOMContentLoaded compliant browsers.
    if (document.addEventListener) {
      document.addEventListener("DOMContentLoaded", function() {
        document.removeEventListener("DOMContentLoaded", arguments.callee, false);
        Click.domready.run();
      }, false);

      // A fallback to window.onload, that will always work
			window.addEventListener( "load",  Click.domready.run, false );

    // If IE event model is used
    } else if ( document.attachEvent ) {
      // ensure firing before onload, maybe late but safe also for iframes
      document.attachEvent("onreadystatechange", function() {
        if (document.readyState === "complete") {
          document.detachEvent("onreadystatechange", arguments.callee);
          Click.domready.run();
        }
      });

			// A fallback to window.onload, that will always work
			window.attachEvent( "onload", Click.domready.run );

      // If IE and not a frame continually check to see if the document is ready
			var toplevel = false;
      try {
				toplevel = window.frameElement == null;
			} catch(e) {}

			if ( document.documentElement.doScroll && toplevel) {
	      (function () {
    	    try {
    			  document.documentElement.doScroll('left');
    		  } catch (e) {
     			  setTimeout(arguments.callee, 1);
    			  return;
 		      }
		      // Dom is ready, run events
		      Click.domready.run();
	      })();
      }
    }
})();

/**
 * Usage: Call Click.addLoadEvent passing in a function to invoke when the DOM is
 * ready:
 *
 *    Example 1:
 *    function something() {
 *       // do something
 *    }
 *    Click.addLoadEvent(something);
 *
 *    Example 2:
 *    Click.addLoadEvent(function() {
 *        // do something
 *    });
 */
Click.addLoadEvent = function(func) {
    // If dom is ready, fire event and return
    if (Click.domready.ready) {
        return func();
    }
    Click.domready.events.push(func);
};

addLoadEvent=Click.addLoadEvent;

function doubleFilter(event) {
    var keyCode;
    if (document.all) {
        keyCode = event.keyCode;
    } else if (document.getElementById) {
        keyCode = event.which;
    } else if (document.layers) {
        keyCode = event.which;
    }

    if (keyCode >= 33 && keyCode <= 43) {
        return false;

    } else if (keyCode == 47) {
        return false;

    } else if (keyCode >= 58 && keyCode <= 126) {
        return false;

    } else {
        return true;
    }
}

function integerFilter(event) {
    var keyCode;
    if (document.all) {
        keyCode = event.keyCode;
    } else if (document.getElementById) {
        keyCode = event.which;
    } else if (document.layers) {
        keyCode = event.which;
    }

    if (keyCode >= 33 && keyCode <= 44) {
        return false;

    } else if (keyCode >= 46 && keyCode <= 47) {
        return false;

    } else if (keyCode >= 58 && keyCode <= 126) {
        return false;

    } else {
        return true;
    }
}

function noLetterFilter(event) {
    var keyCode;
    if (document.all) {
        keyCode = event.keyCode;
    } else if (document.getElementById) {
        keyCode = event.which;
    } else if (document.layers) {
        keyCode = event.which;
    }

    if (keyCode >= 33 && keyCode <= 39) {
        return false;

    } else if (keyCode == 47) {
        return false;

    } else if (keyCode >= 58 && keyCode <= 126) {
        return false;

    } else {
        return true;
    }
}

function setFocus(id) {
    var field = document.getElementById(id);
    if (field && field.focus && field.type != "hidden" && field.disabled != true) {
    	try {
			field.focus();
		} catch (err) {
		}
    }
}

function trim(str) {
    while (str.charAt(0) == (" ")) {
        str = str.substring(1);
      }
      while (str.charAt(str.length - 1) == " ") {
          str = str.substring(0,str.length-1);
      }
      return str;
}

Click.hasClass=function(element,cls){
    var className=element.className;
    if(className) {
        return new RegExp('\\b'+cls+'\\b').test(className);
    }
    return false;
}

Click.addClass=function(element,cls){
    if(!Click.hasClass(element,cls)) {
        element.className += element.className ? ' ' + cls : cls;
    }
}

Click.removeClass=function(element,cls){
    var className=element.className;
    if(!className) return;

    if(className.indexOf(' ')<0) {
        element.className='';
        return;
    }

    var rep = new RegExp('(^|\\s)' + cls + '(?:\\s|$)');
    element.className = className.replace(rep, '$1');
}

Click.setFieldValidClass=function(field) {
    Click.removeClass(field,'error');
}

Click.setFieldErrorClass=function(field) {
    Click.addClass(field,'error');
}

function validateTextField(id, required, minLength, maxLength, msgs) {
    var field = document.getElementById(id);
    if (field) {
        var value = trim(field.value);
        if (required) {
            if (value.length == 0) {
                Click.setFieldErrorClass(field);
                return msgs[0];
            }
        }
        if (required && minLength > 0) {
            if (value.length < minLength) {
                Click.setFieldErrorClass(field);
                return msgs[1];
            }
        }
        if (maxLength > 0) {
            if (value.length > maxLength) {
                Click.setFieldErrorClass(field);
                return msgs[2];
            }
        }
        Click.setFieldValidClass(field);
        return null;
    } else {
        return 'Field ' + id + ' not found.';
    }
}

function validateCheckbox(id, required, msgs) {
    var field = document.getElementById(id);
    if (field) {
        if (required) {
            if (field.checked) {
                return null;
            } else {
                return msgs[0];
            }
        }
    } else {
        return 'Field ' + id + ' not found.';
    }
}

function validateSelect(id, defaultValue, required, msgs) {
    var field = document.getElementById(id);
    if (field) {
        if (required) {
            var value = field.value;
            if (value != defaultValue) {
                Click.setFieldValidClass(field);
                return null;
            } else {
                Click.setFieldErrorClass(field);
                return msgs[0];
            }
        }
    } else {
        return 'Field ' + id + ' not found.';
    }
}

function validateRadioGroup(radioName, formId, required, msgs) {
    if(required){
        var form = document.getElementById(formId);
        if(form){
            var path=form[radioName];
            for (i = 0; i < path.length; i++){
                if (path[i].checked){
                    return null;
                }
            }
            return msgs[0];
        }
    }
}

function validateFileField(id, required, msgs) {
    var field = document.getElementById(id);
    if (field) {
        var value = trim(field.value);
        if (required) {
            if (value.length == 0) {
                Click.setFieldErrorClass(field);
                return msgs[0];
            }
        }
    } else {
        return 'Field ' + id + ' not found.';
    }
}

function validateForm(msgs, id, align, style) {
    var errorsHtml = '';
    var focusFieldId = null;

    for (i = 0; i < msgs.length; i++) {
        var value = msgs[i];
        if (value != null) {
            var index = value.lastIndexOf('|');
            var fieldMsg = value.substring(0, index);
            var fieldId = value.substring(index + 1);

            if (focusFieldId == null) {
                focusFieldId = fieldId;
            }

            errorsHtml += '<tr class="errors"><td class="errors" align="';
            errorsHtml += align;
			if (style != null) {
            	errorsHtml += '" style="';
				errorsHtml += style;
            }
            errorsHtml += '">';
            errorsHtml += '<a class="error" href="javascript:setFocus(\'';
            errorsHtml += fieldId;
            errorsHtml += '\');">';
            errorsHtml += fieldMsg;
            errorsHtml += '</a>';
            errorsHtml += '</td></tr>';
        }
    }

    if (errorsHtml.length > 0) {
        errorsHtml = '<table class="errors">' + errorsHtml + '</table>';
        //alert(errorsHtml);

        document.getElementById(id + '-errorsDiv').innerHTML = errorsHtml;
        document.getElementById(id + '-errorsTr').style.display = 'inline';

        setFocus(focusFieldId);

        return false;

    } else {
        return true;
    }
}

/**
 * Submit the form and checks that no field or button is called 'submit' as
 * it causes JS exceptions.
 *
 * Usage: <input onclick="Click.submit(form)">
 */
Click.submit=function(form) {
    if (typeof form == 'undefined') {
        alert('Error: form is undefined. Usage: Click.submit(form)');
        return false;
    }

if (form) {
        var formElements = form.elements;
        for (var i=0; i < formElements.length; i++) {
            var el = formElements[i];
    		    if(el.name=='submit'){
                alert('Error: In order to submit the Form through JavaScript, buttons and fields must not be named "submit". Please rename the button/field called "submit".');
                return false;
            }
        }
    }
    form.submit();
    return true;
}

/* Validate Functions */

function validateCreditCardField(id, typeId, required, minLength, maxLength, msgs){

	var msg = validateTextField(id, required, minLength, maxLength, msgs);

	if(msg){
		return msg;

	} else {
		var field = document.getElementById(id);
		var value = field.value;
		var type  = document.getElementById(typeId).value;

		if(value.length > 0){
			// strip
			var buffer = '';
			for(var i=0;i<value.length;i++){
				var c = value.charAt(i);
				if(c != '=' && c != ' '){
					buffer = buffer + c;
				}
			}
			value = buffer;

			var length = value.length;
			if(length < 13){
				Click.setFieldErrorClass(field);
				return msgs[3];
			}

			var firstdig  = value.charAt(0);
			var seconddig = value.charAt(1);
			var isValid = false;

			if(type=='VISA'){
				isValid = ((length == 16) || (length == 13)) && (firstdig == '4');
			}
			if(type=='MASTER'){
				isValid = (length == 16) && (firstdig == '5') && ("12345".indexOf(seconddig) >= 0);
			}
			if(type=='AMEX'){
				isValid = (length == 15) && (firstdig == '3') && ("47".indexOf(seconddig) >= 0);
			}
			if(type=='DINERS'){
				isValid = (length == 14) && (firstdig == '3') && ("068".indexOf(seconddig) >= 0);
			}
			if(type=='DISCOVER'){
				isValid = (length == 16) && value.startsWith("6011");
			}

			if (!isValid) {
				Click.setFieldErrorClass(field);
				return msgs[3];
			}
		}

		// no error
		Click.setFieldValidClass(field);
		return null;
	}
}

function validateEmailField (id, required, minLength, maxLength, msgs){
	var msg = validateTextField(id, required, minLength, maxLength, msgs);
	if (msg) {
		return msg;
	} else {
		var field  = document.getElementById(id);
		var value  = field.value;
		var length = value.length;

		if(length > 0){
			var index = value.indexOf("@");

			if (index < 1 || index == length - 1) {
				Click.setFieldErrorClass(field);
				return msgs[3];
			}

			if (!isLetterOrDigit(value.charAt(0))) {
				Click.setFieldErrorClass(field);
				return msgs[3];
			}

			if (!isLetterOrDigit(value.charAt(length - 1))) {
				Click.setFieldErrorClass(field);
				return msgs[3];
			}
		}
	}
	Click.setFieldValidClass(field);
	return null;
}

function validateNumberField(id, required, minValue, maxValue, msgs){
	var field = document.getElementById(id);
	if(field){
		var value = field.value;
		if (value.length == 0) {
			if(required){
				Click.setFieldErrorClass(field);
				return msgs[0];
			}
		} else {
			if (value > maxValue){
				Click.setFieldErrorClass(field);
				return msgs[2];
			} else if (value < minValue){
				Click.setFieldErrorClass(field);
				return msgs[1];
			}
		}

		Click.setFieldValidClass(field);
		return null;

	} else {
		return 'Field ' + id + ' not found.';
	}
}

function validatePickList(id, required, msgs){
	var field = document.getElementById(id);
	if(field){
		if (field.options.length == 0) {
			if(required){
				Click.setFieldErrorClass(field);
				return msgs[0];
			}
		}

		Click.setFieldValidClass(field);
		return null;

	} else {
		return 'Field ' + id + ' not found.';
	}
}

function validateRegexField(id, required, minLength, maxLength, regex, msgs){
	var msg = validateTextField(id, required, minLength, maxLength, msgs);
	if (msg) {
		return msg;
	} else {
		var field = document.getElementById(id);
		if (field.value.length == 0 || field.value.match(new RegExp(regex))) {
			Click.setFieldValidClass(field);
			return null;
		} else {
			Click.setFieldErrorClass(field);
			return msgs[3];
		}
	}
	return null;
}

/* Misc Functions */

function initMenu() {
	if (document.all && document.getElementById) {
		var navRoot = document.getElementById("dmenu");
		if (navRoot) {
			for (i = 0; i < navRoot.childNodes.length; i++) {
				var node = navRoot.childNodes[i];
				if (node.nodeName == "LI") {
					node.onmouseover = function() {
						this.className += " over";
					}
					node.onmouseout = function() {
						this.className = this.className.replace(" over", "");
					}
				}
			}
		}
	}
}


function isLetter (c){
	return ( ((c >= "a") && (c <= "z")) || ((c >= "A") && (c <= "Z")) )
}

function isDigit (c){
	return ((c >= "0") && (c <= "9"))
}

function isLetterOrDigit (c){
	return (isLetter(c) || isDigit(c))
}

function pickListMove(from, to, hidden, isSelected){
	var values = new Object();
	for(var i=0;i<from.options.length;i++){
		if(from.options[i].selected){
			values[from.options[i].value] = true;
		}
	}
	pickListMoveItem(from, to, values, hidden, isSelected);
}

function pickListMoveAll(from, to, hidden, isSelected){
	var values = new Object();
	for(i=0; i<from.options.length; i++){
		values[from.options[i].value] = true;
	}
	pickListMoveItem(from, to, values, hidden, isSelected);
}

function pickListMoveItem(from, to, values, hidden, isSelected){
	for(var i=0; i<hidden.options.length; i++){
		if(values[hidden.options[i].value]){
			hidden.options[i].selected = isSelected;
		}
	}
	for(var i=0; i<from.options.length; i++){
		if(values[from.options[i].value]){
			from.options[i] = null;
			i--;
		}
	}
	var toIndex = 0;
	for(var i=0; i<hidden.options.length; i++){
		if(hidden.options[i].selected == isSelected){
			to.options[toIndex] = new Option(hidden.options[i].text, hidden.options[i].value);
			toIndex++;
		}
	}
}

/**
 * Define the SubmitLink action. This function creates hidden fields for
 * each SubmitLink parameter and submits the form.
 *
 * @return false to prevent the link default action
 */
Click.submitLinkAction = function(link, formId) {
  var params=Click.getUrlParams(link.href);
  if (params == null) {
      return false;
  }
  var form = document.getElementById(formId);
  if(form == null) {
      return false;
  }
  for(var i=0; i<params.length; i++){
    var param=params[i];
    var input = document.createElement("input");
    input.setAttribute("type", "hidden");
    input.setAttribute("name", param.name);
    input.setAttribute("value", param.value);
    form.appendChild(input);
  }
  form.submit();
  return false;
}

/**
 * Return the url parameters as an array of key/value pairs or null
 * if no parameters can be extracted.
 */
Click.getUrlParams = function(url) {
  if (url == null || url == '' || url == 'undefined') {
    return null;
  }
  url = unescape(url);
  var start = url.indexOf('?')
  if (start == -1) {
    return null;
  }
  url=url.substring(start + 1);
  var pairs=url.split("&");
  var params = new Array();
  for (var i=0;i<pairs.length;i++) {
    var param = new Object();
    var pos = pairs[i].indexOf('=');
    if (pos >= 0) {
      param.name = pairs[i].substring(0,pos);
      param.value = pairs[i].substring(pos+1);
      params.push(param);
    }
  }
  return params;
};

if ( typeof Click.menu == 'undefined' )
    Click.menu = {};

// Code adapted from jquery.bgiframe. Add an IFrame to the menu ensuring Select
// elements does not burn through when menu is open
Click.menu.fixHiddenMenu = (document.all && /msie 6\.0/i.test(navigator.userAgent) ? function(menuId) {
    var menu = document.getElementById(menuId);

    // If menu is not available, exit early
    if(menu==null){
    	return;
    }

    var s = {
        top : 'auto',
        left : 'auto',
        width : 'auto',
        height : 'auto',
        opacity : true,
        src : 'javascript:false;'
    };

    var prop = function(n){return n&&n.constructor==Number?n+'px':n;}

    var html = '<iframe class="bgiframe"frameborder="0"tabindex="-1"src="'+s.src+'"'+
        'style="display:block;position:absolute;z-index:-1;'+
        (s.opacity !== false?'filter:Alpha(Opacity=\'0\');':'')+
        'top:'+(s.top=='auto'?'expression(((parseInt(this.parentNode.currentStyle.borderTopWidth)||0)*-1)+\'px\')':prop(s.top))+';'+
        'left:'+(s.left=='auto'?'expression(((parseInt(this.parentNode.currentStyle.borderLeftWidth)||0)*-1)+\'px\')':prop(s.left))+';'+
        'width:'+(s.width=='auto'?'expression(this.parentNode.offsetWidth+\'px\')':prop(s.width))+';'+
        'height:'+(s.height=='auto'?'expression(this.parentNode.offsetHeight+\'px\')':prop(s.height))+';'+
        '"/>';

    var uls = document.getElementById(menuId).getElementsByTagName('ul');
    for(var i = 0; i < uls.length; i++) {
        var ul = uls[i];
        var el = document.createElement(html);
        ul.insertBefore(el);
    }
} : function() {});

// Add 'over' class when hovering over menu items
Click.menu.fixHover = (document.all && /msie 6\.0/i.test(navigator.userAgent) ? function(menuId) {
    var elements = document.getElementById(menuId);
    if (elements != null) {
        var list = elements.getElementsByTagName("LI");
        if (list != null) {
            //enable hover for the list
            for (var i=0; i<list.length; i++) {
                list[i].onmouseover=function() {
                    this.className+=" over";
                }
                list[i].onmouseout=function() {
                    this.className=this.className.replace(new RegExp(" over\\b"), "");
                }
            }
        }
    }
} : function() {});

Click.addLoadEvent(function(){
 if(typeof Click != 'undefined' && typeof Click.menu != 'undefined') {
   if(typeof Click.menu.fixHiddenMenu != 'undefined') {
     Click.menu.fixHiddenMenu("rootMenu");
     Click.menu.fixHover("rootMenu");
   }
 }
});
