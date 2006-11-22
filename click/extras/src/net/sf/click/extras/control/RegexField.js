function validateRegexField(id, required, minLength, maxLength, regex, msgs){
	var msg = validateTextField(id, required, minLength, maxLength, msgs);
	if (msg) {
		return msg;
	} else {
		var field = document.getElementById(id);
		if (field.value.match(new RegExp(regex))) {
			setFieldValidColor(field);
			return null;
		} else {
			setFieldErrorColor(field);
			return msgs[3];
		}
	}
	return null;
}
