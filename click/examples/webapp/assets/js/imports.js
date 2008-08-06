/*
 * (c)2006 Jesse Skinner/Dean Edwards/Matthias Miller/John Resig
 * Special thanks to Dan Webb's domready.js Prototype extension
 * and Simon Willison's addLoadEvent
 *
 * For more info, see:
 * http://www.thefutureoftheweb.com/blog/adddomloadevent
 * http://dean.edwards.name/weblog/2006/06/again/
 * http://www.vivabit.com/bollocks/2006/06/21/a-dom-ready-extension-for-prototype
 * http://simon.incutio.com/archive/2004/05/26/addLoadEvent
 *
 * This script also replaced the IE solution with the one from this article:
 * http://javascript.nwbox.com/IEContentLoaded/
 *
 * To use call addLoadEvent one or more times with functions, ie:
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
 *
 */
addLoadEvent = (function(){
    // create event function stack
    var load_events = [],
        load_timer,
        done,
        exec,
        old_onload,
        init = function () {
            done = true;

            // kill the timer
            clearInterval(load_timer);

            // execute each function in the stack in the order they were added
            while (exec = load_events.shift()) {
                exec();
            }
        };

    return function (func) {
        // if the init function was already ran, just run this function now and stop
        if (done) {return func()};

        if (!load_events[0]) {

            // for Internet Explorer
            /*@cc_on @*/
            /*@if (@_win32)
			      // polling for no errors
			      var d = window.document;
	          (function () {
		            try {
			              // throws errors until after ondocumentready
			              d.documentElement.doScroll('left');
		            } catch (e) {
			              setTimeout(arguments.callee, 50);
			              return;
 		            }
		            // no errors, fire
		            init();
	          })();
            d.onreadystatechange = function() {
            if (this.readyState == "complete")
                init(); // call the onload handler
            };
            /*@end @*/

            // for Mozilla/Opera9
            if (document.addEventListener) {
                document.addEventListener("DOMContentLoaded", init, false);
            }

            // for Safari
            if (/WebKit/i.test(navigator.userAgent)) { // sniff
                load_timer = setInterval(function() {
                    if (/loaded|complete/.test(document.readyState)) {
                        init(); // call the onload handler
                    }
                }, 10);
            }

            // for other browsers set the window.onload, but also execute the old window.onload
            old_onload = window.onload;
            window.onload = function() {
                init();
                if (old_onload) {old_onload()};
            };
        }

        load_events.push(func);
    }
})();

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

/*
 * This file is dependend upon /click/control.js functions.
 */
 
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
				setFieldErrorColor(field);
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
				setFieldErrorColor(field);
				return msgs[3];
			}
		}
		
		// no error
		setFieldValidColor(field);
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
				setFieldErrorColor(field);
				return msgs[3];
			}
	
			if (!isLetterOrDigit(value.charAt(0))) {
				setFieldErrorColor(field);
				return msgs[3];
			}
	
			if (!isLetterOrDigit(value.charAt(length - 1))) {
				setFieldErrorColor(field);
				return msgs[3];
			}
		}
	}
	setFieldValidColor(field);
	return null;
}

function validateNumberField(id, required, minValue, maxValue, msgs){
	var field = document.getElementById(id);
	if(field){
		var value = field.value;
		if (value.length == 0) {
			if(required){
				setFieldErrorColor(field);
				return msgs[0];
			}
		} else {
			if (value > maxValue){
				setFieldErrorColor(field);
				return msgs[2];
			} else if (value < minValue){
				setFieldErrorColor(field);
				return msgs[1];
			}
		}
		
		setFieldValidColor(field);
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
				setFieldErrorColor(field);
				return msgs[0];
			}
		}
		
		setFieldValidColor(field);
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
		if (field.value.match(new RegExp(regex))) {
			setFieldValidColor(field);
			return null;
		} else {
			setFieldErrorColor(field);
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
