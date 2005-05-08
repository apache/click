function toggleBorders(checkbox) {
  var formTable = document.getElementById("form-form");
  var fieldsTable = document.getElementById("form-fields");
  var buttonsTable = document.getElementById("form-buttons");
  var errorsTable = document.getElementById("form-errors");
  
  if (checkbox.checked) {
    formTable.style.border = "1px dotted gray";
    fieldsTable.style.border = "1px dashed blue";
    buttonsTable.style.border = "1px dashed green";
    if (errorsTable) {
      errorsTable.style.border = "1px dashed red";
    }
  } else {
    formTable.style.border = "0px dotted gray";
    fieldsTable.style.border = "0px dashed blue";
    buttonsTable.style.border = "0px dashed green";
    if (errorsTable) {
      errorsTable.style.border = "0px dashed red";
    }
  }
  document.form.showBorders.value = checkbox.checked;
}
