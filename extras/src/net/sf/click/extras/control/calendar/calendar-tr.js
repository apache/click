// ** I18N

// Calendar TR language
// Author: Ali Ok, <aliok@aliok.info>
// Encoding: any
// Used work of Nuri AKMAN
// Distributed under the same terms as the calendar itself.

// For translators: please use UTF-8 if possible.  We strongly believe that
// Unicode is the answer to a real internationalized world.  Also please
// include your contact information in the header, as can be seen above.

// full day names
Calendar._DN = new Array
("Pazar",
 "Pazartesi",
 "Salý",
 "Çarþamba",
 "Perþembe",
 "Cuma",
 "Cumartesi",
 "Pazar");

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
("Pzr",
 "Pzt",
 "Sal",
 "Çrþ",
 "Prþ",
 "Cum",
 "Cmt",
 "Pzr");

// First day of the week. "0" means display Sunday first, "1" means display
// Monday first, etc.
Calendar._FD = 1;

// full month names
Calendar._MN = new Array
("Ocak",
 "Þubat",
 "Mart",
 "Nisan",
 "Mayýs",
 "Haziran",
 "Temmuz",
 "Aðustos",
 "Eylül",
 "Ekim",
 "Kasým",
 "Aralýk");

// short month names
Calendar._SMN = new Array
("Oca",
 "Þub",
 "Mar",
 "Nis",
 "May",
 "Haz",
 "Tem",
 "Aðu",
 "Eyl",
 "Eki",
 "Kas",
 "Ara");

// tooltips
Calendar._TT = {};
Calendar._TT["INFO"] = "Takvim hakkýnda";

Calendar._TT["ABOUT"] =
"DHTML Date/Time Selector\n" +
"(c) dynarch.com 2002-2005 / Yazar: Mihai Bazon\n" + // don't translate this this ;-)
"En son sürüm için: http://www.dynarch.com/projects/calendar/\n" +
"GNU LGPL altýnda daðýtýlmaktadýr. Detaylar için http://gnu.org/licenses/lgpl.html sayfasýna bakýnýz." +
"\n\n" +
"Tarih seçimi:\n" +
"- \xab ve \xbb tuþlarý ile yýl seçebilirsiniz.\n" +
"- " + String.fromCharCode(0x2039) + " ve " + String.fromCharCode(0x203a) + " tuþlarý ile aylarý seçebilirsiniz.\n" +
"- Daha hýzlý seçim yapmak için fare düðmelerini bu butonlarýn üzerinde basýlý tutun.";
Calendar._TT["ABOUT_TIME"] = "\n\n" +
"Saat seçimi:\n" +
"- Zaman parçalarýný týklayarak artýrýn\n" +
"- veya Shift tuþuna basýlý tutarak týklayýn ve azaltýn\n" +
"- veya daha hýzlý seçim için sürükleyip býrakýn.";

Calendar._TT["PREV_YEAR"] = "Önceki Yýl (Menü için basýlý tutunuz)";
Calendar._TT["PREV_MONTH"] = "Önceki Ay (Menü için basýlý tutunuz)";
Calendar._TT["GO_TODAY"] = "Bugün'e git";
Calendar._TT["NEXT_MONTH"] = "Sonraki Ay (Menü için basýlý tutunuz)";
Calendar._TT["NEXT_YEAR"] = "Sonraki Yýl (Menü için basýlý tutunuz)";
Calendar._TT["SEL_DATE"] = "Tarih seçiniz";
Calendar._TT["DRAG_TO_MOVE"] = "Taþýmak için sürükleyiniz";
Calendar._TT["PART_TODAY"] = " (bugün)";

// the following is to inform that "%s" is to be the first day of week
// %s will be replaced with the day name.
Calendar._TT["DAY_FIRST"] = "%s önce gösterilsin";

// This may be locale-dependent.  It specifies the week-end days, as an array
// of comma-separated numbers.  The numbers are from 0 to 6: 0 means Sunday, 1
// means Monday, etc.
Calendar._TT["WEEKEND"] = "1,7";

Calendar._TT["CLOSE"] = "Kapat";
Calendar._TT["TODAY"] = "Bugün";
Calendar._TT["TIME_PART"] = "Deðeri deðiþtirmek için (Shift ile) týklayýn veya sürükleyin";

// date formats
Calendar._TT["DEF_DATE_FORMAT"] = "%d-%m-%Y";
Calendar._TT["TT_DATE_FORMAT"] = "%a, %b %e";

Calendar._TT["WK"] = "Hafta";
Calendar._TT["TIME"] = "Saat:";
