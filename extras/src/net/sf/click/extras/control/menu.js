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