package net.sf.click.util;

import java.util.Date;

public class ParentObject {
	
    private String name;
    private Object value;
    private Date date;
    private ChildObject child; 
    private Boolean valid;
    
    public ParentObject(String name, Object value, Date date, Boolean valid, ChildObject child) {
        this.name = name;
        this.value = value;
        this.date = date;
        this.valid = valid;
        this.child = child;
    }
    
    public ParentObject() {
    }
    
    public String getName() {
        return name;
    }
    
    public Date getDate() {
    	return date;
    }
    
    public Object getValue() {
        return value;
    }
    
    public ChildObject getChild() {
    	return child;
    }
    
    public void setChild(ChildObject child) {
    	this.child = child;
    }
    
    public Boolean isValid() {
    	return valid;
    }
}

