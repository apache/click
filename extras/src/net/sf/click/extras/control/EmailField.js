function validateEmailField (id, required, minLength, maxLength, msgs){
	var msg = validateTextField(id, required, minLength, maxLength, msgs);
	if (msg) {
		return msg;
	} else {
		var field  = document.getElementById(id);
		var value  = field.value;
		var length = value.length;
		
		if(length > 0){
			var index = value.indexOf("@");
			
			if (index < 1 || index == length - 1) {
				setFieldErrorColor(field);
				return msgs[3];
			}
	
			if (!isLetterOrDigit(value.charAt(0))) {
				setFieldErrorColor(field);
				return msgs[3];
			}
	
			if (!isLetterOrDigit(value.charAt(length - 1))) {
				setFieldErrorColor(field);
				return msgs[3];
			}
		}
	}
	setFieldValidColor(field);
	return null;
}

function isLetter (c){
	return ( ((c >= "a") && (c <= "z")) || ((c >= "A") && (c <= "Z")) )
}

function isDigit (c){
	return ((c >= "0") && (c <= "9"))
}

function isLetterOrDigit (c){
	return (isLetter(c) || isDigit(c))
}

