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
 * This file is dependend upon /click/control.js functions.
 */

 /* Ensure Click namespace exists */
if ( typeof Click == 'undefined' )
  Click = {};

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
		if (field.value.length == 0 || field.value.match(new RegExp(regex))) {
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

/**
 * Define the SubmitLink action. This function creates hidden fields for
 * each SubmitLink parameter and submits the form.
 *
 * @return false to prevent the link default action
 */
Click.submitLinkAction = function(link, formName) {
  var params=Click.getUrlParams(link.href);
  if (params == null) {
      return false;
  }
  var form = document.getElementById(formName);
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