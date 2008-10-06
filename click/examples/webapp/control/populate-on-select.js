/**
 * Prints out the selected option for each of the three Select Controls
 */
function printValues() {
    var log = document.getElementById('log');
    log.style.display = 'block';

    // Select ids will be passed in from Java
    var state = document.getElementById('${stateId}');
    var city = document.getElementById('${cityId}');
    var suburb = document.getElementById('${suburbId}');
    
    var html = 'Selected state: key -> ' + state.value + ', value -> ' + state.options[state.selectedIndex].text;
    html+="<br>" + 'Selected city: key -> ' + city.value  + ', value -> ' + city.options[city.selectedIndex].text;
    html+="<br>" + 'Selected suburb: key -> ' + suburb.value  + ', value -> ' + suburb.options[suburb.selectedIndex].text;
    log.innerHTML = html;
}

/**
 * This method is invoked when a Select control is changed.
 *
 * @id specifies the Select control that must be reset
 * @form specified the form that must be submitted
 */
function handleChange(id, form) {
    var select=document.getElementById(id);
    if(select != null) {
        select.selectedIndex=-1;
    }
    form.submit();
}