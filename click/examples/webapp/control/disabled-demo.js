$(document).ready(function() {
    $('#form_toggle').click(function() {
        if($(this).is(':checked')) {
            toggle(false);
            $("label[for='form_toggle']").text('Disable Fields');
        } else {
            toggle(true);
            $("label[for='form_toggle']").text('Enable Fields');
        }
    });

    function toggle(disable) {
        var nameFld = $('#form_name');
        var investmentSelect = $('#form_investments');

        if (disable) {
            // set the disabled attribute and 'disabled' CSS class
            nameFld.attr('disabled', 'disabled').attr('class', 'disabled');
            investmentSelect.attr('disabled', 'disabled').attr('class', 'disabled');
        } else {
            // remove the disabled attribute and 'disabled' CSS class
            nameFld.removeAttr('disabled').removeAttr('class');
            investmentSelect.removeAttr('disabled').removeAttr('class');
        }
    }
})
