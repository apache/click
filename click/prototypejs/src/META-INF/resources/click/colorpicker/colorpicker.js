// Copyright 2008 Sebo Zoltan <iamzoli@yahoo.com>
//
// Permission is hereby granted, free of charge, to any person obtaining
// a copy of this software and associated documentation files (the
// "Software"), to deal in the Software without restriction, including
// without limitation the rights to use, copy, modify, merge, publish,
// distribute, sublicense, and/or sell copies of the Software, and to
// permit persons to whom the Software is furnished to do so, subject
// to the following conditions:
//
// The above copyright notice and this permission notice shall be
// included in all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
// EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
// MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
// NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
// BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
// ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
// CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

/**
 * This script provides a ColorPicker popup which allows users to pick
 * colors from a color palette.
 *
 * Usage:
 *      Click.colorPicker.showColorPicker(
 *         { inputObjId:'myField', imageId: 'myField_img', resourcePath:'/myapp' }
 *      )
 *
 * @option String inputObjId Specifies the HTML id of the ColorPicker input
 *   textfield where the color value will be stored.
 * @option String imageId Specifies the HTML id of the ColorPicker img tag.
 * @option String resourcePath Specifies the URL path to the ColorPicker image
 *   resources.
 *
 * More options are available below. See Click.colorPicker.defaults.
 */

// Make sure that the Click namespace exists
if( typeof Click == 'undefined' )
    Click = {};

// Make sure that the Click.colorPicker namespace exists
if( typeof Click.colorPicker == 'undefined' )
    Click.colorPicker = {};

/*
 * Validates the ColorPicker.
 */
function validateColorPicker(id, required, msgs){
    var field = document.getElementById(id);
    if(field){
        var value = field.value;
        if(value.length == 0){
            if(required){
                Click.setFieldErrorClass(field);
                return msgs[0];
            }
        } else if(!field.value.match(new RegExp("^#[a-fA-F0-9]{3}([a-fA-F0-9]{3})?$"))){
            Click.setFieldErrorClass(field);
            return msgs[1];
        }

        Click.setFieldValidClass(field);
        return null;

    } else {
        return 'Field ' + id + ' not found.';
    }
}

// Create closure
(function() {

    // override these in your code to change the default behavior and style
    Click.colorPicker.defaults = {
        CROSSHAIRS_LOCATION: '/click/colorpicker/images/crosshairs.png',
        HUE_SLIDER_LOCATION: '/click/colorpicker/images/h.png',
        HUE_SLIDER_ARROWS_LOCATION: '/click/colorpicker/images/position.png',
        SAT_VAL_SQUARE_LOCATION: '/click/colorpicker/images/sv.png',
        BUTTON_CLOSE_LOCATION: '/click/colorpicker/images/close.png',
        BUTTON_CLEAR_LOCATION: '/click/colorpicker/images/clear.png',
        inputObjId: null,
        colorPickerId: null,
        closeMsg: 'Close',
        clearMsg: 'Clear color',
        resourcePath: '.',
        previewId:null,
        imageId:null,
        isRequired:false
    }

    // Here are some boring utility functions. The real code comes later.
    var is_div_init=false;
    var options={};
    function hexToRgb(hex_string, default_) {
        if (default_ == undefined) {
            default_ = null;
        }

        if (hex_string.substr(0, 1) == '#') {
            hex_string = hex_string.substr(1);
        }

        var r;
        var g;
        var b;
        if (hex_string.length == 3) {
            r = hex_string.substr(0, 1);
            r += r;
            g = hex_string.substr(1, 1);
            g += g;
            b = hex_string.substr(2, 1);
            b += b;
        }
        else if (hex_string.length == 6) {
            r = hex_string.substr(0, 2);
            g = hex_string.substr(2, 2);
            b = hex_string.substr(4, 2);
        }
        else {
            return default_;
        }

        r = parseInt(r, 16);
        g = parseInt(g, 16);
        b = parseInt(b, 16);
        if (isNaN(r) || isNaN(g) || isNaN(b)) {
            return default_;
        }
        else {
            return {
                r: r / 255,
                g: g / 255,
                b: b / 255
            };
        }
    }

    function rgbToHex(r, g, b, includeHash) {
        r = Math.round(r * 255);
        g = Math.round(g * 255);
        b = Math.round(b * 255);
        if (includeHash == undefined) {
            includeHash = true;
        }

        r = r.toString(16);
        if (r.length == 1) {
            r = '0' + r;
        }
        g = g.toString(16);
        if (g.length == 1) {
            g = '0' + g;
        }
        b = b.toString(16);
        if (b.length == 1) {
            b = '0' + b;
        }
        return ((includeHash ? '#' : '') + r + g + b).toUpperCase();
    }

    var arVersion = navigator.appVersion.split("MSIE");
    var version = parseFloat(arVersion[1]);

    function fixPNG(myImage) {
        if ((version >= 5.5) && (version < 7) && (document.body.filters)) {
            var node = document.createElement('span');
            node.id = myImage.id;
            node.className = myImage.className;
            node.title = myImage.title;
            node.style.cssText = myImage.style.cssText;
            node.style.setAttribute('filter', "progid:DXImageTransform.Microsoft.AlphaImageLoader"
                + "(src=\'" + myImage.src + "\', sizingMethod='scale')");
            node.style.fontSize = '0';
            node.style.width = myImage.width.toString() + 'px';
            node.style.height = myImage.height.toString() + 'px';
            node.style.display = 'inline-block';
            return node;
        }
        else {
            return myImage.cloneNode(false);
        }
    }

    function trackDrag(node, handler) {
        function fixCoords(x, y) {
            var nodePageCoords = pageCoords(node);
            var de = document.documentElement;

            // Get Y offset independent of browser or compatibility mode
            var offsetY = self.pageYOffset || ( de && de.scrollTop ) || document.body.scrollTop;

            // Get X offset independent of browser or compatibility mode
            var offsetX = self.pageXOffset || ( de && de.scrollLeft ) || document.body.scrollLeft;

            x = (x - nodePageCoords.x) + offsetX;
            y = (y - nodePageCoords.y) + offsetY;
            if (x < 0) x = 0;
            if (y < 0) y = 0;
            if (x > node.offsetWidth - 1) x = node.offsetWidth - 1;
            if (y > node.offsetHeight - 1) y = node.offsetHeight - 1;
            return {
                x: x,
                y: y
            };
        }
        function mouseDown(ev) {
            var coords = fixCoords(ev.clientX, ev.clientY);
            var lastX = coords.x;
            var lastY = coords.y;
            handler(coords.x, coords.y);

            function moveHandler(ev) {
                var coords = fixCoords(ev.clientX, ev.clientY);
                if (coords.x != lastX || coords.y != lastY) {
                    lastX = coords.x;
                    lastY = coords.y;
                    handler(coords.x, coords.y);
                }
            }
            function upHandler(ev) {
                myRemoveEventListener(document, 'mouseup', upHandler);
                myRemoveEventListener(document, 'mousemove', moveHandler);
                myAddEventListener(node, 'mousedown', mouseDown);
            }
            myAddEventListener(document, 'mouseup', upHandler);
            myAddEventListener(document, 'mousemove', moveHandler);
            myRemoveEventListener(node, 'mousedown', mouseDown);
            if (ev.preventDefault) ev.preventDefault();
        }
        myAddEventListener(node, 'mousedown', mouseDown);
        node.onmousedown = function(e) {
            return false;
        };
        node.onselectstart = function(e) {
            return false;
        };
        node.ondragstart = function(e) {
            return false;
        };
    }

    var eventListeners = [];

    function findEventListener(node, event, handler) {
        var i;
        for (i in eventListeners) {
            if (eventListeners[i].node == node && eventListeners[i].event == event
                && eventListeners[i].handler == handler) {
                return i;
            }
        }
        return null;
    }
    function myAddEventListener(node, event, handler) {
        if (findEventListener(node, event, handler) != null) {
            return;
        }

        if (!node.addEventListener) {
            node.attachEvent('on' + event, handler);
        }
        else {
            node.addEventListener(event, handler, false);
        }

        eventListeners.push({
            node: node,
            event: event,
            handler: handler
        });
    }

    function removeEventListenerIndex(index) {
        var eventListener = eventListeners[index];
        delete eventListeners[index];

        if (!eventListener.node.removeEventListener) {
            eventListener.node.detachEvent('on' + eventListener.event,
                eventListener.handler);
        }
        else {
            eventListener.node.removeEventListener(eventListener.event,
                eventListener.handler, false);
        }
    }

    function myRemoveEventListener(node, event, handler) {
        removeEventListenerIndex(findEventListener(node, event, handler));
    }

    function cleanupEventListeners() {
        var i;
        for (i = eventListeners.length; i > 0; i--) {
            if (eventListeners[i] != undefined) {
                removeEventListenerIndex(i);
            }
        }
    }
    myAddEventListener(window, 'unload', cleanupEventListeners);

    // This copyright statement applies to the following two functions,
    // which are taken from MochiKit.
    //
    // Copyright 2005 Bob Ippolito <bob@redivi.com>
    //
    // Permission is hereby granted, free of charge, to any person obtaining
    // a copy of this software and associated documentation files (the
    // "Software"), to deal in the Software without restriction, including
    // without limitation the rights to use, copy, modify, merge, publish,
    // distribute, sublicense, and/or sell copies of the Software, and to
    // permit persons to whom the Software is furnished to do so, subject
    // to the following conditions:
    //
    // The above copyright notice and this permission notice shall be
    // included in all copies or substantial portions of the Software.
    //
    // THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
    // EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
    // MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
    // NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
    // BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
    // ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
    // CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

    function hsvToRgb(hue, saturation, value) {
        var red;
        var green;
        var blue;
        if (value == 0.0) {
            red = 0;
            green = 0;
            blue = 0;
        }
        else {
            var i = Math.floor(hue * 6);
            var f = (hue * 6) - i;
            var p = value * (1 - saturation);
            var q = value * (1 - (saturation * f));
            var t = value * (1 - (saturation * (1 - f)));
            switch (i) {
                case 1: red = q; green = value; blue = p; break;
                case 2: red = p; green = value; blue = t; break;
                case 3: red = p; green = q; blue = value; break;
                case 4: red = t; green = p; blue = value; break;
                case 5: red = value; green = p; blue = q; break;
                case 6: // fall through
                case 0: red = value; green = t; blue = p; break;
            }
        }
        return {
            r: red,
            g: green,
            b: blue
        };
    }

    function rgbToHsv(red, green, blue) {
        var max = Math.max(Math.max(red, green), blue);
        var min = Math.min(Math.min(red, green), blue);
        var hue;
        var saturation;
        var value = max;
        if (min == max) {
            hue = 0;
            saturation = 0;
        }
        else {
            var delta = (max - min);
            saturation = delta / max;
            if (red == max) {
                hue = (green - blue) / delta;
            }
            else if (green == max) {
                hue = 2 + ((blue - red) / delta);
            }
            else {
                hue = 4 + ((red - green) / delta);
            }
            hue /= 6;
            if (hue < 0) {
                hue += 1;
            }
            if (hue > 1) {
                hue -= 1;
            }
        }
        return {
            h: hue,
            s: saturation,
            v: value
        };
    }

    function pageCoords(node) {
        var x = node.offsetLeft;
        var y = node.offsetTop;
        var parent = node.offsetParent;
        while (parent != null) {
            x += parent.offsetLeft;
            y += parent.offsetTop;
            parent = parent.offsetParent;
        }
        return {
            x: x,
            y: y
        };
    }

    function makeColorSelector(inputBox, preview) {
        var path = options.resourcePath;

        // The real code begins here.

        var rgb, hsv;

        function colorChanged() {
            is_div_init=false;
            var hex = rgbToHex(rgb.r, rgb.g, rgb.b);

            var hueRgb = hsvToRgb(hsv.h, 1, 1);
            var hueHex = rgbToHex(hueRgb.r, hueRgb.g, hueRgb.b);
            inputBox.value=hex;
            if(preview) {
                preview.style.background = hex;
                 /*
                // popox idea
                inputBox.style.background = hex;
                if(((rgb.r*100+rgb.g*100+rgb.b*100)/3)<65) //change text color to white if the background color is to dark
                    inputBox.style.color="#fff";
                else inputBox.style.color="#000";
                */
            }

            satValDiv.style.background = hueHex;
            crossHairs.style.left = ((hsv.v*199)-10).toString() + 'px';
            crossHairs.style.top = (((1-hsv.s)*199)-10).toString() + 'px';
            huePos.style.top = ((hsv.h*199)-5).toString() + 'px';
            is_div_init=true;
        }
        function rgbChanged() {
            hsv = rgbToHsv(rgb.r, rgb.g, rgb.b);
            colorChanged();
        }
        function hsvChanged() {
            rgb = hsvToRgb(hsv.h, hsv.s, hsv.v);
            colorChanged();
        }

        var colorSelectorDiv = document.createElement('div');
        colorSelectorDiv.style.paddingLeft = '5px';
        colorSelectorDiv.style.paddingRight = '5px';
        colorSelectorDiv.style.paddingBottom = '5px';
        colorSelectorDiv.style.position = 'relative';
        colorSelectorDiv.style.diplay="inline";
        colorSelectorDiv.style.height = '227px';
        colorSelectorDiv.style.width = '210px';

        if(!options.isRequired) {
            var buttonClearImg = document.createElement('img');
            buttonClearImg.galleryImg = false;
            buttonClearImg.width = 17;
            buttonClearImg.height = 17;
            buttonClearImg.src = path + options.BUTTON_CLEAR_LOCATION;
            buttonClearImg.title = options.clearMsg;
            buttonClearImg.style.position = 'absolute';
            buttonClearImg.style.cursor='pointer';
            buttonClearImg.className='colorPickerClearImg';
            buttonClearImg.onclick=clearColorAndHideColorPicker;

            var clearButton = document.createElement('div');
            clearButton.style.position = 'absolute';
            clearButton.style.diplay="inline";
            clearButton.style.height = '17px';
            clearButton.style.width = '17px';
            clearButton.style.top="2px";
            clearButton.style.left="6px";

            clearButton.appendChild(buttonClearImg);
            colorSelectorDiv.appendChild(clearButton);
        }

        var buttonCloseImg = document.createElement('img');
        buttonCloseImg.galleryImg = false;
        buttonCloseImg.width = 17;
        buttonCloseImg.height = 17;
        buttonCloseImg.src = path + options.BUTTON_CLOSE_LOCATION;
        buttonCloseImg.title = options.closeMsg;
        buttonCloseImg.style.position = 'absolute';
        buttonCloseImg.style.cursor='pointer';
        buttonCloseImg.className='colorPickerCloseImg';
        buttonCloseImg.onclick=hideColorPicker;

        var buttonclose = document.createElement('div');
        buttonclose.style.position = 'absolute';
        buttonclose.style.diplay="inline";
        buttonclose.style.height = '17px';
        buttonclose.style.width = '17px';
        buttonclose.style.top="2px";
        buttonclose.style.left="224px";

        buttonclose.appendChild(buttonCloseImg);
        colorSelectorDiv.appendChild(buttonclose);

        var satValImg = document.createElement('img');
        satValImg.galleryImg = false;
        satValImg.width = 200;
        satValImg.height = 200;
        satValImg.src = path + options.SAT_VAL_SQUARE_LOCATION;
        satValImg.style.display = 'block';

        var satValDiv = document.createElement('div');
        satValDiv.style.position = 'absolute';
        satValDiv.style.diplay="inline";
        satValDiv.style.top = '28px';
        satValDiv.style.width = '200px';
        satValDiv.style.height = '200px';

        var newSatValImg = fixPNG(satValImg);
        satValDiv.appendChild(newSatValImg);

        var crossHairsImg = document.createElement('img');
        crossHairsImg.galleryImg = false;
        crossHairsImg.width = 21;
        crossHairsImg.height = 21;
        crossHairsImg.src = path + options.CROSSHAIRS_LOCATION;
        crossHairsImg.style.position = 'absolute';

        var crossHairs = crossHairsImg.cloneNode(false);
        satValDiv.appendChild(crossHairs);

        function satValDragged(x, y) {
            hsv.s = 1-(y/199);
            hsv.v = (x/199);
            hsvChanged();
        }
        trackDrag(satValDiv, satValDragged)

        colorSelectorDiv.appendChild(satValDiv);

        var hueSelectorImg = document.createElement('img');
        hueSelectorImg.galleryImg = false;
        hueSelectorImg.width = 35;
        hueSelectorImg.height = 200;
        hueSelectorImg.src = path + options.HUE_SLIDER_LOCATION;
        hueSelectorImg.style.display = 'block';

        var huePositionImg = document.createElement('img');
        huePositionImg.galleryImg = false;
        huePositionImg.width = 35;
        huePositionImg.height = 11;
        huePositionImg.src = path + options.HUE_SLIDER_ARROWS_LOCATION;
        huePositionImg.style.position = 'absolute';

        var hueDiv = document.createElement('div');
        hueDiv.style.position = 'absolute';
        hueDiv.style.diplay="inline";
        hueDiv.style.left = '210px';
        hueDiv.style.top = '28px';
        hueDiv.style.width = '35px';
        hueDiv.style.height = '200px';

        var huePos = fixPNG(huePositionImg);
        hueDiv.appendChild(hueSelectorImg.cloneNode(false));
        hueDiv.appendChild(huePos);
        function hueDragged(x, y) {
            is_div_init=false;
            hsv.h = y/199;
            hsvChanged();
        }
        trackDrag(hueDiv, hueDragged);
        colorSelectorDiv.appendChild(hueDiv);

        function inputBoxChanged() {
            rgb = hexToRgb(inputBox.value, {
                r: 0,
                g: 0,
                b: 0
            });
            rgbChanged();
        }
        //myAddEventListener(inputBox, 'change', inputBoxChanged);

        inputBoxChanged();

        return colorSelectorDiv;
    }

    function getTopPos(elem) {
        var returnValue = elem.offsetTop-elem.offsetHeight;
        while((elem = elem.offsetParent) != null) {
            returnValue += elem.offsetTop;
        }
        return returnValue;
    }

    function getLeftPos(elem) {
        var returnValue = elem.offsetLeft;
        while((elem = elem.offsetParent) != null)returnValue += elem.offsetLeft;
        return returnValue;
    }

    function overrideDefaults() {
        var length = arguments.length, options, target = {};
        for (var i = 0; i < length; i++ ) {
            if ( (options = arguments[ i ]) != null ) {
                for ( var name in options ) {
                    var copy = options[ name ];
                    if ( copy !== undefined ) {
                        target[name] = copy;
                    }
                }
            }
        }
        return target;
    }

    /**
     * Public API.
     */
    Click.colorPicker.showColorPicker = function(opts) {
        var inputObj = document.getElementById(opts.inputObjId);
        if (inputObj) {
            hideColorPicker();
            options = overrideDefaults(Click.colorPicker.defaults, opts);
            options.colorPickerId=options.inputObjId+'_cp';
            var color_picker_div = document.createElement('DIV');
            var img = document.getElementById(options.imageId);
            var preview = document.getElementById(options.previewId);
            color_picker_div.style.left = getLeftPos(img) + 'px';
            color_picker_div.style.width='250px';
            color_picker_div.style.heigth='190px';
            color_picker_div.style.top = getTopPos(img) + img.offsetHeight + 1 + 'px';
            color_picker_div.id = options.colorPickerId;
            color_picker_div.className = 'colorPicker';
            color_picker_div.style.display='block';
            color_picker_div.appendChild(makeColorSelector(inputObj, preview));
            document.body.appendChild(color_picker_div);
            is_div_init=true;
        } else {
            alert('Field ' + id + ' not found.');
        }
    }
    function clearColorAndHideColorPicker() {
        if (options.colorPickerId && is_div_init) {
            var colorInputObj = document.getElementById(options.inputObjId);
            var preview = document.getElementById(options.previewId);
            if(colorInputObj) {
                colorInputObj.value='';
                if (preview) {
                    preview.style.backgroundColor = '';
                }
            }
            hideColorPicker();
        }
    }
    function hideColorPicker() {
        if (options.colorPickerId && is_div_init){
            is_div_init=false;
            var colorPicker = document.getElementById(options.colorPickerId);
            if (colorPicker) {
                document.body.removeChild(colorPicker);
            }
            options={};
        }
    }

    function maskedHex(input) {
        var mask = '#[0-9a-fA-F]{7}';
        input.value=input.value.replace(mask,"");
    }
})();
