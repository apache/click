/*
 * Add a function to the window.onload event
 * without removing the old event functions.
 * 
 * Example usage:
 * addLoadEvent(function () {
 *	initChecklist();
 * });
 * 
 * See Simon Willison's blog 
 * http://simon.incutio.com/archive/2004/05/26/addLoadEvent
 */
function addLoadEvent(func) {
	var oldonload = window.onload;
	
	if (typeof window.onload != "function") {
		window.onload = func;
	} else {
		window.onload = function () {
			if (oldonload) {
				oldonload();
			}
			func();
		}
	}
}

/*
 * initialse the menu.
 */
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