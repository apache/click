package net.sf.click.examples.domain.auto;

/**
 * Class _Customer was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public class _Customer extends net.sf.click.examples.domain.BaseEntity {

    private static final long serialVersionUID = 1L;

    public static final String ACTIVE_PROPERTY = "active";
    public static final String AGE_PROPERTY = "age";
    public static final String DATE_JOINED_PROPERTY = "dateJoined";
    public static final String EMAIL_PROPERTY = "email";
    public static final String HOLDINGS_PROPERTY = "holdings";
    public static final String INVESTMENTS_PROPERTY = "investments";
    public static final String NAME_PROPERTY = "name";

    public static final String ID_PK_COLUMN = "id";

    public void setActive(Boolean active) {
        writeProperty("active", active);
    }
    public Boolean getActive() {
        return (Boolean)readProperty("active");
    }


    public void setAge(Integer age) {
        writeProperty("age", age);
    }
    public Integer getAge() {
        return (Integer)readProperty("age");
    }


    public void setDateJoined(java.util.Date dateJoined) {
        writeProperty("dateJoined", dateJoined);
    }
    public java.util.Date getDateJoined() {
        return (java.util.Date)readProperty("dateJoined");
    }


    public void setEmail(String email) {
        writeProperty("email", email);
    }
    public String getEmail() {
        return (String)readProperty("email");
    }


    public void setHoldings(Double holdings) {
        writeProperty("holdings", holdings);
    }
    public Double getHoldings() {
        return (Double)readProperty("holdings");
    }


    public void setInvestments(String investments) {
        writeProperty("investments", investments);
    }
    public String getInvestments() {
        return (String)readProperty("investments");
    }


    public void setName(String name) {
        writeProperty("name", name);
    }
    public String getName() {
        return (String)readProperty("name");
    }


}
