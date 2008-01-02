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
	var toIndex = 0;
	for(var i=0;i<hidden.options.length;i++){
		if(hidden.options[i].value == from.options[index].value){
			hidden.options[i].selected = isSelected;
		}
		if(hidden.options[i].selected == isSelected){
			to.options[toIndex] = new Option(hidden.options[i].text, hidden.options[i].value);
			toIndex++;
		}
	}
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
