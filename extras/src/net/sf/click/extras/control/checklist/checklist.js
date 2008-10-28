/*
 * CheckList function. 
 * See  Nicolaus Rougeux 'Check it, don't select it
 * http://c82.net/article.php?ID=25 
 */
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

function validateCheckList(pathName, required, msgs){
	if(required){
		for (i = 0; i < pathName.length; i++) {
			if (pathName[i].checked) {
				return null;
			}
		}
		return msgs[0];
	}
}
