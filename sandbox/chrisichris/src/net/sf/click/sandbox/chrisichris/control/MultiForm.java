package net.sf.click.sandbox.chrisichris.control;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;

import org.apache.commons.lang.StringUtils;

import ognl.Ognl;
import ognl.OgnlException;


import net.sf.click.Context;
import net.sf.click.control.Button;
import net.sf.click.control.Column;
import net.sf.click.control.Field;
import net.sf.click.control.FieldSet;
import net.sf.click.control.Form;
import net.sf.click.control.HiddenField;
import net.sf.click.control.Table;
import net.sf.click.util.ClickLogger;
import net.sf.click.util.ClickUtils;
import net.sf.click.util.HtmlStringBuffer;

abstract public class MultiForm extends BaseForm {

    /**
     * 
     */
    private static final long serialVersionUID = -3903347508860998256L;
    
    public static final String ID_HIDDEN_FIELD_NAME = "_idHiddenField";
    protected ClickLogger log = ClickLogger.getInstance();
    
    protected String[] fieldNames;
    protected Object[] bindExpressions;
    protected Object idExpression;
    protected final List rowIds = new ArrayList();
    

    public MultiForm(String name, String[] fieldNames) {
        super(name);
        if(fieldNames == null) {
            throw new NullPointerException("No fieldNames");
        }
        this.fieldNames = fieldNames;
    }

    public MultiForm(String name, String[] fieldNames, String idExpression) {
        this(name,fieldNames);
        setIdExpression(idExpression);
    }
    
    abstract protected void createFieldsImpl(String id);
    
    public String[] getFieldNames(){
        return fieldNames;
    }
    
    public void setFieldNames(String[] names) {
        this.fieldNames = names;
    }
    
    
    
    public void setIdExpression(String expression) {
        if(expression == null) {
            this.idExpression = null;
        } else {
            try {
                idExpression = Ognl.parseExpression(expression);
            } catch (OgnlException e) {
                RuntimeException ex = new IllegalArgumentException("The expression ["+expression+"] is not valid OGNL expression");
                ex.initCause(e);
                throw ex;
            }
        }
    }

    
    public boolean onProcess() {
        if(isFormSubmission()) {
            //read the ids
            final List ids = getSubmittedIds();
            //create the fields for processing
            for(int i=0;i<ids.size();i++) {
                final String id = (String)ids.get(i);
                createFields(id);
            }
        }
        return super.onProcess();
    }
    
    
    public void createFields(String id) {
        if(StringUtils.isBlank(id)) {
            throw new NullPointerException("You must provide an id");
        }
        if(rowIds.contains(id)){
            throw new IllegalArgumentException("The id ["+id+"] exists already");
        }
        createFieldsImpl(id);
        rowIds.add(id);
    }
    
    public void removeFields(String id) {
        if(StringUtils.isBlank(id)) {
            throw new NullPointerException("You must provide an id");
        }
        if(!rowIds.contains(id)){
            throw new IllegalArgumentException("The id ["+id+"] does not exist");
        }
        removeFieldsImpl(id);
        rowIds.remove(id);
    }
    
    protected void removeFieldsImpl(String id) {
        Field[] fields = getFieldsRowArray(id);
        for (int i = fields.length-1; i >= 0; i--) {
            Field field = fields[i];
            this.remove(field);
        }
    }

    public Field getField(String name,String id) {
        return getField(getFieldName(name,id));
    }
    
    public Field[] getFieldsColumn(String name) {
        Field[] ret = new Field[getRowIds().size()];
        int i=0;
        for (Iterator it = getRowIds().iterator(); it.hasNext();) {
            String id = (String) it.next();
            Field fl = getField(name,id);
            ret[i] = fl;
            i++;
        }
        return ret;
    }
    
    public Field[] getFieldsRowArray(String id) {
        if(rowIds.contains(id)) {
            String[] fNames = getFieldNames();
            Field[] ret = new Field[fNames.length];
            for(int i=0;i<fNames.length;i++) {
                String fName = fNames[i];
                Field field = getField(fName,id);
                if(field == null) {
                    throw new IllegalStateException("No field with name ["+fName+"] for id ["+id+"]");
                }
                ret[i] = field;
            }
            return ret;
        }
        return null;
    }
    
    public Map getFieldsRow(Object idO) {
        String id = idO.toString();
        Field[] fields = getFieldsRowArray(id);
        if(fields == null) {
            return null;
        }
        
        String[] fNames = getFieldNames();
        Map ret = new HashMap();
        for (int i = 0; i < fNames.length; i++) {
            ret.put(fNames[i],fields[i]);
        }
        return ret;
    }
    
    public String getFieldName(String name,String id) {
        if(name == null) {
            return id;
        }
        return name+"-"+id;
    }
    
    public String[] getBindingExpressions() {
        return getFieldNames();
    }
    
    public Object[] getCompiledBindingExpressions() {
        if(this.bindExpressions == null) {
            String[] fNames = getBindingExpressions();
            Object[] exprs = new Object[fNames.length];
            for (int i = 0; i < fNames.length; i++) {
                if(fNames[i] != null && !fNames[i].startsWith("nobind-")) {
                    try {
                        Object expr = Ognl.parseExpression(fNames[i]);
                        exprs[i] = expr;
                    } catch (OgnlException e) {
                        if(log.isDebugEnabled()) {
                            log.debug("MultiForm.compileExpressions: No valid expression name: "+fNames[i]);
                        }
                        exprs[i] = null;
                    }
                } else {
                    exprs[i] = null;
                }
            }
            bindExpressions = exprs;
        }
        return bindExpressions;
    }
    
    public void copyRowTo(String rowid,Object ob) {
        Field[] fields = getFieldsRowArray(rowid);
        if(fields == null) {
             throw new IllegalArgumentException("No fields for id: "+rowid);
        }
        
        Object[] bExprs = getCompiledBindingExpressions();
        for (int i = 0; i < bExprs.length; i++) {
            Object expr = bExprs[i];
            if(expr != null) {
                try {
                    Ognl.setValue(expr,ob,fields[i].getValueObject());
                } catch (OgnlException e) {
                    RuntimeException ex = new IllegalArgumentException("Could not process expression:" + getBindingExpressions()[i]);
                    ex.initCause(e);
                    throw ex;
                }
            }
        }
    }
    
    public void copyRowTo(Object ob) {
        copyRowTo(getRowId(ob),ob);
    }
    
    public void copyRowsTo(List rowData) {
        for (Iterator it = rowData.iterator(); it.hasNext();) {
            Object data = (Object) it.next();
            copyRowTo(data);
        }
    }
    
    public void copyRowFrom(Object ob) {
        final String rowid = getRowId(ob);
        Field[] fields = getFieldsRowArray(rowid);
        if(fields == null) {
            createFields(rowid);
            fields = getFieldsRowArray(rowid);
            if(fields == null) {
                throw new NullPointerException("No fields where created for id: "+rowid);
            }
        }
        Object[] bExprs = getCompiledBindingExpressions();
        for (int i = 0; i < bExprs.length; i++) {
            Object expr = bExprs[i];
            if(expr != null) {
                try {
                    Object val = Ognl.getValue(expr,ob);
                    fields[i].setValueObject(val);
                } catch (OgnlException e) {
                    RuntimeException ex = new IllegalArgumentException("Could not process expression:" + getBindingExpressions()[i]);
                    ex.initCause(e);
                    throw ex;
                }
            }
        }
    }
    
    public void copyRowsFrom(List rowData,boolean overwrite,boolean delete) {
        Object[] bExprs = getCompiledBindingExpressions();
        List dataIds = new ArrayList(rowData.size());
        for (Iterator it = rowData.iterator(); it.hasNext();) {
            Object ob = (Object) it.next();
            String rowid = getRowId(ob);
            dataIds.add(rowid);
            boolean newRow = false;
            
            Field[] fields = getFieldsRowArray(rowid);
            if(fields == null) {
                newRow = true;
                createFields(rowid);
                fields = getFieldsRowArray(rowid);
                if(fields == null) {
                    throw new NullPointerException("No fields where created for id: "+rowid);
                }
            }
            if(newRow || overwrite) {
                for (int i = 0; i < bExprs.length; i++) {
                    Object expr = bExprs[i];
                    if(expr != null) {
                        try {
                            Object val = Ognl.getValue(expr,ob);
                            fields[i].setValueObject(val);
                        } catch (OgnlException e) {
                            RuntimeException ex = new IllegalArgumentException("Could not process expression:" + getBindingExpressions()[i]);
                            ex.initCause(e);
                            throw ex;
                        }
                    }
                }
            }
        }
        
        //if delete
        if(delete) {
            List toRemove = new ArrayList(this.getRowIds());
            toRemove.removeAll(dataIds);
            for (Iterator it = toRemove.iterator(); it.hasNext();) {
                String id = (String) it.next();
                removeFields(id);
            }
        }
    }
    
    public String getRowId(Object modelObject) {
        if(idExpression != null) {
            String id;
            try {
                id = (String) Ognl.getValue(idExpression,modelObject,String.class);
                if(id == null) {
                    throw new IllegalStateException("Could not get Id for modelObject:"+modelObject);
                }
                return id;
            } catch (OgnlException e) {
                RuntimeException ex = new IllegalStateException("Error evaluating expression");
                ex.initCause(e);
                throw ex;
            }
        } 
        throw new IllegalStateException("Please set an id expression");
    }
    
    public List getSubmittedIds() {
        String fN = getFieldNames()[0];
        fN = fN+"-";
        Map paraMa = getContext().getRequest().getParameterMap();
        List ids = new ArrayList();
        for (Iterator it = paraMa.keySet().iterator(); it.hasNext();) {
            String key = (String) it.next();
            if(key.startsWith(fN)) {
                String id = key.substring(fN.length());
                ids.add(id);
            }
        }
        return ids;
    }

    public List getRowIds() {
        return Collections.unmodifiableList(rowIds);
    }
    
    
}
