package benchmark.click.controls;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import net.sf.click.control.TextField;

public class DateTextField extends TextField {

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");

    public DateTextField(String name) {
        super(name);
    }

    public Object getValueObject() {
        return getDate();
    }

    public void setValueObject(Object object) {
        if (object != null) {
            if (Date.class.isAssignableFrom(object.getClass())) {
                setDate((Date) object);

            } else {
                String msg =
                    "Invalid object class: " + object.getClass().getName();
                throw new IllegalArgumentException(msg);
            }
        }
    }

    public Object getDate() {
        if (value != null && value.length() > 0) {
            try {
                Date date = dateFormat.parse(value);
                return new Date(date.getTime());
            } catch (ParseException pe) {
                return null;
            }
        } else {
            return null;
        }
    }

    public void setDate(Date date) {
        if (date != null) {
            value = dateFormat.format(date);
        }
    }
}
