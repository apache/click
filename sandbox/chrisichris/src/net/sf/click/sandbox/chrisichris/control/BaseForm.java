package net.sf.click.sandbox.chrisichris.control;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.lang.StringUtils;

import net.sf.click.Context;
import net.sf.click.Control;
import net.sf.click.Page;
import net.sf.click.control.ActionLink;
import net.sf.click.control.Button;
import net.sf.click.control.Field;
import net.sf.click.control.FieldSet;
import net.sf.click.control.FileField;
import net.sf.click.control.Form;
import net.sf.click.control.HiddenField;
import net.sf.click.util.ClickUtils;
import net.sf.click.util.HtmlStringBuffer;

public class BaseForm extends Form {

    private final class NameHiddenField extends HiddenField {
        NameHiddenField() {
            super(ActionLink.ACTION_LINK, String.class);
        }

        public String toString() {
            setValue(BaseForm.this.getId());
            return super.toString();
        }
    }

    protected Field focusField;

    public BaseForm() {
        super();
        add(new NameHiddenField());
    }

    public BaseForm(String name) {
        super(name);
        add(new NameHiddenField());
    }
    
    public String getId() {
        if (hasAttributes() && getAttributes().containsKey("id")) {
            return getAttribute("id");
        } else {
            String id = ControlUtils.getId(this);
            if(id != null) {
                setAttribute("id",id);
            }
            return id;
        }
    }

    public void setName(String name) {
        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("The name is blank or null");
        }
        this.name = name;
    }
    
    public boolean isFormSubmission() {
        if (getContext() == null) {
            throw new IllegalStateException("Context has not been set");
        }
        // check the method
        String requestMethod = getContext().getRequest().getMethod();
        if (!getMethod().equalsIgnoreCase(requestMethod)) {
            return false;
        }

        // resolve multipart
        // If "multipart/form-data" request and not already loaded then
        // load form data FileItem into context
        if (getContext().isMultipartRequest()
                && getContext().getMultiPartFormData() == Collections.EMPTY_MAP) {

            FileField fileField = null;
            List fieldList = ClickUtils.getFormFields(this);
            for (int i = 0, size = fieldList.size(); i < size; i++) {
                Field field = (Field) fieldList.get(i);
                if (!field.isHidden() && (field instanceof FileField)) {
                    fileField = (FileField) field;
                    break;
                }
            }

            FileUploadBase fileUpload = null;
            if (fileField != null) {
                fileUpload = fileField.getFileUpload();
                if (fileUpload.getFileItemFactory() == null) {
                    FileItemFactory fif = new DiskFileItemFactory();
                    fileUpload.setFileItemFactory(fif);
                }
            } else {
                String msg = "No FileField defined for POST "
                        + "Content-type 'multipart' request";
                throw new RuntimeException(msg);
            }

            try {
                List itemsList = fileUpload.parseRequest(getContext()
                        .getRequest());

                Map itemsMap = new HashMap(itemsList.size());
                for (int i = 0; i < itemsList.size(); i++) {
                    FileItem fileItem = (FileItem) itemsList.get(i);
                    itemsMap.put(fileItem.getFieldName(), fileItem);
                }

                getContext().setMultiPartFormData(itemsMap);

            } catch (FileUploadException fue) {
                throw new RuntimeException(fue);
            }
        }

        // If a form name is defined, but does not match this form exit.
        String formName = getContext().getRequestParameter(ActionLink.ACTION_LINK);

        if (formName == null || !formName.equals(getId())) {
            return false;
        } else {
            return true;
        }

    }

    public boolean onProcess() {
        if (isFormSubmission()) {
            boolean continueProcessing = true;
            for (int i = 0, size = getFieldList().size(); i < size; i++) {
                Field field = (Field) getFieldList().get(i);
                if (!field.getName().startsWith(SUBMIT_CHECK)) {
                    continueProcessing = field.onProcess();
                    if (!continueProcessing) {
                        return false;
                    }
                }
            }

            for (int i = 0, size = getButtonList().size(); i < size; i++) {
                Button button = (Button) getButtonList().get(i);
                continueProcessing = button.onProcess();
                if (!continueProcessing) {
                    return false;
                }
            }

            if (listener != null && listenerMethod != null) {
                return ClickUtils.invokeListener(listener, listenerMethod);
            }
            return true;
        } else {
            return true;
        }
    }

    protected boolean performSubmitCheck() {
        final HttpServletRequest request = getContext().getRequest();
        final String submitTokenName = SUBMIT_CHECK
                + getContext().getResourcePath();

        boolean isValidSubmit = true;

        // only test if submit for this form
        if (!getContext().isForward() && isFormSubmission()) {
            Long sessionTime = (Long) getContext().getSessionAttribute(
                    submitTokenName);

            if (sessionTime != null) {
                String value = request.getParameter(submitTokenName);
                Long formTime = Long.valueOf(value);
                isValidSubmit = formTime.equals(sessionTime);
            }
        }

        // Save state info to form and session
        final Long time = new Long(System.currentTimeMillis());
        HiddenField field = new HiddenField(submitTokenName, Long.class);
        field.setValueObject(time);
        add(field);

        getContext().setSessionAttribute(submitTokenName, time);

        if (isValidSubmit) {
            return true;

        } else {
            return false;
        }
    }

    public Field getFucusField() {
        return focusField;
    }

    public Field getFocusField() {
        return focusField;
    }

    public Field findFirstField() {
        for (int i = 0, size = getFieldList().size(); i < size; i++) {
            Field field = (Field) getFieldList().get(i);

            if (field instanceof FieldSet) {
                FieldSet fieldSet = (FieldSet) field;

                for (int j = 0; j < fieldSet.getFieldList().size(); j++) {
                    Field fieldSetField = (Field) fieldSet.getFieldList()
                            .get(j);

                    if (!fieldSetField.isHidden()
                            && !fieldSetField.isDisabled()) {

                        return fieldSetField;
                    }
                }

            } else if (!field.isHidden() && !field.isDisabled()) {
                return field;
            }
        }
        return null;
    }

    public void setFocusField(Field focusField) {
        this.focusField = focusField;
    }

    public String renderFormHead() {
        //frist add a state hiddenfield
        StatePage sPage = StatePage.getStatePage(this);
        if(sPage != null) {
            HiddenField sField = sPage.getStateField();
            if(getField(sField.getName())== null) {
                add(sField);
            }
        }
        // form tag
        HtmlStringBuffer buffer = new HtmlStringBuffer(300);

        buffer.elementStart("form");

        buffer.appendAttribute("method", getMethod());
        buffer.appendAttribute("name", getName());
        buffer.appendAttribute("id", getId());
        buffer.appendAttribute("action", getActionURL());
        buffer.appendAttribute("enctype", getEnctype());
        if (hasAttributes()) {
            buffer.appendAttributes(getAttributes());
        }
        if (getJavaScriptValidation()) {
            buffer.appendAttribute("onsubmit", "return onFormSubmit();");
        }
        buffer.closeTag();
        buffer.append("\n");

        // hiddenfields
        for (int i = 0, size = getFieldList().size(); i < size; i++) {
            Field field = (Field) getFieldList().get(i);
            if (field.isHidden()) {
                buffer.append(field);
                buffer.append("\n");
            }
        }
        return buffer.toString();
    }

    public String renderErrorMessages() {
        HtmlStringBuffer buffer = new HtmlStringBuffer();

        final boolean processed = context.getRequest().getMethod()
                .equalsIgnoreCase(getMethod());

        Field fieldWithError = null;
        if (processed && !isValid()) {

            buffer.append("<div algin=\"");
            buffer.append(getErrorsAlign());
            buffer.append("\" class=\"errors\" id=\"");
            buffer.append(getId());
            buffer.append("-errors\" style=\"");
            buffer.append(getErrorsStyle());
            buffer.append("\">\n");

            if (getError() != null) {
                buffer.append("<div class=\"error\">");
                buffer.append("<span class=\"error\">");
                buffer.append(getError());
                buffer.append("</span>");
                buffer.append("</div>\n");
            }

            List errorFieldList = getErrorFields();

            for (int i = 0, size = errorFieldList.size(); i < size; i++) {
                Field field = (Field) errorFieldList.get(i);

                if (getFocusField() == null) {
                    setFocusField(field);
                }

                if (fieldWithError == null && !field.isDisabled()) {
                    fieldWithError = field;
                }

                buffer.append("<div class=\"error\">");
                buffer.append("<span class=\"error\">");

                buffer.append("<a class=\"error\"");
                buffer.append(" href=\"javascript:");
                buffer.append(field.getFocusJavaScript());
                buffer.append("\">");
                buffer.append(field.getError());
                buffer.append("</a>");
                buffer.append("</span>\n");
                buffer.append("</div>\n");
            }
            buffer.append("</div>");

        }

        if (getValidate() && getJavaScriptValidation()) {
            buffer.append("<div style=\"display:none\" id=\"");
            buffer.append(getId());
            buffer.append("-errorsTr\" align=\"");
            buffer.append(getErrorsAlign());
            buffer.append("\">\n");
            buffer.append("<div class=\"errors\" id=\"");
            buffer.append(getId());
            buffer.append("-errorsDiv\"/>\n");
            buffer.append("</div>\n");
        }


        String ret = buffer.toString();
        return ret;

    }

    public String renderButtons() {
        if (!buttonList.isEmpty()) {
            HtmlStringBuffer buffer = new HtmlStringBuffer(
                    buttonList.size() * 50);
            buffer.append("<div class=\"buttons\" id=\"");
            buffer.append(getId());
            buffer.append("-buttons\">\n");
            for (int i = 0, size = buttonList.size(); i < size; i++) {
                buffer.append("<span class=\"buttons\"");
                buffer.appendAttribute("style", getButtonStyle());
                buffer.closeTag();

                Button button = (Button) buttonList.get(i);
                buffer.append(button);

                buffer.append("</span>\n");
            }
            buffer.append("</div>\n");
            String ret = buffer.toString();
            return ret;
        }
        return "";
    }

    public String renderFormEnd() {
        HtmlStringBuffer buffer = new HtmlStringBuffer();

        buffer.elementEnd("form");
        buffer.append("\n");

        // Set field focus
        Field focusField = getFocusField();
        if (focusField == null) {
            focusField = this.findFirstField();
        }
        if (focusField != null) {
            buffer.append("<script type=\"text/javascript\"><!--\n");
            buffer.append("var field = document.getElementById('");
            buffer.append(focusField.getId());
            buffer.append("');\n");
            buffer.append("if (field && field.focus) { field.focus(); };\n");
            buffer.append("//--></script>\n");

        }

        renderValidationJavaScript(buffer);
        String ret = buffer.toString();
        return ret;

    }

    public String toString() {
        StatePage sPage = StatePage.getStatePage(this);
        if(sPage != null) {
            HiddenField sField = sPage.getStateField();
            if(getField(sField.getName())== null) {
                add(sField);
            }
        }
        return super.toString();
    }
}
