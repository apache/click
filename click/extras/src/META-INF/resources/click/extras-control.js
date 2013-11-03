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
 * This file depends on /click/control.js.
 */

/* Ensure Click namespace exists */
if ( typeof Click == 'undefined' )
  Click = {};

/* Validate Functions */
Click.validateCreditCardField = function(id, typeId, required, minLength, maxLength, msgs) {
	var msg = Click.validateTextField(id, required, minLength, maxLength, msgs);
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
};

Click.validateEmailField = function(id, required, minLength, maxLength, msgs) {
	var msg = Click.validateTextField(id, required, minLength, maxLength, msgs);
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
	
			if (!Click.isLetterOrDigit(value.charAt(0))) {
				Click.setFieldErrorClass(field);
				return msgs[3];
			}
	
			if (!Click.isLetterOrDigit(value.charAt(length - 1))) {
				Click.setFieldErrorClass(field);
				return msgs[3];
			}
		}
	}
	Click.setFieldValidClass(field);
	return null;
};

Click.validateNumberField = function(id, required, minValue, maxValue, msgs) {
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
};

Click.validatePickList = function(id, required, msgs) {
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
};

Click.validateRegexField = function(id, required, minLength, maxLength, regex, msgs) {
	var msg = Click.validateTextField(id, required, minLength, maxLength, msgs);
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
};

/* Filter */
Click.noLetterFilter = function(event) {
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
};

Click.integerFilter = function(event) {
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
};

Click.doubleFilter = function(event) {
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
};

/* Misc Functions */
// todo: where is this function used?
function initMenu() {
	if (document.all && document.getElementById) {
		var navRoot = document.getElementById("dmenu");
		if (navRoot) {
			for (i = 0; i < navRoot.childNodes.length; i++) {
				var node = navRoot.childNodes[i];
				if (node.nodeName == "LI") {
					node.onmouseover = function() {
						this.className += " over";
					};
					node.onmouseout = function() {
						this.className = this.className.replace(" over", "");
					};
				}
			}
		}
	}
}


Click.isLetter = function(c) {
	return ( ((c >= "a") && (c <= "z")) || ((c >= "A") && (c <= "Z")) )
};

Click.isDigit = function(c) {
	return ((c >= "0") && (c <= "9"))
};

Click.isLetterOrDigit = function(c) {
	return (Click.isLetter(c) || Click.isDigit(c))
};

Click.pickListMove = function(from, to, hidden, isSelected) {
	var values = {};
	for(var i=0;i<from.options.length;i++){
		if(from.options[i].selected){
			values[from.options[i].value] = true;
		}
	}
	Click.pickListMoveItem(from, to, values, hidden, isSelected);
};

Click.pickListMoveAll = function(from, to, hidden, isSelected) {
	var values = {};
	for(var i=0; i<from.options.length; i++){
		values[from.options[i].value] = true;
	}
	Click.pickListMoveItem(from, to, values, hidden, isSelected);
};

Click.pickListMoveItem = function(from, to, values, hidden, isSelected){
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
};

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
};

/**
 * Return the url parameters as an array of key/value pairs or null
 * if no parameters can be extracted.
 */
Click.getUrlParams = function(url) {
  if (url == null || url == '' || url == 'undefined') {
    return null;
  }
  url = unescape(url);
  var start = url.indexOf('?');
  if (start == -1) {
    return null;
  }
  url=url.substring(start + 1);
  var pairs=url.split("&");
  var params = [];
  for (var i=0;i<pairs.length;i++) {
    var param = {};
    var pos = pairs[i].indexOf('=');
    if (pos >= 0) {
      param.name = pairs[i].substring(0,pos);
      param.value = pairs[i].substring(pos+1);
      params.push(param);
    }
  }
  return params;
};

/**
 * Return the tab sheet number for the element with the given ID.
 */
Click.getTabSheetNumber=function(id) {
    var node=document.getElementById(id);
    if(!node) return 1;
    var parent = node.parentNode;
    while(parent && !Click.isTabSheet(parent)) {
        parent = parent.parentNode;
    }
    if (parent) return parent.getAttribute("id").substr(10);
    return 1;
};

/**
 * Return true if the node is a tab sheet, false otherwise.
 */
Click.isTabSheet=function(node) {
    if(!node) return false;
    var id=node.getAttribute('id');
    if(id){
        if(id.indexOf('tab-sheet-')>=0) return true;
    }
    return false;
};

/**
 * Validate a TabbedForm.
 */
Click.validateTabbedForm=function(msgs, id, align, style) {
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
            errorsHtml += '<a class="error" href="javascript:onShowTab(Click.getTabSheetNumber(\''
                + fieldId + '\'));Click.setFocus(\'';
            errorsHtml += fieldId;
            errorsHtml += '\');">';
            errorsHtml += fieldMsg;
            errorsHtml += '</a>';
            errorsHtml += '</td></tr>';
        }
    }

    if (errorsHtml.length > 0) {
        errorsHtml = '<table class="errors">' + errorsHtml + '</table>';

        document.getElementById(id + '-errorsDiv').innerHTML = errorsHtml;
        document.getElementById(id + '-errorsTr').style.display = 'inline';

        Click.setFocus(focusFieldId);

        return false;
    } else {
        return true;
    }
};
