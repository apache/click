/*! Copyright (c) 2010 Brandon Aaron (http://brandonaaron.net)
* Licensed under the MIT License (LICENSE.txt).
*
* Version 2.1.3-pre
*/
// Ensure Click namespace exists
if ( typeof Click == 'undefined' )
    Click = {};

if ( typeof Click.menu == 'undefined' )
    Click.menu = {};

// Code adapted from jquery.bgiframe. Add an IFrame to the menu ensuring Select
// elements does not burn through when menu is open
Click.menu.fixHiddenMenu = (document.all && /msie 6\.0/i.test(navigator.userAgent) ? function(menuId) {
    var menu = document.getElementById(menuId);

    // If menu is not available, exit early
    if(menu==null){
    	return;
    }

    var s = {
        top : 'auto',
        left : 'auto',
        width : 'auto',
        height : 'auto',
        opacity : true,
        src : 'javascript:false;'
    };

    var prop = function(n){return n&&n.constructor==Number?n+'px':n;}

    var html = '<iframe class="bgiframe"frameborder="0"tabindex="-1"src="'+s.src+'"'+
        'style="display:block;position:absolute;z-index:-1;'+
        (s.opacity !== false?'filter:Alpha(Opacity=\'0\');':'')+
        'top:'+(s.top=='auto'?'expression(((parseInt(this.parentNode.currentStyle.borderTopWidth)||0)*-1)+\'px\')':prop(s.top))+';'+
        'left:'+(s.left=='auto'?'expression(((parseInt(this.parentNode.currentStyle.borderLeftWidth)||0)*-1)+\'px\')':prop(s.left))+';'+
        'width:'+(s.width=='auto'?'expression(this.parentNode.offsetWidth+\'px\')':prop(s.width))+';'+
        'height:'+(s.height=='auto'?'expression(this.parentNode.offsetHeight+\'px\')':prop(s.height))+';'+
        '"/>';

    var uls = document.getElementById(menuId).getElementsByTagName('ul');
    for(var i = 0; i < uls.length; i++) {
        var ul = uls[i];
        var el = document.createElement(html);
        ul.insertBefore(el);
    }
} : function() {});

// Add 'over' class when hovering over menu items
Click.menu.fixHover = (document.all && /msie 6\.0/i.test(navigator.userAgent) ? function(menuId) {
    var elements = document.getElementById(menuId);
    if (elements != null) {
        var list = elements.getElementsByTagName("LI");
        if (list != null) {
            //enable hover for the list
            for (var i=0; i<list.length; i++) {
                list[i].onmouseover=function() {
                    this.className+=" over";
                }
                list[i].onmouseout=function() {
                    this.className=this.className.replace(new RegExp(" over\\b"), "");
                }
            }
        }
    }
} : function() {});