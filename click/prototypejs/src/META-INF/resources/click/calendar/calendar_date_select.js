// Copyright 2009 Tim Harper (http://code.google.com/p/calendardateselect/)
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
// THE SOFTWARE.

// CalendarDateSelect version 1.16.1 - a prototype based date picker
// Questions, comments, bugs? - see the project page: http://code.google.com/p/calendardateselect
if (typeof Prototype == 'undefined') alert("CalendarDateSelect Error: Prototype could not be found. Please make sure that your application's layout includes prototype.js (.g. <%= javascript_include_tag :defaults %>) *before* it includes calendar_date_select.js (.g. <%= calendar_date_select_includes %>).");
if (Prototype.Version < "1.6") alert("Prototype 1.6.0 is required.  If using earlier version of prototype, please use calendar_date_select version 1.8.3");

Element.addMethods({
  purgeChildren: function(element) { $A(element.childNodes).each(function(e){$(e).remove();}); },
  build: function(element, type, options, style) {
    var newElement = Element.buildAndAppend(type, options, style);
    element.appendChild(newElement);
    return newElement;
  }
});

Element.buildAndAppend = function(type, options, style)
{
  var e = $(document.createElement(type));
  $H(options).each(function(pair) { e[pair.key] = pair.value });
  if (style) e.setStyle(style);
  return e;
};
nil = null;

Date.one_day = 24*60*60*1000;

// Full month names
Date.months = new Array('January','February','March','April','May','June','July','August','September','October','November','December');

// Month abbreviations
Date.monthAbbreviations = new Array('Jan','Feb','Mar','Apr','May','Jun','Jul','Aug','Sep','Oct','Nov','Dec');

// Full day names
Date.dayNames = new Array('Sunday','Monday','Tuesday','Wednesday','Thursday','Friday','Saturday');

// Day abbreviations
Date.dayAbbreviations = new Array('Sun','Mon','Tue','Wed','Thu','Fri','Sat');

// Weekdays displayed by popup calendar
Date.weekdays = new Array('S','M','T','W','T','F','S');

Date.first_day_of_week = 0;

Date.padded2 = function(hour) { var padded2 = parseInt(hour, 10); if (hour < 10) padded2 = "0" + padded2; return padded2; }
Date.prototype.getPaddedMinutes = function() { return Date.padded2(this.getMinutes()); }
Date.prototype.getAMPMHour = function() { var hour = this.getHours(); return (hour == 0) ? 12 : (hour > 12 ? hour - 12 : hour ) }
Date.prototype.getAMPM = function() { return (this.getHours() < 12) ? "AM" : "PM"; }
Date.prototype.stripTime = function() { return new Date(this.getFullYear(), this.getMonth(), this.getDate());};
Date.prototype.daysDistance = function(compare_date) { return Math.round((compare_date - this) / Date.one_day); };
Date.prototype.toFormattedString = function(include_time, format){
  var hour, str;
  str = Date.months[this.getMonth()] + " " + this.getDate() + ", " + this.getFullYear();

  if (include_time) { hour = this.getHours(); str += " " + this.getAMPMHour() + ":" + this.getPaddedMinutes() + " " + this.getAMPM() }
  return str;
}
Date.parseFormattedString = function(string, format) { return new Date(string);}
Math.floor_to_interval = function(n, i) { return Math.floor(n/i) * i;}
window.f_height = function() { return( [window.innerHeight ? window.innerHeight : null, document.documentElement ? document.documentElement.clientHeight : null, document.body ? document.body.clientHeight : null].select(function(x){return x>0}).first()||0); }
window.f_scrollTop = function() { return ([window.pageYOffset ? window.pageYOffset : null, document.documentElement ? document.documentElement.scrollTop : null, document.body ? document.body.scrollTop : null].select(function(x){return x>0}).first()||0 ); }

_translations = {
  "OK": "OK",
  "Now": "Now",
  "Today": "Today",
  "Clear": "Clear"
}
SelectBox = Class.create();
SelectBox.prototype = {
  initialize: function(parent_element, values, html_options, style_options) {
    this.element = $(parent_element).build("select", html_options, style_options);
    this.populate(values);
  },
  populate: function(values) {
    this.element.purgeChildren();
    var that = this; $A(values).each(function(pair) { if (typeof(pair)!="object") {pair = [pair, pair]}; that.element.build("option", { value: pair[1], innerHTML: pair[0]}) });
  },
  setValue: function(value) {
    var e = this.element;
    var matched = false;
    $R(0, e.options.length - 1 ).each(function(i) { if(e.options[i].value==value.toString()) {e.selectedIndex = i; matched = true;}; } );
    return matched;
  },
  getValue: function() { return $F(this.element)}
}
CalendarDateSelect = Class.create();
CalendarDateSelect.prototype = {
  initialize: function(target_element, options) {
    this.target_element = $(target_element); // make sure it's an element, not a string
    if (!this.target_element) { alert("Target element " + target_element + " not found!"); return false;}
    if (this.target_element.tagName != "INPUT") this.target_element = this.target_element.down("INPUT")
    this.target_element.calendar_date_select = this;
    this.last_click_at = 0;
    // initialize the date control
    this.options = $H({
      embedded: false,
      popup: nil,
      formatValue: 'yyyy MMM dd',
      time: false,
      footer: true,
      buttons: true,
      clear_button: true,
      year_range: 10,
      close_on_click: nil,
      minute_interval: 5,
      popup_by: this.target_element,
      month_year: "dropdowns",
      onchange: this.target_element.onchange,
      valid_date_check: nil
    }).merge(options || {});
    this.use_time = this.options.get("time");
    this.parseDate();
    this.callback("before_show")
    this.initCalendarDiv();
    if(!this.options.get("embedded")) {
      this.positionCalendarDiv()
      // set the click handler to check if a user has clicked away from the document
      Event.observe(document, "mousedown", this.closeIfClickedOut_handler = this.closeIfClickedOut.bindAsEventListener(this));
      Event.observe(document, "keypress", this.keyPress_handler = this.keyPress.bindAsEventListener(this));
    }
    this.callback("after_show")
  },
  positionCalendarDiv: function() {
    var above = false;
    var c_pos = this.calendar_div.cumulativeOffset(), c_left = c_pos[0], c_top = c_pos[1], c_dim = this.calendar_div.getDimensions(), c_height = c_dim.height, c_width = c_dim.width;
    var w_top = window.f_scrollTop(), w_height = window.f_height();
    var e_dim = $(this.options.get("popup_by")).cumulativeOffset(), e_top = e_dim[1], e_left = e_dim[0], e_height = $(this.options.get("popup_by")).getDimensions().height, e_bottom = e_top + e_height;

    if ( (( e_bottom + c_height ) > (w_top + w_height)) && ( e_bottom - c_height > w_top )) above = true;
    var left_px = e_left.toString() + "px", top_px = (above ? (e_top - c_height ) : ( e_top + e_height )).toString() + "px";

    this.calendar_div.style.left = left_px;  this.calendar_div.style.top = top_px;

    this.calendar_div.setStyle({visibility:""});

    // draw an iframe behind the calendar -- ugly hack to make IE 6 happy
    if(navigator.appName=="Microsoft Internet Explorer") this.iframe = $(document.body).build("iframe", {src: "javascript:false", className: "ie6_blocker"}, { left: left_px, top: top_px, height: c_height.toString()+"px", width: c_width.toString()+"px", border: "0px"})
  },
  initCalendarDiv: function() {
    if (this.options.get("embedded")) {
      var parent = this.target_element.parentNode;
      var style = {}
    } else {
      var parent = document.body
      var style = { position:"absolute", visibility: "hidden", left:0, top:0 }
    }
    this.calendar_div = $(parent).build('div', {className: "calendar_date_select"}, style);

    var that = this;
    // create the divs
    $w("top header body buttons footer bottom").each(function(name) {
      eval("var " + name + "_div = that." + name + "_div = that.calendar_div.build('div', { className: 'cds_"+name+"' }, { clear: 'left'} ); ");
    });

    this.initHeaderDiv();
    this.initButtonsDiv();
    this.initCalendarGrid();
    this.initFooterDiv();
    this.updateFooter("&#160;");

    this.refresh();
    this.setUseTime(this.use_time);
  },
  initHeaderDiv: function() {
    var header_div = this.header_div;
    this.close_button = header_div.build("a", { innerHTML: "x", href:"#", onclick:function () { this.close(); return false; }.bindAsEventListener(this), className: "close" });
    this.next_month_button = header_div.build("a", { innerHTML: "&gt;", href:"#", onclick:function () { this.navMonth(this.date.getMonth() + 1 ); return false; }.bindAsEventListener(this), className: "next" });
    this.prev_month_button = header_div.build("a", { innerHTML: "&lt;", href:"#", onclick:function () { this.navMonth(this.date.getMonth() - 1 ); return false; }.bindAsEventListener(this), className: "prev" });

    if (this.options.get("month_year")=="dropdowns") {
      this.month_select = new SelectBox(header_div, $R(0,11).map(function(m){return [Date.months[m], m]}), {className: "month", onchange: function () { this.navMonth(this.month_select.getValue()) }.bindAsEventListener(this)});
      this.year_select = new SelectBox(header_div, [], {className: "year", onchange: function () { this.navYear(this.year_select.getValue()) }.bindAsEventListener(this)});
      this.populateYearRange();
    } else {
      this.month_year_label = header_div.build("span")
    }
  },
  initCalendarGrid: function() {
    var body_div = this.body_div;
    this.calendar_day_grid = [];
    var days_table = body_div.build("table", { cellPadding: "0px", cellSpacing: "0px", width: "100%" })
    // make the weekdays!
    var weekdays_row = days_table.build("thead").build("tr");
    Date.weekdays.each( function(weekday) {
      weekdays_row.build("th", {innerHTML: weekday});
    });

    var days_tbody = days_table.build("tbody")
    // Make the days!
    var row_number = 0, weekday;
    for(var cell_index = 0; cell_index<42; cell_index++)
    {
      weekday = (cell_index+Date.first_day_of_week ) % 7;
      if ( cell_index % 7==0 ) days_row = days_tbody.build("tr", {className: 'row_'+row_number++});
      (this.calendar_day_grid[cell_index] = days_row.build("td", {
          calendar_date_select: this,
          onmouseover: function () { this.calendar_date_select.dayHover(this); },
          onmouseout: function () { this.calendar_date_select.dayHoverOut(this) },
          onclick: function() { this.calendar_date_select.updateSelectedDate(this, true); },
          className: (weekday==0) || (weekday==6) ? " weekend" : "" //clear the class
        },
        { cursor: "pointer" }
      )).build("div");
      this.calendar_day_grid[cell_index];
    }
  },
  initButtonsDiv: function()
  {
    var buttons_div = this.buttons_div;
    if (this.options.get("time"))
    {
      var blank_time = $A(this.options.get("time")=="mixed" ? [[" - ", ""]] : []);
      buttons_div.build("span", {innerHTML:"@", className: "at_sign"});

      var t = new Date();
      this.hour_select = new SelectBox(buttons_div,
        blank_time.concat($R(0,23).map(function(x) {t.setHours(x); return $A([t.getAMPMHour()+ " " + t.getAMPM(),x])} )),
        {
          calendar_date_select: this,
          onchange: function() { this.calendar_date_select.updateSelectedDate( { hour: this.value });},
          className: "hour"
        }
      );
      buttons_div.build("span", {innerHTML:":", className: "seperator"});
      var that = this;
      this.minute_select = new SelectBox(buttons_div,
        blank_time.concat($R(0,59).select(function(x){return (x % that.options.get('minute_interval')==0)}).map(function(x){ return $A([ Date.padded2(x), x]); } ) ),
        {
          calendar_date_select: this,
          onchange: function() { this.calendar_date_select.updateSelectedDate( {minute: this.value }) },
          className: "minute"
        }
      );

    } else if (! this.options.get("buttons")) buttons_div.remove();

    if (this.options.get("buttons")) {
      buttons_div.build("span", {innerHTML: "&#160;"});
      if (this.options.get("time")=="mixed" || !this.options.get("time")) b = buttons_div.build("a", {
          innerHTML: _translations["Today"],
          href: "#",
          onclick: function() {this.today(false); return false;}.bindAsEventListener(this)
        });

      if (this.options.get("time")=="mixed") buttons_div.build("span", {innerHTML: "&#160;|&#160;", className:"button_seperator"})

      if (this.options.get("time")) b = buttons_div.build("a", {
        innerHTML: _translations["Now"],
        href: "#",
        onclick: function() {this.today(true); return false}.bindAsEventListener(this)
      });

      if (!this.options.get("embedded") && !this.closeOnClick())
      {
        buttons_div.build("span", {innerHTML: "&#160;|&#160;", className:"button_seperator"})
        buttons_div.build("a", { innerHTML: _translations["OK"], href: "#", onclick: function() {this.close(); return false;}.bindAsEventListener(this) });
      }
      if (this.options.get('clear_button')) {
        buttons_div.build("span", {innerHTML: "&#160;|&#160;", className:"button_seperator"})
        buttons_div.build("a", { innerHTML: _translations["Clear"], href: "#", onclick: function() {this.clearDate(); if (!this.options.get("embedded")) this.close(); return false;}.bindAsEventListener(this) });
      }
    }
  },
  initFooterDiv: function(){
    if (!this.options.get("footer")) {
      var footer_div = this.footer_div;
      if(footer_div) footer_div.remove();
    }
  },
  refresh: function ()
  {
    this.refreshMonthYear();
    this.refreshCalendarGrid();

    this.setSelectedClass();
    this.updateFooter();
  },
  refreshCalendarGrid: function () {
    this.beginning_date = new Date(this.date).stripTime();
    this.beginning_date.setDate(1);
    this.beginning_date.setHours(12); // Prevent daylight savings time boundaries from showing a duplicate day
    var pre_days = this.beginning_date.getDay() // draw some days before the fact
    if (pre_days < 3) pre_days += 7;
    this.beginning_date.setDate(1 - pre_days + Date.first_day_of_week);

    var iterator = new Date(this.beginning_date);

    var today = new Date().stripTime();
    var this_month = this.date.getMonth();
    vdc = this.options.get("valid_date_check");
    for (var cell_index = 0;cell_index<42; cell_index++)
    {
      day = iterator.getDate(); month = iterator.getMonth();
      cell = this.calendar_day_grid[cell_index];
      Element.remove(cell.childNodes[0]); div = cell.build("div", {innerHTML:day});
      if (month!=this_month) div.className = "other";
      cell.day = day; cell.month = month; cell.year = iterator.getFullYear();
      if (vdc) { if (vdc(iterator.stripTime())) cell.removeClassName("disabled"); else cell.addClassName("disabled") };
      iterator.setDate( day + 1);
    }

    if (this.today_cell) this.today_cell.removeClassName("today");

    if ( $R( 0, 41 ).include(days_until = this.beginning_date.stripTime().daysDistance(today)) ) {
      this.today_cell = this.calendar_day_grid[days_until];
      this.today_cell.addClassName("today");
    }
  },
  refreshMonthYear: function() {
    var m = this.date.getMonth();
    var y = this.date.getFullYear();
    // set the month
    if (this.options.get("month_year") == "dropdowns")
    {
      this.month_select.setValue(m, false);

      var e = this.year_select.element;
      if (this.flexibleYearRange() && (!(this.year_select.setValue(y, false)) || e.selectedIndex <= 1 || e.selectedIndex >= e.options.length - 2 )) this.populateYearRange();

      this.year_select.setValue(y);

    } else {
      this.month_year_label.update( Date.months[m] + " " + y.toString()  );
    }
  },
  populateYearRange: function() {
    this.year_select.populate(this.yearRange().toArray());
  },
  yearRange: function() {
    if (!this.flexibleYearRange())
      return $R(this.options.get("year_range")[0], this.options.get("year_range")[1]);

    var y = this.date.getFullYear();
    return $R(y - this.options.get("year_range"), y + this.options.get("year_range"));
  },
  flexibleYearRange: function() { return (typeof(this.options.get("year_range")) == "number"); },
  validYear: function(year) { if (this.flexibleYearRange()) { return true;} else { return this.yearRange().include(year);}  },
  dayHover: function(element) {
    var hover_date = new Date(this.selected_date);
    hover_date.setYear(element.year); hover_date.setMonth(element.month); hover_date.setDate(element.day);
    this.updateFooter(hover_date.toFormattedString(this.use_time, this.options.get("formatValue")));
  },
  dayHoverOut: function(element) { this.updateFooter(); },
  clearSelectedClass: function() {if (this.selected_cell) this.selected_cell.removeClassName("selected");},
  setSelectedClass: function() {
    if (!this.selection_made) return;
    this.clearSelectedClass()
    if ($R(0,42).include( days_until = this.beginning_date.stripTime().daysDistance(this.selected_date.stripTime()) )) {
      this.selected_cell = this.calendar_day_grid[days_until];
      this.selected_cell.addClassName("selected");
    }
  },
  reparse: function() { this.parseDate(); this.refresh(); },
  dateString: function() {
    return (this.selection_made) ? this.selected_date.toFormattedString(this.use_time, this.options.get("formatValue")) : "&#160;";
  },
  parseDate: function()
  {
    var value = $F(this.target_element).strip()
    var default_time = this.options.get("default_time");
    this.selection_made = (value != "" || default_time);
    this.date = value=="" ? NaN : Date.parseFormattedString(this.options.get("date") || value, this.options.get("formatValue"));
    if (isNaN(this.date) && !default_time)
        this.date = new Date();
    else if (isNaN(this.date) && default_time)
        this.date = (Object.prototype.toString.apply(default_time) === '[object Function]') ? default_time() : default_time;

    if (!this.validYear(this.date.getFullYear())) this.date.setYear( (this.date.getFullYear() < this.yearRange().start) ? this.yearRange().start : this.yearRange().end);
    this.selected_date = new Date(this.date);
    this.use_time = /[0-9]:[0-9]{2}/.exec(value) ? true : false;
    this.date.setDate(1);
  },
  updateFooter:function(text) {
    if (this.options.get("footer")) {
      if (!text)
        text = this.dateString();
      this.footer_div.purgeChildren();
      this.footer_div.build("span", {innerHTML: text });
    }
  },
  clearDate:function() {
    if ((this.target_element.disabled || this.target_element.readOnly) && this.options.get("popup") != "force") return false;
    var last_value = this.target_element.value;
    this.target_element.value = "";
    this.clearSelectedClass();
    this.updateFooter('&#160;');
    if (last_value!=this.target_element.value) this.callback("onchange");
  },
  updateSelectedDate:function(partsOrElement, via_click) {
    var parts = $H(partsOrElement);
    if ((this.target_element.disabled || this.target_element.readOnly) && this.options.get("popup") != "force") return false;
    if (parts.get("day")) {
      var t_selected_date = this.selected_date, vdc = this.options.get("valid_date_check");
      t_selected_date.setYear(parts.get("year"));
      t_selected_date.setMonth(parts.get("month"));
      t_selected_date.setDate(parts.get("day"));

      if (vdc && ! vdc(t_selected_date.stripTime())) { return false; }
      this.selected_date = t_selected_date;
      this.selection_made = true;
    }

    if (!isNaN(parts.get("hour"))) this.selected_date.setHours(parts.get("hour"));
    if (!isNaN(parts.get("minute"))) this.selected_date.setMinutes( Math.floor_to_interval(parts.get("minute"), this.options.get("minute_interval")) );
    if (!isNaN(parts.get("second"))) this.selected_date.setSeconds(parts.get("second"));
    if (parts.get("hour") === "" || parts.get("minute") === "")
      this.setUseTime(false);
    else if (!isNaN(parts.get("hour")) || !isNaN(parts.get("minute")))
      this.setUseTime(true);

    this.updateFooter();
    this.setSelectedClass();

    if (this.selection_made) this.updateValue();
    if (this.closeOnClick()) { this.close(); }
    if (via_click && !this.options.get("embedded")) {
      if ((new Date() - this.last_click_at) < 333) this.close();
      this.last_click_at = new Date();
    }
  },
  closeOnClick: function() {
    if (this.options.get("embedded")) return false;
    if (this.options.get("close_on_click")===nil )
      return (this.options.get("time")) ? false : true
    else
      return (this.options.get("close_on_click"))
  },
  navMonth: function(month) { (target_date = new Date(this.date)).setMonth(month); return (this.navTo(target_date)); },
  navYear: function(year) { (target_date = new Date(this.date)).setYear(year); return (this.navTo(target_date)); },
  navTo: function(date) {
    if (!this.validYear(date.getFullYear())) return false;
    this.date = date;
    this.date.setDate(1);
    this.refresh();
    this.callback("after_navigate", this.date);
    return true;
  },
  setUseTime: function(turn_on) {
    this.use_time = this.options.get("time") && (this.options.get("time")=="mixed" ? turn_on : true) // force use_time to true if time==true && time!="mixed"
    if (this.use_time && this.selected_date) { // only set hour/minute if a date is already selected
      var minute = Math.floor_to_interval(this.selected_date.getMinutes(), this.options.get("minute_interval"));
      var hour = this.selected_date.getHours();

      this.hour_select.setValue(hour);
      this.minute_select.setValue(minute)
    } else if (this.options.get("time")=="mixed") {
      this.hour_select.setValue(""); this.minute_select.setValue("");
    }
  },
  updateValue: function() {
    var last_value = this.target_element.value;
    this.target_element.value = this.dateString();
    if (last_value!=this.target_element.value) this.callback("onchange");
  },
  today: function(now) {
    var d = new Date(); this.date = new Date();
    var o = $H({ day: d.getDate(), month: d.getMonth(), year: d.getFullYear(), hour: d.getHours(), minute: d.getMinutes(), second:d.getSeconds()});
    if ( ! now ) o = o.merge({hour: "", minute:"", second:""});
    this.updateSelectedDate(o, true);
    this.refresh();
  },
  close: function() {
    if (this.closed) return false;
    this.callback("before_close");
    this.target_element.calendar_date_select = nil;
    Event.stopObserving(document, "mousedown", this.closeIfClickedOut_handler);
    Event.stopObserving(document, "keypress", this.keyPress_handler);
    this.calendar_div.remove(); this.closed = true;
    if (this.iframe) this.iframe.remove();
    if (this.target_element.type != "hidden" && ! this.target_element.disabled) this.target_element.focus();
    this.callback("after_close");
  },
  closeIfClickedOut: function(e) {
    if (! $(Event.element(e)).descendantOf(this.calendar_div) ) this.close();
  },
  keyPress: function(e) {
    if (e.keyCode==Event.KEY_ESC) this.close();
  },
  callback: function(name, param) { if (this.options.get(name)) { this.options.get(name).bind(this.target_element)(param); } }
}

// Click customization start

// Matt Kruse Date functions start

/**
 * Copyright (c)2005-2009 Matt Kruse (javascripttoolbox.com)
 *
 * Dual licensed under the MIT and GPL licenses.
 * This basically means you can use this code however you want for
 * free, but don't claim to have written it yourself!
 * Donations always accepted: http://www.JavascriptToolbox.com/donate/
 *
 * Please do not link to the .js files on javascripttoolbox.com from
 * your site. Copy the files locally to your server instead.
 *
 */
/*
Date functions

These functions are used to parse, format, and manipulate Date objects.
See documentation and examples at http://www.JavascriptToolbox.com/lib/date/

*/
Date.$VERSION = 1.02;

// Utility function to append a 0 to single-digit numbers
Date.LZ = function(x) {return(x<0||x>9?"":"0")+x};
// Used for parsing ambiguous dates like 1/2/2000 - default to preferring 'American' format meaning Jan 2.
// Set to false to prefer 'European' format meaning Feb 1
Date.preferAmericanFormat = true;

// If the getFullYear() method is not defined, create it
if (!Date.prototype.getFullYear) {
  Date.prototype.getFullYear = function() { var yy=this.getYear(); return (yy<1900?yy+1900:yy); } ;
}

// Parse a string and convert it to a Date object.
// If no format is passed, try a list of common formats.
// If string cannot be parsed, return null.
// Avoids regular expressions to be more portable.
Date.parseString = function(val, format) {
  // If no format is specified, try a few common formats
  if (typeof(format)=="undefined" || format==null || format=="") {
    var generalFormats=new Array('y-M-d','MMM d, y','MMM d,y','y-MMM-d','d-MMM-y','MMM d','MMM-d','d-MMM');
    var monthFirst=new Array('M/d/y','M-d-y','M.d.y','M/d','M-d');
    var dateFirst =new Array('d/M/y','d-M-y','d.M.y','d/M','d-M');
    var checkList=new Array(generalFormats,Date.preferAmericanFormat?monthFirst:dateFirst,Date.preferAmericanFormat?dateFirst:monthFirst);
    for (var i=0; i<checkList.length; i++) {
      var l=checkList[i];
      for (var j=0; j<l.length; j++) {
        var d=Date.parseString(val,l[j]);
        if (d!=null) {
          return d;
        }
      }
    }
    return null;
  };

  this.isInteger = function(val) {
    for (var i=0; i < val.length; i++) {
      if ("1234567890".indexOf(val.charAt(i))==-1) {
        return false;
      }
    }
    return true;
  };
  this.getInt = function(str,i,minlength,maxlength) {
    for (var x=maxlength; x>=minlength; x--) {
      var token=str.substring(i,i+x);
      if (token.length < minlength) {
        return null;
      }
      if (this.isInteger(token)) {
        return token;
      }
    }
  return null;
  };
  val=val+"";
  format=format+"";
  var i_val=0;
  var i_format=0;
  var c="";
  var token="";
  var token2="";
  var x,y;
  var year=new Date().getFullYear();
  var month=1;
  var date=1;
  var hh=0;
  var mm=0;
  var ss=0;
  var ampm="";
  while (i_format < format.length) {
    // Get next token from format string
    c=format.charAt(i_format);
    token="";
    while ((format.charAt(i_format)==c) && (i_format < format.length)) {
      token += format.charAt(i_format++);
    }
    // Extract contents of value based on format token
    if (token=="yyyy" || token=="yy" || token=="y") {
      if (token=="yyyy") {
        x=4;y=4;
      }
      if (token=="yy") {
        x=2;y=2;
      }
      if (token=="y") {
        x=2;y=4;
      }
      year=this.getInt(val,i_val,x,y);
      if (year==null) {
        return null;
      }
      i_val += year.length;
      if (year.length==2) {
        if (year > 70) {
          year=1900+(year-0);
        }
        else {
          year=2000+(year-0);
        }
      }
    }
    else if (token=="MMM" || token=="NNN"){
      month=0;
      var names = (token=="MMM"?(Date.months.concat(Date.monthAbbreviations)):Date.monthAbbreviations);
      for (var i=0; i<names.length; i++) {
        var month_name=names[i];
        if (val.substring(i_val,i_val+month_name.length).toLowerCase()==month_name.toLowerCase()) {
          month=(i%12)+1;
          i_val += month_name.length;
          break;
        }
      }
      if ((month < 1)||(month>12)){
        return null;
      }
    }
    else if (token=="EE"||token=="E"){
      var names = (token=="EE"?Date.dayNames:Date.dayAbbreviations);
      for (var i=0; i<names.length; i++) {
        var day_name=names[i];
        if (val.substring(i_val,i_val+day_name.length).toLowerCase()==day_name.toLowerCase()) {
          i_val += day_name.length;
          break;
        }
      }
    }
    else if (token=="MM"||token=="M") {
      month=this.getInt(val,i_val,token.length,2);
      if(month==null||(month<1)||(month>12)){
        return null;
      }
      i_val+=month.length;
    }
    else if (token=="dd"||token=="d") {
      date=this.getInt(val,i_val,token.length,2);
      if(date==null||(date<1)||(date>31)){
        return null;
      }
      i_val+=date.length;
    }
    else if (token=="hh"||token=="h") {
      hh=this.getInt(val,i_val,token.length,2);
      if(hh==null||(hh<1)||(hh>12)){
        return null;
      }
      i_val+=hh.length;
    }
    else if (token=="HH"||token=="H") {
      hh=this.getInt(val,i_val,token.length,2);
      if(hh==null||(hh<0)||(hh>23)){
        return null;
      }
      i_val+=hh.length;
    }
    else if (token=="KK"||token=="K") {
      hh=this.getInt(val,i_val,token.length,2);
      if(hh==null||(hh<0)||(hh>11)){
        return null;
      }
      i_val+=hh.length;
      hh++;
    }
    else if (token=="kk"||token=="k") {
      hh=this.getInt(val,i_val,token.length,2);
      if(hh==null||(hh<1)||(hh>24)){
        return null;
      }
      i_val+=hh.length;
      hh--;
    }
    else if (token=="mm"||token=="m") {
      mm=this.getInt(val,i_val,token.length,2);
      if(mm==null||(mm<0)||(mm>59)){
        return null;
      }
      i_val+=mm.length;
    }
    else if (token=="ss"||token=="s") {
      ss=this.getInt(val,i_val,token.length,2);
      if(ss==null||(ss<0)||(ss>59)){
        return null;
      }
      i_val+=ss.length;
    }
    else if (token=="a") {
      if (val.substring(i_val,i_val+2).toLowerCase()=="am") {
        ampm="AM";
      }
      else if (val.substring(i_val,i_val+2).toLowerCase()=="pm") {
        ampm="PM";
      }
      else {
        return null;
      }
      i_val+=2;
    }
    else {
      if (val.substring(i_val,i_val+token.length)!=token) {
        return null;
      }
      else {
        i_val+=token.length;
      }
    }
  }
  // If there are any trailing characters left in the value, it doesn't match
  if (i_val != val.length) {
    return null;
  }
  // Is date valid for month?
  if (month==2) {
    // Check for leap year
    if ( ( (year%4==0)&&(year%100 != 0) ) || (year%400==0) ) { // leap year
      if (date > 29){
        return null;
      }
    }
    else {
      if (date > 28) {
        return null;
      }
    }
  }
  if ((month==4)||(month==6)||(month==9)||(month==11)) {
    if (date > 30) {
      return null;
    }
  }
  // Correct hours value
  if (hh<12 && ampm=="PM") {
    hh=hh-0+12;
  }
  else if (hh>11 && ampm=="AM") {
    hh-=12;
  }
  return new Date(year,month-1,date,hh,mm,ss);
};

// Check if a date string is valid
Date.isValid = function(val,format) {
  return (Date.parseString(val,format) != null);
};

// Check if a date object is before another date object
Date.prototype.isBefore = function(date2) {
  if (date2==null) {
    return false;
  }
  return (this.getTime()<date2.getTime());
};

// Check if a date object is after another date object
Date.prototype.isAfter = function(date2) {
  if (date2==null) {
    return false;
  }
  return (this.getTime()>date2.getTime());
};

// Check if two date objects have equal dates and times
Date.prototype.equals = function(date2) {
  if (date2==null) {
    return false;
  }
  return (this.getTime()==date2.getTime());
};

// Check if two date objects have equal dates, disregarding times
Date.prototype.equalsIgnoreTime = function(date2) {
  if (date2==null) {
    return false;
  }
  var d1 = new Date(this.getTime()).clearTime();
  var d2 = new Date(date2.getTime()).clearTime();
  return (d1.getTime()==d2.getTime());
};

// Format a date into a string using a given format string
Date.prototype.format = function(format) {
  format=format+"";
  var result="";
  var i_format=0;
  var c="";
  var token="";
  var y=this.getYear()+"";
  var M=this.getMonth()+1;
  var d=this.getDate();
  var E=this.getDay();
  var H=this.getHours();
  var m=this.getMinutes();
  var s=this.getSeconds();
  var yyyy,yy,MMM,MM,dd,hh,h,mm,ss,ampm,HH,H,KK,K,kk,k;
  // Convert real date parts into formatted versions
  var value=new Object();
  if (y.length < 4) {
    y=""+(+y+1900);
  }
  value["y"]=""+y;
  value["yyyy"]=y;
  value["yy"]=y.substring(2,4);
  value["M"]=M;
  value["MM"]=Date.LZ(M);
  value["MMM"]=Date.months[M-1];
  value["NNN"]=Date.monthAbbreviations[M-1];
  value["d"]=d;
  value["dd"]=Date.LZ(d);
  value["E"]=Date.dayAbbreviations[E];
  value["EE"]=Date.dayNames[E];
  value["H"]=H;
  value["HH"]=Date.LZ(H);
  if (H==0){
    value["h"]=12;
  }
  else if (H>12){
    value["h"]=H-12;
  }
  else {
    value["h"]=H;
  }
  value["hh"]=Date.LZ(value["h"]);
  value["K"]=value["h"]-1;
  value["k"]=value["H"]+1;
  value["KK"]=Date.LZ(value["K"]);
  value["kk"]=Date.LZ(value["k"]);
  if (H > 11) {
    value["a"]="PM";
  }
  else {
    value["a"]="AM";
  }
  value["m"]=m;
  value["mm"]=Date.LZ(m);
  value["s"]=s;
  value["ss"]=Date.LZ(s);
  while (i_format < format.length) {
    c=format.charAt(i_format);
    token="";
    while ((format.charAt(i_format)==c) && (i_format < format.length)) {
      token += format.charAt(i_format++);
    }
    if (typeof(value[token])!="undefined") {
      result=result + value[token];
    }
    else {
      result=result + token;
    }
  }
  return result;
};

// Get the full name of the day for a date
Date.prototype.getDayName = function() {
  return Date.dayNames[this.getDay()];
};

// Get the abbreviation of the day for a date
Date.prototype.getDayAbbreviation = function() {
  return Date.dayAbbreviations[this.getDay()];
};

// Get the full name of the month for a date
Date.prototype.getMonthName = function() {
  return Date.months[this.getMonth()];
};

// Get the abbreviation of the month for a date
Date.prototype.getMonthAbbreviation = function() {
  return Date.monthAbbreviations[this.getMonth()];
};

// Clear all time information in a date object
Date.prototype.clearTime = function() {
  this.setHours(0);
  this.setMinutes(0);
  this.setSeconds(0);
  this.setMilliseconds(0);
  return this;
};

// Add an amount of time to a date. Negative numbers can be passed to subtract time.
Date.prototype.add = function(interval, number) {
  if (typeof(interval)=="undefined" || interval==null || typeof(number)=="undefined" || number==null) {
    return this;
  }
  number = +number;
  if (interval=='y') { // year
    this.setFullYear(this.getFullYear()+number);
  }
  else if (interval=='M') { // Month
    this.setMonth(this.getMonth()+number);
  }
  else if (interval=='d') { // Day
    this.setDate(this.getDate()+number);
  }
  else if (interval=='w') { // Weekday
    var step = (number>0)?1:-1;
    while (number!=0) {
      this.add('d',step);
      while(this.getDay()==0 || this.getDay()==6) {
        this.add('d',step);
      }
      number -= step;
    }
  }
  else if (interval=='h') { // Hour
    this.setHours(this.getHours() + number);
  }
  else if (interval=='m') { // Minute
    this.setMinutes(this.getMinutes() + number);
  }
  else if (interval=='s') { // Second
    this.setSeconds(this.getSeconds() + number);
  }
  return this;
};

// Matt Kruse Date functions end

Date.prototype.toFormattedString = function(include_time, format) {
  var date = this.format(format);
  return date;
}

Date.parseFormattedString = function(string, format) {
  var date = Date.parseString(string, format);
  if (!date) {
    // if date cannot be parsed, return today
    date = new Date();
  }
  return date;
}

// Click customization end

/**
 * Changelog:
 *
 * 2009-05-11: Added ability to pass date format as options argument
 * 2009-05-11: Added format argument for Date.prototype.toFormattedString
 *             and Date.parseFormattedString functions
 */
