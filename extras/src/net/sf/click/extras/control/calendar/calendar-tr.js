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
 "Sal�",
 "�ar�amba",
 "Per�embe",
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
 "�r�",
 "Pr�",
 "Cum",
 "Cmt",
 "Pzr");

// First day of the week. "0" means display Sunday first, "1" means display
// Monday first, etc.
Calendar._FD = 1;

// full month names
Calendar._MN = new Array
("Ocak",
 "�ubat",
 "Mart",
 "Nisan",
 "May�s",
 "Haziran",
 "Temmuz",
 "A�ustos",
 "Eyl�l",
 "Ekim",
 "Kas�m",
 "Aral�k");

// short month names
Calendar._SMN = new Array
("Oca",
 "�ub",
 "Mar",
 "Nis",
 "May",
 "Haz",
 "Tem",
 "A�u",
 "Eyl",
 "Eki",
 "Kas",
 "Ara");

// tooltips
Calendar._TT = {};
Calendar._TT["INFO"] = "Takvim hakk�nda";

Calendar._TT["ABOUT"] =
"DHTML Date/Time Selector\n" +
"(c) dynarch.com 2002-2005 / Yazar: Mihai Bazon\n" + // don't translate this this ;-)
"En son s�r�m i�in: http://www.dynarch.com/projects/calendar/\n" +
"GNU LGPL alt�nda da��t�lmaktad�r. Detaylar i�in http://gnu.org/licenses/lgpl.html sayfas�na bak�n�z." +
"\n\n" +
"Tarih se�imi:\n" +
"- \xab ve \xbb tu�lar� ile y�l se�ebilirsiniz.\n" +
"- " + String.fromCharCode(0x2039) + " ve " + String.fromCharCode(0x203a) + " tu�lar� ile aylar� se�ebilirsiniz.\n" +
"- Daha h�zl� se�im yapmak i�in fare d��melerini bu butonlar�n �zerinde bas�l� tutun.";
Calendar._TT["ABOUT_TIME"] = "\n\n" +
"Saat se�imi:\n" +
"- Zaman par�alar�n� t�klayarak art�r�n\n" +
"- veya Shift tu�una bas�l� tutarak t�klay�n ve azalt�n\n" +
"- veya daha h�zl� se�im i�in s�r�kleyip b�rak�n.";

Calendar._TT["PREV_YEAR"] = "�nceki Y�l (Men� i�in bas�l� tutunuz)";
Calendar._TT["PREV_MONTH"] = "�nceki Ay (Men� i�in bas�l� tutunuz)";
Calendar._TT["GO_TODAY"] = "Bug�n'e git";
Calendar._TT["NEXT_MONTH"] = "Sonraki Ay (Men� i�in bas�l� tutunuz)";
Calendar._TT["NEXT_YEAR"] = "Sonraki Y�l (Men� i�in bas�l� tutunuz)";
Calendar._TT["SEL_DATE"] = "Tarih se�iniz";
Calendar._TT["DRAG_TO_MOVE"] = "Ta��mak i�in s�r�kleyiniz";
Calendar._TT["PART_TODAY"] = " (bug�n)";

// the following is to inform that "%s" is to be the first day of week
// %s will be replaced with the day name.
Calendar._TT["DAY_FIRST"] = "%s �nce g�sterilsin";

// This may be locale-dependent.  It specifies the week-end days, as an array
// of comma-separated numbers.  The numbers are from 0 to 6: 0 means Sunday, 1
// means Monday, etc.
Calendar._TT["WEEKEND"] = "1,7";

Calendar._TT["CLOSE"] = "Kapat";
Calendar._TT["TODAY"] = "Bug�n";
Calendar._TT["TIME_PART"] = "De�eri de�i�tirmek i�in (Shift ile) t�klay�n veya s�r�kleyin";

// date formats
Calendar._TT["DEF_DATE_FORMAT"] = "%d-%m-%Y";
Calendar._TT["TT_DATE_FORMAT"] = "%a, %b %e";

Calendar._TT["WK"] = "Hafta";
Calendar._TT["TIME"] = "Saat:";
