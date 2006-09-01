function validateNumericField(id, required, minValue, maxValue, msgs){
	var field = document.getElementById(id);
	if(field){
		var value = field.value;
		if (value.length == 0) {
			if(required){
				setFieldErrorColor(field);
				return msgs[0];
			}
		} else {
			if (value > maxValue){
				setFieldErrorColor(field);
				return msgs[2];
			} else if (value < minValue){
				setFieldErrorColor(field);
				return msgs[1];
			}
		}
		
		setFieldValidColor(field);
		return null;
		
	} else {
		return 'Field ' + id + ' not found.';
	}
}
