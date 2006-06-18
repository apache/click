package net.sf.click.clicklets.sample;

public class Entry {
	private String id;
	private String name;
	private String mail;
	
	public Entry(String id, String name, String mail){
		setId(id);
		setName(name);
		setMail(mail);
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getMail() {
		return mail;
	}
	public void setMail(String mail) {
		this.mail = mail;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
}
