package examples.page;

import java.util.ArrayList;
import java.util.List;

import net.sf.click.control.Form;
import net.sf.click.control.HiddenField;
import net.sf.click.control.Select;
import net.sf.click.control.Submit;

/**
 * Provides a example Page to demonstrate Form layout options.
 * 
 * @author Malcolm Edgar
 */
public class LayoutForm extends EditCustomer {
    
    static final List ALIGN_OPTIONS = new ArrayList();
    static {
        ALIGN_OPTIONS.add(new Select.Option("left", "Left"));
        ALIGN_OPTIONS.add(new Select.Option("center", "Center"));
        ALIGN_OPTIONS.add(new Select.Option("right", "Right"));
    }
    
    HiddenField formLayoutHidden = new HiddenField("formLayout", Integer.class);
    HiddenField labelAlignHidden= new HiddenField("labelAlign", String.class);
    HiddenField buttonAlignHidden = new HiddenField("buttonAlign", String.class);
    
    Form styleForm;
    Select formLayoutSelect;
    Select labelAlignSelect;
    Select buttonAlignSelect;

    public void onInit() {
        super.onInit();
        
        // Add hidden form style field
        formLayoutHidden.setValue(new Integer(Form.LABEL_ON_LEFT));
        form.add(formLayoutHidden);
        labelAlignHidden.setValue("left");
        form.add(labelAlignHidden);
        buttonAlignHidden.setValue("left");
        form.add(buttonAlignHidden);
        
        // Unset EditCustomer.onOkClick() listener
        okButton.setListener(null, null);
        emailField.setRequired(true);
        
        // Add style form to modify the original forms layout
        styleForm = new Form("styleForm", getContext());
        styleForm.setButtonAlign("center");
        addControl(styleForm);
        
        formLayoutSelect = new Select("Form Layout");
        formLayoutSelect.add(new Select.Option("left", "Label on Left"));
        formLayoutSelect.add(new Select.Option("top", "Label on Top"));
        styleForm.add(formLayoutSelect);
        
        labelAlignSelect = new Select("Label Align");
        labelAlignSelect.addAll(ALIGN_OPTIONS);
        styleForm.add(labelAlignSelect);

        buttonAlignSelect = new Select("Button Align");
        buttonAlignSelect.addAll(ALIGN_OPTIONS);
        styleForm.add(buttonAlignSelect);
        
        Submit applyButton = new Submit("   Apply Layout   ");
        applyButton.setTitle("Apply the layout to the form");
        applyButton.setListener(this, "onApplyClick");
        styleForm.add(applyButton);
    }
    
    /**
     * Apply the hidden field form styles to the form and the style display
     * controls.
     */
    public void onPost() {
        Integer formLayout = (Integer) formLayoutHidden.getValueObject();
        
//TODO: NPE probably formLayout        
        form.setLayout(formLayout.intValue());
        form.setLabelAlign(labelAlignHidden.getValue());
        form.setButtonAlign(buttonAlignHidden.getValue());
        
        if (formLayout.intValue() == Form.LABEL_ON_LEFT) {
            formLayoutSelect.setValue("left");
        } else {
            formLayoutSelect.setValue("top");
        }
        labelAlignSelect.setValue(labelAlignHidden.getValue());
        buttonAlignSelect.setValue(buttonAlignHidden.getValue());
    }
    
    /**
     * Apply the layout to the form.
     * 
     * @return true
     */
    public boolean onApplyClick() {
        if (formLayoutSelect.getValue().equals("left")) {
            formLayoutHidden.setValue(new Integer(Form.LABEL_ON_LEFT));     
        } else {
            formLayoutHidden.setValue(new Integer(Form.LABEL_ON_TOP));               
        }
        
        labelAlignHidden.setValue(labelAlignSelect.getValue());
        buttonAlignHidden.setValue(buttonAlignSelect.getValue());
   
        return true;
    }
    
    /**
     * On a POST Cancel button submit redirect to "examples.html"
     * <p/>
     * Override <tt>EditCustomer.onCancelClick()</tt> method
     * 
     * @return false to stop processing
     */
    public boolean onCancelClick() {
        setRedirect("examples.html");
        return false;
    }
}
