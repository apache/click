function numberFilter(event) {
    var keyCode;
    if (document.all) {
        keyCode = event.keyCode; 
    } else if (document.getElementById) {
        keyCode = event.which;   
    } else if (document.layers) {
        keyCode = event.which;   
    }
  
    if (keyCode >= 65 && keyCode <= 90) {
        return false;
    } else if (keyCode >= 97 && keyCode <= 122) {
        return false;
    } else {  
        return true;     
    }
}

/* 
 *  Pop Up Calendar 
 *  By Paul Geerts
 *  11 Oct 2002
*/

// is the calendar created?
var calDiv = null;

// is the calendar showing?
var showing = false;

var pickArea = null;

// function to create the calendar if necessary and pop it up
// the button is the "V" button that triggers the popup event
function goCalendar(button) {
    if (showing) {   // if we're currently showing, stop it
        closeCalendar();
    }

    format = button.previousSibling;      // get the hidden text field containng the desired date format 
    val = format.previousSibling;         // get the date as milliseconds since 1970 
    disp = val.previousSibling;           // get the display field

    currentDate = new Date();             // create a new date to hold the
	checkDate(disp);                      // check if the text matches a date format
    if (val.value != "") {
   	    currentDate.setTime(val.value);   // start value 
       	oldDate = currentDate;            // Rememeber the old date so we can show it as blue
    } else {
   	    oldDate = null;
   	}
    var point = getPoint(disp);

    if (!calDiv) {                        // if we haven't create the calendar, go to
        calDiv = document.createElement("DIV");
        calDiv.style.backgroundColor = "#cccccc";
        calDiv.style.border = "2px outset";
        calDiv.style.position = "absolute";
        calDiv.style.top = point.y + disp.offsetHeight + 1;
        calDiv.style.left = point.x;
        calDiv.style.fontFamily = "sans-serif";
        calDiv.appendChild(createCalendar(currentDate));
        document.body.appendChild(calDiv);
    } else {                             // otherwise, just move and unhide it
        calDiv.style.display = "block";
        calDiv.style.top = point.y + disp.offsetHeight + 1;
        calDiv.style.left = point.x;
        updatePickArea();               // update the selection area
    }

    showing = true;  // we are now showing

    // this needs to be done outsite a click event for some reason
    setTimeout(addBodyClick, 1);

}

function addBodyClick() {   // this adds the handler which hides the 
                            // picker when something else is clicked
    addEvent("click", document.body, closeCalendar);
}

function createCalendar(date) {   // create calendar using W3C DOM methods

    var table = document.createElement("TABLE");
    var tbody = document.createElement("TBODY");
    table.appendChild(tbody);
    // create head row with dropdowns
    var headtr = document.createElement("TR");
    tbody.appendChild(headtr);
    headtr.appendChild(getHeadTD(currentDate));

    // create body TR with picker
    var bodytr = document.createElement("TR");
    tbody.appendChild(bodytr);
    pickArea = createPickArea();
    updatePickArea();
    bodytr.appendChild(pickArea);

    // create footer TR with "Today" link
    var foottr = document.createElement("TR");
    foottd = document.createElement("TD");
    foottr.appendChild(foottd);
    foottd.align = "middle";
    var today = new Date();
    foottd.innerHTML = "Today : " + months[today.getMonth()] + " " + today.getDate()  + ", " + today.getFullYear();
    foottd.date = new Date();
    foottd.align="center";
    foottd.style.fontSize="8pt";
    setCursor(foottd);
    foottd.style.backgroundColor="#aaaaaa";

    // add events using the approprate method for each browser
    addEvent("mouseover", foottd, tdMouseOver);
    addEvent("mouseout", foottd, tdMouseOut);
    addEvent("click", foottd, tdClick);
    tbody.appendChild(foottr);


    return table;

}

function getHeadTD(currentDate) {  // create dropdown TD 
    var td = document.createElement("TD");
    td.align = "center";
    td.valign = "top";

    monthSelect = getMonthSelect(currentDate);  // remember these so we can update them later
    yearSelect = getYearSelect(currentDate);
    td.appendChild(monthSelect);
    td.appendChild(yearSelect);

    
    return td;
}

function getMonthSelect(currentDate) {  // return a select with a list of months

    var sel = document.createElement("SELECT");
    for (var i = 0 ; i < months.length ; i++) {
        var opt = document.createElement("OPTION");
        opt.innerHTML = monthLongNames[i];
        opt.value = i;
        if (i == currentDate.getMonth()) {
            opt.selected = true;
        }
        sel.appendChild(opt);
    }
    
    // add events
    addEvent("change", sel, changeMonth);   

    // the doNuffin event is to cancel the bubble
    // without it, the calendar would disappear
    // when you clicked on the dropdpwn
    addEvent("click", sel, doNuffin);  
                                       
    return sel;
}

function getYearSelect(currentDate) {   // generate a list
                                        // of years
    var sel = document.createElement("SELECT");
    var year = currentDate.getFullYear();
    for (var i = 1920; i < 2021; i++) {
        var opt = document.createElement("OPTION");
        opt.innerHTML = i;
        opt.value = i;
        if (i == year) {
            opt.selected = true;
        }
        sel.appendChild(opt);
    }

    addEvent("change", sel, changeYear);
    addEvent("click", sel, doNuffin);
    return sel;
}

function createPickArea() {    // create the area where the calendar actually sits
    var bigtd = document.createElement("TD");
    var table = document.createElement("TABLE");
    var tbody = document.createElement("TBODY");
    bigtd.appendChild(table);
    table.appendChild(tbody);
    table.style.width="100%";
    table.cellSpacing = 0;
    table.style.backgroundColor="#ffffff";
    var trhead = document.createElement("TR");
    
    // create the header labels
    for (var i = 0 ; i < 7 ; i ++) {
        var td = document.createElement("TD");
        td.innerHTML = days[i];
        td.align="center";
        td.style.fontSize="8pt";
        td.style.color = "#ffffff";
        td.style.backgroundColor = "#777777";   
        trhead.appendChild(td);
    }
    tbody.appendChild(trhead);
    // create the grid for the 
    // date picking area
    for (var i = 0 ; i < 6 ; i ++) {
        var tr = document.createElement("TR");
        for (var j = 0 ; j < 7 ; j ++) {
            var td = document.createElement("TD");
            td.style.fontSize="8pt";
            setCursor(td);
            td.innerHTML = i*7+j;
            td.align="center";
            // add events for highlighting and clicking
            addEvent("mouseover", td, tdMouseOver);
            addEvent("mouseout", td, tdMouseOut);
            addEvent("click", td, tdClick);
            tr.appendChild(td);
        }
        tbody.appendChild(tr);
    }

    bigtd.tbody = tbody;
    return bigtd;
}


function updatePickArea() {  // this changes the pick area to reflect the current date

    monthSelect.value = currentDate.getMonth();
    yearSelect.value = currentDate.getFullYear();
    var first = new Date(currentDate.getFullYear(), currentDate.getMonth(), 1);
    var dayNumber = new Array();
    var monthOffset = new Array();
    var currdn = 0;

    // work out how what days from the previous month should be displayed
    for (var i = 0 ; i < first.getDay() ; i++) {
        var back = new Date();
        var daysback = first.getDay() - i;
        back.setTime(first - daysback * 1000 * 60 * 60 * 24);
        dayNumber[currdn] = back.getDate();
        monthOffset[currdn] = -1;
        currdn++;
    }

    // work out how what days from the current month should be displayed
    var ml = getMonthLength(first.getMonth(), first.getFullYear());
    for (var i = 1 ; i <= ml ; i++) {
        dayNumber[currdn] = i;
        monthOffset[currdn] = 0;
        currdn++;
    }

    // work out how what days from the next month should be displayed
    var nm = 1;
    while (currdn < 42) {
        dayNumber[currdn] = nm++;
        monthOffset[currdn] = 1;
        currdn++;
    }
    
    // update the cells to the correct values
    var c = 0;
    for (var i = 1 ; i <= 6 ; i++) {
        row = pickArea.tbody.childNodes[i];
        for (var j = 0 ; j < 7 ; j++) {
            col = row.childNodes[j];
            col.innerHTML = dayNumber[c];
            // store the date for this cell as an ad-hoc property of the cell
            // using the month-offset like this could lead to having negative
            // months (months start at zero for some reason) 
            // but the JS Date object works everything out fine
            col.date = new Date(currentDate.getFullYear(),
                                            currentDate.getMonth() + monthOffset[c],
                                            dayNumber[c]);

            // format the cell colour
            col.style.backgroundColor = "#ffffff";
            if (monthOffset[c]!=0) {
                col.style.color = "#aaaaaa";
            } else {
                col.style.color = "#000000";
            }

            // blue if current date
            if (oldDate != null && 
                col.date.getFullYear() == oldDate.getFullYear() &&
                col.date.getMonth() == oldDate.getMonth() && 
                col.date.getDate() == oldDate.getDate()) {
                col.style.color = "#0000FF";
            }            
            c++;
                                
        }
    }    
}


function changeMonth(e) {    // event handler for changing month
    var sel = getEventObject(e);
    currentDate = new Date(currentDate.getFullYear(), sel.value, currentDate.getDate());
    updatePickArea();

}

function changeYear(e) {  // event handler for changing year
    var sel = getEventObject(e);
    currentDate = new Date(sel.value,currentDate.getMonth(), currentDate.getDate());
    updatePickArea();

}

function doNuffin(e) {  // cancels bubble, see comment on line 140
    e.cancelBubble = true;
}

// highlite the cell
function tdMouseOver(e) {
    var td = getEventObject(e);
    td.oldBackground = td.style.backgroundColor;
    td.style.backgroundColor = "yellow";
}

// put it back the way it was
function tdMouseOut(e) {
    var td = getEventObject(e);
    td.style.backgroundColor = td.oldBackground;
}

// select a date by clicking
function tdClick(e) {
    tdMouseOut(e);   // ensure bg colour is reset, because we are reusing the date picker
    var td = getEventObject(e);
    setDate(td.date);
}

function setDate(date) {   // update the date display and hidden value
    var dstring = formatDate(date, format.value);
    
    disp.value = dstring;
    val.value = date.getTime();
    closeCalendar();
}

function closeCalendar() {   // put the calendar away
    deleteEvent("click", document.body, closeCalendar);  // remove the event
    calDiv.style.display = "none";
    showing = false;
}

function getEventObject(e) {  // utility function to retrieve object from event
    if (navigator.appName == "Microsoft Internet Explorer") {
        return e.srcElement;
    } else {  // is mozilla/netscape
        // need to crawl up the tree to get the first "real" element
        // i.e. a tag, not raw text
        var o = e.target;
        while (!o.tagName) {
            o = o.parentNode;
        }
        return o;
    }
}


function addEvent(name, obj, funct) { // utility function to add event handlers

    if (navigator.appName == "Microsoft Internet Explorer") {
        obj.attachEvent("on"+name, funct);
    } else {  // is mozilla/netscape
        obj.addEventListener(name, funct, false);
    }
}


function deleteEvent(name, obj, funct) { // utility function to delete event handlers

    if (navigator.appName == "Microsoft Internet Explorer") {
        obj.detachEvent("on"+name, funct);
    } else {  // is mozilla/netscape
        obj.removeEventListener(name, funct, false);
    }
}

function setCursor(obj) {
   if (navigator.appName == "Microsoft Internet Explorer") {
        obj.style.cursor = "hand";
    } else {  // is mozilla/netscape
        obj.style.cursor = "pointer";
    }
}


// Point x, y class
function Point(iX, iY)
{
   this.x = iX;
   this.y = iY;
}

// Get the Point of the given tag
function getPoint(aTag)
{
   var oTmp = aTag;  
   var point = new Point(0,0);
  
   do 
   {
      point.x += oTmp.offsetLeft;
      point.y += oTmp.offsetTop;
      oTmp = oTmp.offsetParent;
   } 
   while (oTmp.tagName != "BODY");

   return point;
}

/*
  Common Date processing data and routines
  
  Author: Paul Geerts
*/

// Reference Data arrays
// Reference Data arrays
var months = new Array("Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec");
var days = new Array("S", "M", "T", "W", "T", "F", "S");
var monthLengths =new Array(31,28,31, 30, 31, 30, 31, 31, 30, 31, 30, 31);
var monthLongNames = new Array('January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'October', 'November', 'December');
var dayNames = new Array('Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday','Friday', 'Saturday');


function getMonthLength(month, year) { // returns length of month, deals with leap year
    if (month != 1) {                  // months start at zero
        return monthLengths[month];
    } else {
        var d = new Date(year, 1, 29);
        return (d.getDate() == 29 ? 29 : 28);
    }
}

function formatDate(date, format) {  // formats the date based on the date format specified
    var bits = new Array();
    // work out what each bit should be
    bits['d'] = date.getDate();
    bits['dd'] = pad(date.getDate(),2);
    bits['dddd'] = dayNames[date.getDay()];

    bits['M'] = date.getMonth()+1;
    bits['MM'] = pad(date.getMonth()+1,2);
    bits['MMM'] = months[date.getMonth()];
    bits['MMMM'] = monthLongNames[date.getMonth()];
    
    var yearStr = "" + date.getFullYear();
    yearStr = (yearStr.length == 2) ? '19' + yearStr: yearStr;
    bits['yyyy'] = yearStr;
    bits['yy'] = bits['yyyy'].toString().substr(2,2);

    // do some funky regexs to replace the format string
    // with the real values
    var frm = new String(format);
    var sect;
    for (sect in bits) {
      frm = eval("frm.replace(/\\b" + sect + "\\b/,'" + bits[sect] + "');");
    }

    return frm;
}

function pad(number,X) {   // utility function to pad a number to a given width
	X = (!X ? 2 : X);
	number = ""+number;
	while (number.length < X) {
	    number = "0" + number;
	}
	return number;
}

/*
  MagicDate.js
  
  Script to perform BA required Date Magic

  Version 0.1
  Paul Geerts
*/

var seps = "[\\-\\s\\/]";
var shortMonths = "(" + months.join("|") + ")";

// lets cook us some regular expressions!

var regex = new Array();
regex[0] = eval("/^(\\d+)" + seps + shortMonths + seps + "(\\d\\d)$/i"); 
regex[0].bits = new Array("d", "MMM", "yy");

regex[1] = eval("/^(\\d+)" + seps + shortMonths + seps + "(\\d\\d\\d\\d)$/i"); 
regex[1].bits = new Array("d", "MMM", "yyyy");

regex[2] = eval("/^(\\d\\d)" +  shortMonths +  "(\\d\\d)$/i");
regex[2].bits = new Array("d", "MMM", "yy");

regex[3] = eval("/^(\\d\\d)" +  shortMonths +  "(\\d\\d\\d\\d)$/i");
regex[3].bits = new Array("d", "MMM", "yyyy");

regex[4] = eval("/^(\\d+)" + seps + "(\\d+)" + seps + "(\\d\\d)$/i"); 
regex[4].bits = new Array("d", "MM", "yy");

regex[5] = eval("/^(\\d+)" + seps + "(\\d+)" + seps + "(\\d\\d\\d\\d)$/i"); 
regex[5].bits = new Array("d", "MM", "yyyy");

regex[6] = eval("/^(\\d\\d)(\\d\\d)(\\d\\d)$/i"); 
regex[6].bits = new Array("d", "MM", "yy");

regex[7] = eval("/^(\\d\\d)(\\d\\d)(\\d\\d\\d\\d)$/i"); 
regex[7].bits = new Array("d", "MM", "yyyy");

regex[8] = eval("/^(\\d+)" + seps + shortMonths + "$/i"); 
regex[8].bits = new Array("d", "MMM");

regex[9] = eval("/^(\\d\\d)" +  shortMonths + "$/i");
regex[9].bits = new Array("d", "MMM");

regex[10] = eval("/^(\\d+)" + seps + "(\\d+)$/i"); 
regex[10].bits = new Array("d", "MM");

regex[11] = eval("/^(\\d\\d)(\\d\\d)$/i"); 
regex[11].bits = new Array("d", "MM");

regex[12] = eval("/^(\\d|\\d\\d)$/i"); 
regex[12].bits = new Array("d");

regex[13] = eval("/^" + shortMonths + seps + "(\\d+)" + seps + "(\\d\\d)$/i"); 
regex[13].bits = new Array("MMM", "d", "yy");

regex[14] = eval("/^" + shortMonths + seps + "(\\d+)" + seps + "(\\d\\d\\d\\d)$/i"); 
regex[14].bits = new Array("MMM", "d", "yyyy");

regex[15] = eval("/^" + shortMonths + "(\\d\\d)(\\d\\d)$/i"); 
regex[15].bits = new Array("MMM", "d", "yy");

regex[16] = eval("/^" + shortMonths + "(\\d\\d)(\\d\\d\\d\\d)$/i"); 
regex[16].bits = new Array("MMM", "d", "yyyy");

regex[17] = eval("/^" + shortMonths + seps + "(\\d+)$/i"); 
regex[17].bits = new Array("MMM", "d");

regex[18] = eval("/^" + shortMonths + "(\\d\\d)$/i"); 
regex[18].bits = new Array("MMM", "d");

regex[19] = eval("/^([\\+\\-])(\\d+)(d|w|m|y)$/i"); 
regex[19].bits = new Array("sign", "amount", "what");

// ok, all cooked

function checkDate(input) {
    var val = input.value;
    var hidden = input.nextSibling;
    var format = hidden.nextSibling;

    val = val.replace(/(^\s+|\s+$)/g, ""); // remove any leading and trailing whitespace. RegExps are cool.

    if ((val == null) || (val == "")) { // dont' try if it's empty, null could be valid
        hidden.value = "";
        return true;
    }

    var match = null;
    for (var i = 0 ; i < regex.length ; i++ ) {
        var ret = regex[i].exec(val);
        if (ret != null) {
            if (match == null) { 
                match = ret;
                match.bits = regex[i].bits;
            } else {  // we've matched before: ambiguity abounds, return the false;
                return false;
            }
        }
    }
    
    if (match != null) {
        var d = getDate(match);
        if (d != null) {
            hidden.value = d.getTime();
            input.value = formatDate(d, format.value);
            return true;
        } else {
            return false;
        }
    }
}

function getDate(match) {
    var bits = match.bits;
    var day = -1;
    var month = -1;
    var year = -1;
    for (var i = 1; i < match.length ; i++) {
        switch (bits[i-1]) {
        case "sign":
            return getDelta(match);
            break;
        case "d":
            day = parseInt(match[i]);
            break;
        case "MMM":
            for (var j = 0; j < months.length ; j++) {
                if (months[j].toLowerCase() == match[i].toLowerCase()) {
                    month = j;
                    break;
                }
            }
            break;
        case "MM":
            var m = match[i].replace(/^0/, "");
            month = parseInt(m) - 1;
            break;

        case "yy":
            year = 2000 + parseInt(match[i]);
            break;
        case "yyyy":
            year = parseInt(match[i]);
            break;
        default:
            alert("Wierd data bit: " + bits[i-1]);
        }
    }

    var now = new Date();
    if (year == -1) {
        year = now.getFullYear();
    }

    if (month == -1) {
        month = now.getMonth();
    }

    var d = new Date(year, month, day);
    if ((d.getDate() != day) || (d.getMonth() != month) || (d.getFullYear() != year)) {
        return null;
    } else {
        return d;
    }
}


function getDelta(match) {
    var sign = match[1];
    var amount = parseInt(match[2]);
    var what = match[3].toLowerCase();
    var now = new Date();
    if (sign=="-") {
        amount*=-1;
    }
    // add some logic here!
    switch (what) {
    case "d":
        now.setDate(now.getDate()+amount);
        break;
    case "m":
        now.setMonth(now.getMonth()+amount);
        break;
    case "y":
        now.setFullYear(now.getFullYear()+amount);
        break;
    case "w":
        now.setDate(now.getDate()+(amount * 7));
        break;

    }
    return now;
}

