package net.sf.click.examples.page;

import net.sf.click.control.Form;
import net.sf.click.control.ImageSubmit;
import net.sf.click.control.Label;

/**
 * Provides an ImageSubmit control example.
 *
 * @author Malcolm Edgar
 */
public class ImageDemo extends BorderPage {

    private ImageSubmit colorSubmit;

    public Form buttonsForm = new Form();
    public Form form = new Form();

    public ImageDemo() {
        // Buttons Form
        ImageSubmit editSubmit = new ImageSubmit("edit", "images/edit.gif");
        editSubmit.setListener(this, "onEditClick");
        editSubmit.setTitle("Edit");
        buttonsForm.add(editSubmit);

        ImageSubmit deleteSubmit = new ImageSubmit("delete", "images/delete.gif");
        deleteSubmit.setListener(this, "onDeleteClick");
        deleteSubmit.setTitle("Delete");
        buttonsForm.add(deleteSubmit);

        // Colors Form
        form.add(new Label("label", "<b>Color Chooser</b>"));

        colorSubmit = new ImageSubmit("submit", "images/colors.gif");
        colorSubmit.setListener(this, "onColorClick");
        form.add(colorSubmit);
    }

    public boolean onEditClick() {
        addModel("buttonMsg", "Edit");
        return true;
    }

    public boolean onDeleteClick() {
        addModel("buttonMsg", "Delete");
        return true;
    }

    public boolean onColorClick() {
        int x = colorSubmit.getX();
        int y = colorSubmit.getY();

        String color = "no color";

        if (x > 3 && x < 31) {
            if (y > 3 && y < 31) {
                color = "Red";
            } else if (y > 44 && y < 71) {
                color = "Green";
            }
        } else if (x > 44 && x < 71) {
            if (y > 3 && y < 31) {
                color = "Blue";
            } else if (y > 44 && y < 71) {
                color = "White";
            }
        }

        String colorMsg = "<b>" + color + "</b>. <p/> " +
                          "[ x=" + x + ", y=" + y + " ]";

        addModel("colorMsg", colorMsg);

        return true;
    }
}
