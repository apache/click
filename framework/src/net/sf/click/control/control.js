/** Add a function to the window.onload event
  without removing the old event functions.
  
  Example usage:
  addLoadEvent(function () {
	initChecklist();
  });
  
  See Simon Willison's blog 
  http://simon.incutio.com/archive/2004/05/26/addLoadEvent
  */
function addLoadEvent(func) {
	var oldonload = window.onload;
	
	if (typeof window.onload != "function") {
		window.onload = func;
	} else {
		window.onload = function () {
			oldonload();
			func();
		}
	}
}

/* CheckList function. 
   See  Nicolaus Rougeux 'Check it, don't select it
   http://c82.net/article.php?ID=25 */
   
function initChecklist(checklistid) {
	if (document.all && document.getElementById) {
		// Get all unordered lists
		var theList = document.getElementById(checklistid);
		if(theList != null) {
			var labels = theList.getElementsByTagName("label");
			
			// Assign event handlers to labels within
			for (var j = 0; j < labels.length; j++) {
				var theLabel = labels[j];
				theLabel.onmouseover = function() { this.className += " hover"; };
				theLabel.onmouseout = function() { this.className = this.className.replace(" hover", ""); };
			}
		}
	}
}   

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