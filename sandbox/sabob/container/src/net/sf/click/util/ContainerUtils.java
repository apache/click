/*
 * Copyright 2004-2008 Malcolm A. Edgar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.sf.click.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.sf.click.Container;
import net.sf.click.Control;
import net.sf.click.Page;
import net.sf.click.control.AbstractControl;
import net.sf.click.control.Field;
import net.sf.click.control.Label;

/**
 *
 * @author Bob Schellink
 */
public class ContainerUtils {
    
    /**
     * Returns a list of fields in this container, as well as recursively
     * scanning for fields in any child containers
     */
    public static List getFieldList(final Container container) {
        if (container == null) {
            throw new IllegalArgumentException("Null container parameter");
        }
        
        final List fieldList = new ArrayList();
        final ControlCallback callback = new ControlCallback() {
            public void process(Control control) {
                if(control instanceof Field && !(control instanceof Label)) {
                    fieldList.add(control);
                }
            }
        };
        getControls(container, callback);
        return fieldList;
    }
    
     public static Map getFields(final Container container) {
        if (container == null) {
            throw new IllegalArgumentException("Null container parameter");
        }

        final Map fields = new HashMap();
        final ControlCallback callback = new ControlCallback() {
            public void process(Control control) {
                if(control instanceof Field && !(control instanceof Label)) {
                    fields.put(control.getName(), control);
                }
            }
        };
        getControls(container, callback);
        return fields;
    }
    
    public static void getControls(Container container, ControlCallback callback) {
        for (int i = 0; i < container.getControls().size(); i++) {
            Control control = (Control) container.getControls().get(i);
            callback.process(control);

            if (control instanceof Container) {
                Container childContainer = (Container) control;
                getControls(childContainer, callback);
            }
        }
    }
    
    public static List getErrorFields(final Container container) {
        if (container == null) {
            throw new IllegalArgumentException("Null container parameter");
        }

        final List fieldList = new ArrayList();
        final ControlCallback canAddCheck = new ControlCallback() {
            public void process(Control control) {
                if(control instanceof Field) {
                    Field field = (Field) control;
                    if (!field.isValid()
                    && !field.isHidden()
                    && !field.isDisabled()) {
                        fieldList.add(control);
                    }
                }
            }
        };
        getControls(container, canAddCheck);
        return fieldList;
    }
    
    public static Control getControl(Container container, String name) {
        Control control = (Control) container.getControl(name);
        
        if (control != null) {
            return control;
            
        } else {
            for (int i = 0; i < container.getControls().size(); i++) {
                Control childControl = (Control) container.getControls().get(i);
                
                if (childControl instanceof Container) {
                    Container childContainer = (Container) childControl;
                    return getControl(childContainer, name);
                }
            }
        }
        return null;
    }

    public static void copyObjectToContainer(Object object, Container container) {
        
        if (object == null) {
            throw new IllegalArgumentException("Null object parameter");
        }
        if (container == null) {
            throw new IllegalArgumentException("Null container parameter");
        }
        
        final List fieldList = getFieldList(container);
        
        if (fieldList.isEmpty()) {
            log("Container has no fields to copy to");
        }
        
        String objectClassname = object.getClass().getName();
        objectClassname =
                objectClassname.substring(objectClassname.lastIndexOf(".") + 1);
        
        Set properties = getObjectPropertyNames(object);
        
        for (int i = 0, size = fieldList.size(); i < size; i++) {
            Field field = (Field) fieldList.get(i);
            
            if (!hasMatchingProperty(field, properties)) {
                continue;
            }
            
            try {
                Object result = PropertyUtils.getValue(object, field.getName());
                
                field.setValueObject(result);
                
                String msg = "Container <- " + objectClassname + "."
                        + field.getName() + " : " + result;
                log(msg);
                
            } catch (Exception e) {
                String msg = "Error incurred invoking " + objectClassname + "."
                        + field.getName() + " error: " + e.toString();
                
                log(msg);
            }
        }
    }
    
    public static void copyContainerToObject(Container container, Object object) {
        
        if (container == null) {
            throw new IllegalArgumentException("Null container parameter");
        }
        if (object == null) {
            throw new IllegalArgumentException("Null object parameter");
        }
        
        String objectClassname = object.getClass().getName();
        objectClassname =
                objectClassname.substring(objectClassname.lastIndexOf(".") + 1);
        
        final List fieldList = getFieldList(container);
        
        if (fieldList.isEmpty()) {
            log("Container has no fields to copy from");
        }
        
        Set properties = getObjectPropertyNames(object);
        Map ognlContext = new HashMap();
        
        for (int i = 0, size = fieldList.size(); i < size; i++) {
            Field field = (Field) fieldList.get(i);
            
            if (!hasMatchingProperty(field, properties)) {
                continue;
            }
            
            ensureObjectPathNotNull(object, field.getName());
            
            try {
                PropertyUtils.setValueOgnl(object, field.getName(), field.getValueObject(), ognlContext);
                
                String msg = "Form -> " + objectClassname + "."
                        + field.getName() + " : " + field.getValueObject();
                
                log(msg);
                
            } catch (Exception e) {
                String msg =
                        "Error incurred invoking " + objectClassname + "."
                        + field.getName() + " with " + field.getValueObject()
                        + " error: " + e.toString();
                
                log(msg);
            }
        }
    }
    
    private static Set getObjectPropertyNames(Object object) {
        if (object instanceof Map) {
            return ((Map) object).keySet();
        }
        
        HashSet hashSet = new HashSet();
        
        Method[] methods = object.getClass().getMethods();
        
        for (int i = 0; i < methods.length; i++) {
            String methodName = methods[i].getName();
            
            if (methodName.startsWith("get") && methodName.length() > 3) {
                String propertyName =
                        "" + Character.toLowerCase(methodName.charAt(3))
                        + methodName.substring(4);
                hashSet.add(propertyName);
            }
            if (methodName.startsWith("is") && methodName.length() > 2) {
                String propertyName =
                        "" + Character.toLowerCase(methodName.charAt(2))
                        + methodName.substring(3);
                hashSet.add(propertyName);
            }
            if (methodName.startsWith("set") && methodName.length() > 3) {
                String propertyName =
                        "" + Character.toLowerCase(methodName.charAt(3))
                        + methodName.substring(4);
                hashSet.add(propertyName);
            }
        }
        
        return hashSet;
    }
    
    private static boolean hasMatchingProperty(Field field, Set properties) {
        String fieldName = field.getName();
        if (fieldName.indexOf(".") != -1) {
            fieldName = fieldName.substring(0, fieldName.indexOf("."));
        }
        return properties.contains(fieldName);
    }
    
    private static void ensureObjectPathNotNull(Object object, String path) {
        
        final int index = path.indexOf('.');
        
        if (index == -1) {
            return;
        }
        
        try {
            String value = path.substring(0, index);
            String getterName = ClickUtils.toGetterName(value);
            String isGetterName = ClickUtils.toIsGetterName(value);
            
            Method foundMethod = null;
            Method[] methods = object.getClass().getMethods();
            for (int i = 0; i < methods.length; i++) {
                String name = methods[i].getName();
                if (name.equals(getterName)) {
                    foundMethod = methods[i];
                    break;
                    
                } else if (name.equals(isGetterName)) {
                    foundMethod = methods[i];
                    break;
                }
            }
            
            if (foundMethod == null) {
                String msg =
                        "Getter method not found for path value : " + value;
                throw new RuntimeException(msg);
            }
            
            Object result = foundMethod.invoke(object, null);
            
            if (result == null) {
                result = foundMethod.getReturnType().newInstance();
                
                String setterName = ClickUtils.toSetterName(value);
                Class[] classArgs = { foundMethod.getReturnType() };
                
                Method setterMethod =
                        object.getClass().getMethod(setterName, classArgs);
                
                Object[] objectArgs = { result };
                
                setterMethod.invoke(object, objectArgs);
            }
            
            String remainingPath = path.substring(index + 1);
            
            ensureObjectPathNotNull(result, remainingPath);
            
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    private static void log(String msg) {
        if(ClickLogger.getInstance().isDebugEnabled()) {
            System.out.println("[Click] [debug] " + msg);
        } else {
            ClickLogger.getInstance().debug(msg);
        }
    }

    public static interface ControlCallback {
        public void process(Control field);
    }

    /**
     * TODO implement fast getId method. Use StringBuffer and depth to get
     * close to the actual string size, and iterate over the control tree to
     * append ids.
     * TODO Need to expose hasAttributes, getAttribute on Control interface
     */
    /*
    public static String getId(Control control) {
        int depth = ControlUtils.getDepth(control);
        HtmlStringBuffer buffer = new HtmlStringBuffer(depth * 7);
        //TODO
        return "";
    }*/
    
    public static String getAbsoluteId(Control control) {
        if(control.getName() == null) {
            //If id of control is null, do NOT continue up the stack parents,
            //and return null
            return null;
        }
        int depth = ControlUtils.getDepth(control);
        HtmlStringBuffer buffer = new HtmlStringBuffer(depth * 7);
        _appendAbsoluteId(control, buffer);
        return buffer.toString();
    }
    
    public static void appendAbsoluteId(Control control, HtmlStringBuffer buffer) {
        if(control.getName() == null) {
            //If id of control is null, exit early
            return;
        }
        buffer.append(" id=\"");
        _appendAbsoluteId(control, buffer);
        buffer.append("\"");
    }

    private static void _appendAbsoluteId(Control control, HtmlStringBuffer buffer) {
        Object parent = control.getParent();
        boolean hasParent = parent instanceof Control && !(parent instanceof Page);
        if(hasParent) {
            _appendAbsoluteId((Control) parent, buffer);
        }
        
        String id = null;

        //TODO Control interface needs methods hasAttributes / getAttributes
        // Workaround by casting to AbstractControl
        AbstractControl acontrol = (AbstractControl) control;
        if (acontrol.hasAttributes() && acontrol.getAttributes().containsKey("id")) {
            id = acontrol.getAttribute("id");
        } else {
            id = control.getName();
        }
        
        if(id != null) {
            //In case the parent id's was blank ensure a '_' is not prepended
            //if(buffer.length() > 0) {
            if(hasParent) {
                buffer.append("_"); 
            }
            buffer.append(id);
        }
    }

    public static String getAllHtmlImports(Container container) {
        if (container == null) {
            throw new IllegalArgumentException("Container cannot be null");
        }
        Set htmlIncludeSet = new HashSet();
        addAllHtmlImports(container, htmlIncludeSet);

        HtmlStringBuffer buffer = new HtmlStringBuffer(htmlIncludeSet.size() * 20);

        for (Iterator it = htmlIncludeSet.iterator(); it.hasNext();) {
            String htmlInclude = (String) it.next();
            buffer.append(htmlInclude);
        }

        return buffer.toString();
    }

    private static void addAllHtmlImports(Container container, Set includeSet) {
        List list = container.getControls();

        String thisInclude = container.getHtmlImports();
        if (thisInclude != null) {
            includeSet.add(thisInclude);
        }

        for (int i = 0, size = list.size(); i < size; i++) {
            Control control = (Control) list.get(i);
            if (control instanceof Container) {
                addAllHtmlImports((Container) control, includeSet);
            } else {
                String include = control.getHtmlImports();
                if (include != null) {
                    includeSet.add(include);
                }
            }
        }
    }
}
