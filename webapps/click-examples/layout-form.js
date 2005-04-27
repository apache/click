function toggleBorders(checkbox) {
  var fieldsTable = document.getElementById("form-fields");
  var buttonsTable = document.getElementById("form-buttons");
  var errorsTable = document.getElementById("form-errors");
  
  if (checkbox.checked) {
    fieldsTable.style.border = "1px dashed blue";
    buttonsTable.style.border = "1px dashed green";
    if (errorsTable) {
      errorsTable.style.border = "1px dashed red";
    }
  } else {
    fieldsTable.style.border = "0px dashed blue";
    buttonsTable.style.border = "0px dashed green";
    if (errorsTable) {
      errorsTable.style.border = "0px dashed red";
    }
  }
  document.form.showBorders.value = checkbox.checked;
}
