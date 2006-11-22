package net.sf.click.util;

public class ChildObject {
	
	private String name;
    private String email;
	
	public ChildObject() {
	}

	public ChildObject(String name, String email) {
		this.name = name;
        this.email = email;
	}

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
    
    public String email() {
        return email;
    }
}