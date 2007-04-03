function pickListMove(from, to, hidden, isSelected){
	for(var i=0;i<from.options.length;i++){
		if(from.options[i].selected){
			pickListMoveItem(from, to, i, hidden, isSelected);
			i--;
		}
	}
}

function pickListMoveAll(from, to, hidden, isSelected){
	for(i=0;i<from.options.length;i++){
		pickListMoveItem(from, to, i, hidden, isSelected);
		i--;
	}
}

function pickListMoveItem(from, to, index, hidden, isSelected){
	var insertIndex = 0;
	if(isSelected){
		for(var i=0;i<hidden.options.length;i++){
			if(hidden.options[i].value == from.options[index].value){
				hidden.options[i].selected = true;
				insertIndex = i;
			}
		}
	} else {
		for(var i=0;i<hidden.options.length;i++){
			if(hidden.options[i].value == from.options[index].value){
				hidden.options[i].selected = false;
				insertIndex = i;
			}
		}
	}
	for(var i=to.options.length-1;i>=insertIndex;i--){
		to.options[i+1] = new Option(to.options[i].text, to.options[i].value);
	}
	if(insertIndex > to.options.length){
		insertIndex = to.options.length;
	}
	to.options[insertIndex] = new Option(from.options[index].text, from.options[index].value);
	from.options[index] = null;
}

function validatePickList(id, required, msgs){
	var field = document.getElementById(id);
	if(field){
		if (field.options.length == 0) {
			if(required){
				setFieldErrorColor(field);
				return msgs[0];
			}
		}
		
		setFieldValidColor(field);
		return null;
		
	} else {
		return 'Field ' + id + ' not found.';
	}
}
