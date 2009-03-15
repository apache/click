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
 * http://simon.incutio.com/archive/2004/05/26/addLoadEvent
 * http://javascript.nwbox.com/IEContentLoaded/
 * http://www.thefutureoftheweb.com/blog/adddomloadevent
 */
(function() {
    /* Handle IE (32/64 bit) */
    /*@cc_on
			@if (@_win32 || @_win64)
      // Guard against iframe
      if (window == top) {
	  		var d = window.document;
  	    (function () {
  		    try {
  			    d.documentElement.doScroll('left');
  		    } catch (e) {
  			    setTimeout(arguments.callee, 50);
  			    return;
 		      }
		      // Dom is ready, run events
		      Click.domready.run();
	      })();
      }
			@end
		@*/

    // Handle DOMContentLoaded compliant browsers.
    if(document.addEventListener) {
        document.addEventListener("DOMContentLoaded", Click.domready.run, false);

    // Handle old KHTML/WebKit
    } else if(/KHTML|WebKit/i.test(navigator.userAgent)) {
        if(/loaded|complete/.test(document.readyState)) {
            Click.domready.run();
        } else {
            setTimeout(arguments.callee, 0);
        }
    }

    // Fallback to window.onload
    var prevOnload = window.onload;
    window.onload = function() {
        Click.domready.run();
        if (typeof prevOnload === 'function') prevOnload();
    };
})();

/**
 * Usage: Call addLoadEvent passing in a function to invoke when the DOM is
 * ready:
 *
 *    Example 1:
 *    function something() {
 *       // do something
 *    }
 *    addLoadEvent(something);
 *
 *    Example 2:
 *    addLoadEvent(function() {
 *        // do something
 *    });
 */
addLoadEvent = function(func) {
    // If dom is ready, fire event and return
    if (Click.domready.ready) {
        return func();
    }
    Click.domready.events.push(func);
};

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


function setFieldValidColor(field) {
    field.style.background = 'white';
}

function setFieldErrorColor(field) {
    field.style.background = '#FFFF80';
}

function validateTextField(id, required, minLength, maxLength, msgs) {
    var field = document.getElementById(id);
    if (field) {
        var value = trim(field.value);
        if (required) {
            if (value.length == 0) {
                setFieldErrorColor(field);
                return msgs[0];
            }
        }
        if (required && minLength > 0) {
            if (value.length < minLength) {
                setFieldErrorColor(field);
                return msgs[1];
            }
        }
        if (maxLength > 0) {
            if (value.length > maxLength) {
                setFieldErrorColor(field);
                return msgs[2];
            }
        }
        setFieldValidColor(field);
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
                setFieldValidColor(field);
                return null;
            } else {
                setFieldErrorColor(field);
                return msgs[0];
            }
        }
    } else {
        return 'Field ' + id + ' not found.';
    }
}

function validateRadioGroup(pathName, required, msgs) {
    if(required){
        //var value = pathName.value;
        for (i = 0; i < pathName.length; i++) {
            if (pathName[i].checked) {
                return null;
            }
        }
        return msgs[0];
    }
}
 
function validateFileField(id, required, msgs) {
    var field = document.getElementById(id);
    if (field) {
        var value = trim(field.value);
        if (required) {
            if (value.length == 0) {
                setFieldErrorColor(field);
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
