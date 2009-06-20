 // Licensed under the Apache License, Version 2.0 (the "License");
 // you may not use this file except in compliance with the License.
 // You may obtain a copy of the License at
 //
 //     http://www.apache.org/licenses/LICENSE-2.0
 //
 // Unless required by applicable law or agreed to in writing, software
 // distributed under the License is distributed on an "AS IS" BASIS,
 // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 // See the License for the specific language governing permissions and
 // limitations under the License.

/*
 * CheckList function. 
 * See  Nicolaus Rougeux 'Check it, don't select it':
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
