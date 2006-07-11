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
	if (field && field.focus && field.type != "hidden") {
		field.focus();
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

function validateField(id) {
    var field = document.getElementById(id);
    if (field) {
    	var value = trim(field.value);
    	if (value.length > 0) {
            setFieldValidColor(field);
            return true;
    	} else {
            setFieldErrorColor(field);
            return false;
    	}
    } else {
        alert('Field ' + id + ' not found.');
        return false;
    }
}

function validateCheckbox(id) {
    var field = document.getElementById(id);
    if (field) {
    	if (field.checked) {
			return true;
    	} else {
			return false;
    	}
    } else {
    	alert('Field ' + id + ' not found.');
    	return false;
    }
}

function validateSelect(id, defaultValue) {
    var field = document.getElementById(id);
    if (field) {
    	var value = field.value;
    	if (value != defaultValue) {
            setFieldValidColor(field);
            return true;
    	} else {
            setFieldErrorColor(field);
            return false;
    	}
    } else {
    	alert('Field ' + id + ' not found.');
    	return false;
    }
}

function validateRadioGroup(pathName) {
    var value = pathName.value;
    for (i = 0; i < pathName.length; i++) {
        if (pathName[i].checked) {
            return true;
        }
    }
    return false;
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
			errorsHtml += '" style="';
			errorsHtml += style;
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
