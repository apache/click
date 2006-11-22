package net.sf.click.util;

public class SampleObject {
    private Integer id;
    private String name;
    private java.util.Date dateOfBirth;
    private boolean _boolean;
    private int _int;
    private double _double;
    private String telephone;
    private boolean active;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public java.util.Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(java.util.Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
    
    public boolean isBoolean() {
        return _boolean;
    }
    
    public void setBoolean(boolean b) {
        _boolean = b;
    }
    
    public double getDouble() {
        return _double;
    }
    
    public void setDouble(double d) {
        _double = d;
    }
    
    public int getInt() {
        return _int;
    }
    
    public void setInt(int i) {
        _int = i;
    }
    
    public String getTelephone() {
        return telephone;
    }
    
    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

}
