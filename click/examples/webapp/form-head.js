#*
// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License. *#

function toggleBorders(checkboxId) {
  var checkbox = document.getElementById(checkboxId);
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
  var optionsForm = document.getElementById("optionsForm");
  optionsForm.showBorders.value = checkbox.checked;
}

toggleBorders("optionsForm_showBorders");