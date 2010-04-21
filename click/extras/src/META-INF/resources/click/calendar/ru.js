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

Date.months = new Array('Январь','Февраль','Март','Апрель','Май','Июнь','Июль','Август','Сентябрь','Октябрь','Ноябрь','Декабрь');

// Month abbreviations
Date.monthAbbreviations = new Array('Янв','Фев','Мар','Апр','Май','Июн','Июл','Авг','Сен','Окт','Ноя','Дек');

// Full day names
Date.dayNames = new Array('Воскресенье','Понедельник','Вторник','Среда','Четверг','Пятница','Суббота');

// Day abbreviations
Date.dayAbbreviations = new Array('Вс','Пн','Вт','Ср','Чт','Пт','Сб');

// Weekdays displayed by popup calendar
Date.weekdays = new Array('Пн','Вт','Ср','Чт','Пт','Сб','Вс');

Date.first_day_of_week = 1

_translations = {
  "OK": "OK",
  "Now": "Сейчас",
  "Today": "Сегодня",
  "Clear": "Очистить"
}

Date.prototype.getAMPMHour = function() { var hour = this.getHours(); return hour; }
Date.prototype.getAMPM = function() { return ""; }