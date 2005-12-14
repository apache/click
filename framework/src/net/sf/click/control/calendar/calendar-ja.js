// ** I18N

// Calendar JA language
// Author: Naoki Takezoe, <takezoe@gmail.com>
// Encoding: Shift_JIS
// Distributed under the same terms as the calendar itself.

// For translators: please use UTF-8 if possible.  We strongly believe that
// Unicode is the answer to a real internationalized world.  Also please
// include your contact information in the header, as can be seen above.

// full day names
Calendar._DN = new Array
("“ú—j“ú",
 "Œ—j“ú",
 "‰Î—j“ú",
 "…—j“ú",
 "–Ø—j“ú",
 "‹à—j“ú",
 "“y—j“ú",
 "“ú—j“ú");

// Please note that the following array of short day names (and the same goes
// for short month names, _SMN) isn't absolutely necessary.  We give it here
// for exemplification on how one can customize the short day names, but if
// they are simply the first N letters of the full name you can simply say:
//
//   Calendar._SDN_len = N; // short day name length
//   Calendar._SMN_len = N; // short month name length
//
// If N = 3 then this is not needed either since we assume a value of 3 if not
// present, to be compatible with translation files that were written before
// this feature.

// short day names
Calendar._SDN = new Array
("“ú",
 "Œ",
 "‰Î",
 "…",
 "–Ø",
 "‹à",
 "“y",
 "“ú");

// First day of the week. "0" means display Sunday first, "1" means display
// Monday first, etc.
Calendar._FD = 0;

// full month names
Calendar._MN = new Array
("1Œ",
 "2Œ",
 "3Œ",
 "4Œ",
 "5Œ",
 "6Œ",
 "7Œ",
 "8Œ",
 "9Œ",
 "10Œ",
 "11Œ",
 "12Œ");

// short month names
Calendar._SMN = new Array
("1Œ",
 "2Œ",
 "3Œ",
 "4Œ",
 "5Œ",
 "6Œ",
 "7Œ",
 "8Œ",
 "9Œ",
 "10Œ",
 "11Œ",
 "12Œ");

// tooltips
Calendar._TT = {};
Calendar._TT["INFO"] = "ƒJƒŒƒ“ƒ_‚É‚Â‚¢‚Ä";

Calendar._TT["ABOUT"] =
"DHTML Date/Time Selector\n" +
"(c) dynarch.com 2002-2005 / Author: Mihai Bazon\n" + // don't translate this this ;-)
"For latest version visit: http://www.dynarch.com/projects/calendar/\n" +
"Distributed under GNU LGPL.  See http://gnu.org/licenses/lgpl.html for details." +
"\n\n" +
"Date selection:\n" +
"- Use the \xab, \xbb buttons to select year\n" +
"- Use the " + String.fromCharCode(0x2039) + ", " + String.fromCharCode(0x203a) + " buttons to select month\n" +
"- Hold mouse button on any of the above buttons for faster selection.";
Calendar._TT["ABOUT_TIME"] = "\n\n" +
"Time selection:\n" +
"- Click on any of the time parts to increase it\n" +
"- or Shift-click to decrease it\n" +
"- or click and drag for faster selection.";

Calendar._TT["PREV_YEAR"] = "‘O”N";
Calendar._TT["PREV_MONTH"] = "‘OŒ";
Calendar._TT["GO_TODAY"] = "¡“ú";
Calendar._TT["NEXT_MONTH"] = "—‚Œ";
Calendar._TT["NEXT_YEAR"] = "—‚”N";
Calendar._TT["SEL_DATE"] = "“ú•t‘I‘ğ";
Calendar._TT["DRAG_TO_MOVE"] = "ƒEƒBƒ“ƒhƒE‚ÌˆÚ“®";
Calendar._TT["PART_TODAY"] = " (¡“ú)";

// the following is to inform that "%s" is to be the first day of week
// %s will be replaced with the day name.
Calendar._TT["DAY_FIRST"] = "%s ‚ğæ“ª‚É";

// This may be locale-dependent.  It specifies the week-end days, as an array
// of comma-separated numbers.  The numbers are from 0 to 6: 0 means Sunday, 1
// means Monday, etc.
Calendar._TT["WEEKEND"] = "0,6";

Calendar._TT["CLOSE"] = "•Â‚¶‚é";
Calendar._TT["TODAY"] = "¡“ú";
Calendar._TT["TIME_PART"] = "(Shift-)Click ‚©ƒhƒ‰ƒbƒO‚Å’l‚ğ•ÏX";

// date formats
Calendar._TT["DEF_DATE_FORMAT"] = "%Y-%m-%d";
Calendar._TT["TT_DATE_FORMAT"] = "%a, %b %e";

Calendar._TT["WK"] = "T";
Calendar._TT["TIME"] = ":";
