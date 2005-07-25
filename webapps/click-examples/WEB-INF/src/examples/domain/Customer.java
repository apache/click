package examples.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * Provides a mockup persistent Customer business object for the examples.
 *
 * @author Malcolm Edgar
 */
public class Customer implements Serializable {

    Long id;
    String name;
    String email;
    Integer age;
    Double holdings;
    String investments;
    Date dateJoined;

    /**
     * @return Returns the age.
     */
    public Integer getAge() {
        return age;
    }

    /**
     * @param age The age to set.
     */
    public void setAge(Integer age) {
        this.age = age;
    }

    /**
     * @return Returns the email.
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email The email to set.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return Returns the holdings.
     */
    public Double getHoldings() {
        return holdings;
    }

    /**
     * @param holdings The holdings to set.
     */
    public void setHoldings(Double holdings) {
        this.holdings = holdings;
    }

    /**
     * @return Returns the id.
     */
    public Long getId() {
        return id;
    }
    /**
     * @param id The id to set.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return Returns the investments.
     */
    public String getInvestments() {
        return investments;
    }

    /**
     * @param investments The investments to set.
     */
    public void setInvestments(String investments) {
        this.investments = investments;
    }

    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return Returns the date joined.
     */
    public Date getDateJoined() {
        return dateJoined;
    }

    /**
     * @param value The date joined to set.
     */
    public void setDateJoined(Date value) {
        dateJoined = value;
    }

    private static final long serialVersionUID = 1L;

}
