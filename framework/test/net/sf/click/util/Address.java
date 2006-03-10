package net.sf.click.util;

import net.sf.click.util.State;

public class Address {
    private Integer id;
    private String lineOne;
    private String lineTwo;
    private String lineThree;
    private State state;
    
    public State getState() {
        return state;
    }
    public void setState(State state) {
        this.state = state;
    }
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getLineOne() {
        return lineOne;
    }
    public void setLineOne(String lineOne) {
        this.lineOne = lineOne;
    }
    public String getLineThree() {
        return lineThree;
    }
    public void setLineThree(String lineThree) {
        this.lineThree = lineThree;
    }
    public String getLineTwo() {
        return lineTwo;
    }
    public void setLineTwo(String lineTwo) {
        this.lineTwo = lineTwo;
    }
}
