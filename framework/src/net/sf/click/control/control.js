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
	if (field && field.focus) {
		field.focus();
	}
}
