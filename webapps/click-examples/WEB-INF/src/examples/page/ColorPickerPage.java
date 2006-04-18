package examples.page;

import java.util.ArrayList;
import java.util.List;

import net.sf.click.control.Form;
import net.sf.click.control.Submit;
import net.sf.click.extras.control.ColorPicker;

public class ColorPickerPage extends BorderedPage {

    private ColorPicker cp1;
    private ColorPicker cp2;
    private ColorPicker cp3;
    private ColorPicker cp4;
    private Form form;

    public ColorPickerPage() {
        form = new Form("form");

        cp1 = new ColorPicker("cp1", "Color Picker 1");
        form.add(cp1);

        cp2 = new ColorPicker("cp2", "Color Picker 2");
        cp2.setRequired(true);
        cp2.setValue("#999");
        form.add(cp2);

        cp3 = new ColorPicker("cp3", "Color Picker 3");
        cp3.setShowTextField(false);
        form.add(cp3);

        cp4 = new ColorPicker("cp4", "Color Picker 4");
        cp4.setShowTextField(false);
        cp4.setRequired(true);
        form.add(cp4);

        form.add(new Submit("ok", "  OK  "));

        addControl(form);
    }

    public void onPost() {
        if(form.isValid()) {
            List colors = new ArrayList(4);

            colors.add(makeColor(cp1.getValue()));
            colors.add(makeColor(cp2.getValue()));
            colors.add(makeColor(cp3.getValue()));
            colors.add(makeColor(cp4.getValue()));

            addModel("colors", colors);
        }
    }

    private Object makeColor(String c) {
        if (c == null || c.length() == 0) {
            return Boolean.FALSE;
        }
        return c;
    }

}
