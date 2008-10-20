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

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.sf.click.control.Container;
import net.sf.click.Control;
import net.sf.click.Page;
import net.sf.click.control.Button;
import net.sf.click.control.Field;
import net.sf.click.control.FieldSet;
import net.sf.click.control.Form;
import net.sf.click.control.Label;
import net.sf.click.service.LogService;
import org.apache.commons.lang.ClassUtils;

/**
 * Provides Container access and copy utilities.
 *
 * @author Bob Schellink
 */
public class ContainerUtils {

    /**
     * Return the list of Buttons for the given Container, recursively including
     * any Fields contained in child containers.
     *
     * @param container the container to obtain the buttons from
     * @return the list of contained buttons
     */
    public static List getButtons(final Container container) {
        if (container == null) {
            throw new IllegalArgumentException("Null container parameter");
        }

        final List buttons = new ArrayList();
        addButtons(container, buttons);
        return buttons;
    }

    /**
     * Return the list of Fields for the given Container, recursively including
     * any Fields contained in child containers.
     *
     * @param container the container to obtain the fields from
     * @return the list of contained fields
     */
    public static List getFields(final Container container) {
        if (container == null) {
            throw new IllegalArgumentException("Null container parameter");
        }

        final List fields = new ArrayList();
        addFields(container, fields);
        return fields;
    }

    /**
     * Return the list of input Fields (TextField, Select, Radio, Checkbox etc.)
     * for the given Container, recursively including any Fields contained in
     * child containers. The list of returned fields will exclude any
     * <tt>Button</tt>, <tt>FieldSet</tt> and <tt>Label</tt> fields.
     *
     * @param container the container to obtain the fields from
     * @return the list of contained fields
     */
    public static List getInputFields(final Container container) {
        if (container == null) {
            throw new IllegalArgumentException("Null container parameter");
        }

        final List fields = new ArrayList();
        addInputFields(container, fields);
        return fields;
    }

    /**
     * Return the list of Fields for the given Container, recursively including
     * any Fields contained in child containers. The list of returned fields
     * will exclude any <tt>Button</tt> and <tt>FieldSet</tt> fields.
     *
     * @param container the container to obtain the fields from
     * @return the list of contained fields
     */
    public static List getFieldsAndLabels(final Container container) {
        if (container == null) {
            throw new IllegalArgumentException("Null container parameter");
        }

        final List fields = new ArrayList();
        addFieldsAndLabels(container, fields);
        return fields;
    }

    /**
     * Return a map of all Fields for the given Container, recursively including
     * any Fields contained in child containers.
     * <p/>
     * The map's key / value pair will consist of the control name and instance.
     *
     * @param container the container to obtain the fields from
     * @return the map of contained fields
     */
    public static Map getFieldMap(final Container container) {
        if (container == null) {
            throw new IllegalArgumentException("Null container parameter");
        }

        final Map fields = new HashMap();
        addFields(container, fields);
        return fields;
    }

    /**
     * Return the list of hidden Fields for the given Container, recursively including
     * any Fields contained in child containers. The list of returned fields
     * will exclude any <tt>Button</tt>, <tt>FieldSet</tt> and <tt>Label</tt>
     * fields.
     *
     * @param container the container to obtain the fields from
     * @return the list of contained fields
     */
    public static List getHiddenFields(final Container container) {
        if (container == null) {
            throw new IllegalArgumentException("Null container parameter");
        }

        final List fields = new ArrayList();
        addHiddenFields(container, fields);
        return fields;
    }

    /**
     * Return a list of container fields which are not valid, not hidden and not
     * disabled.
     * <p/>
     * The list of returned fields will exclude any <tt>Button</tt> fields.
     *
     * @param container the container to obtain the invalid fields from
     * @return list of container fields which are not valid, not hidden and not
     * disabled
     */
    public static List getErrorFields(final Container container) {
        if (container == null) {
            throw new IllegalArgumentException("Null container parameter");
        }

        final List fields = new ArrayList();
        addErrorFields(container, fields);
        return fields;
    }

    /**
     * Find and return the specified controls parent Form or null
     * if no Form is present.
     *
     * @param control the control to check for Form
     * @return the controls parent Form or null if no parent is a Form
     */
    public static Form findForm(Control control) {
        while (control.getParent() != null && !(control.getParent() instanceof Page)) {
            control = (Control) control.getParent();
            if (control instanceof Form) {
                return (Form) control;
            }
        }
        return null;
    }

    /**
     * Find and return the first control with a matching name in the specified
     * container.
     *
     * @param container the container that is checked for controls for matching
     * names
     * @param name the name of the control to find
     * @return the control with a matching name
     */
    public static Control findControlByName(Container container, String name) {
        Control control = (Control) container.getControl(name);

        if (control != null) {
            return control;

        } else {
            for (int i = 0; i < container.getControls().size(); i++) {
                Control childControl = (Control) container.getControls().get(i);

                if (childControl instanceof Container) {
                    Container childContainer = (Container) childControl;
                    Control found = findControlByName(childContainer, name);
                    if (found != null) {
                        return found;
                    }
                }

            }
        }
        return null;
    }

    /**
     * Populate the given object attributes from the Containers field values.
     * <p/>
     * If a Field and object attribute matches, the object attribute is set to
     * the Object returned from the method
     * {@link net.sf.click.control.Field#getValueObject()}. If an object
     * attribute is a primitive, the Object returned from
     * {@link net.sf.click.control.Field#getValueObject()} will be converted
     * into the specific primitive e.g. Integer will become int and Boolean will
     * become boolean.
     * <p/>
     * The fieldList specifies which fields to copy to the object. This allows
     * one to include or exclude certain Container fields before populating the
     * object.
     * <p/>
     * The following example shows how to exclude disabled fields from
     * populating a customer object:
     * <pre class="prettyprint">
     * public void onInit() {
     *     List formFields = new ArrayList();
     *     for(Iterator it = form.getFieldList().iterator(); it.hasNext(); ) {
     *         Field field = (Field) formFields.next();
     *         // Exclude disabled fields
     *         if (!field.isDisabled()) {
     *             formFields.add(field);
     *         }
     *     }
     *     Customer customer = new Customer();
     *     ContainerUtils.copyContainerToObject(form, customer, formFields);
     * }
     * </pre>
     *
     * The specified Object can either be a POJO (plain old java object) or
     * a {@link java.util.Map}. If a POJO is specified, its attributes are
     * populated from  matching container fields. If a map is specified, its
     * key/value pairs are populated from matching container fields.
     *
     * @param container the fieldList Container
     * @param object the object to populate with field values
     * @param fieldList the list of fields to obtain values from
     *
     * @throws IllegalArgumentException if container, object or fieldList is
     * null
     */
    public static void copyContainerToObject(Container container,
        Object object, List fieldList) {

        if (container == null) {
            throw new IllegalArgumentException("Null container parameter");
        }

        if (object == null) {
            throw new IllegalArgumentException("Null object parameter");
        }

        if (fieldList == null) {
            throw new IllegalArgumentException("Null fieldList parameter");
        }

         if (fieldList.isEmpty()) {
            LogService logService = ClickUtils.getLogService();
            if (logService.isDebugEnabled()) {
                String containerClassName =
                    ClassUtils.getShortClassName(container.getClass());
                logService.debug("   " + containerClassName
                    + " has no fields to copy from");
            }
            //Exit early.
            return;
        }

        String objectClassname = object.getClass().getName();
        objectClassname =
            objectClassname.substring(objectClassname.lastIndexOf(".") + 1);

        // If the given object is a map, its key/value pair is populated from
        // the fields name/value pair.
        if (object instanceof Map) {
            copyFieldsToMap(fieldList, (Map) object);
            // Exit after populating the map.
            return;
        }

        LogService logService = ClickUtils.getLogService();

        Set properties = getObjectPropertyNames(object);
        Map ognlContext = new HashMap();

        for (int i = 0,  size = fieldList.size(); i < size; i++) {
            Field field = (Field) fieldList.get(i);

            if (!hasMatchingProperty(field, properties)) {
                continue;
            }

            String fieldName = field.getName();

            ensureObjectPathNotNull(object, fieldName);

            try {
                PropertyUtils.setValueOgnl(object, fieldName, field.getValueObject(), ognlContext);

                if (logService.isDebugEnabled()) {
                    String containerClassName =
                        ClassUtils.getShortClassName(container.getClass());
                    String msg = "    " + containerClassName + " -> "
                        + objectClassname + "." + fieldName + " : "
                        + field.getValueObject();

                    logService.debug(msg);
                }

            } catch (Exception e) {
                String msg =
                    "Error incurred invoking " + objectClassname + "."
                    + fieldName + " with " + field.getValueObject()
                    + " error: " + e.toString();

                logService.debug(msg);
            }
        }
    }

    /**
     * Populate the given object attributes from the Containers field values.
     *
     * @see #copyContainerToObject(net.sf.click.control.Container, java.lang.Object, java.util.List)
     *
     * @param container the Container to obtain field values from
     * @param object the object to populate with field values
     */
    public static void copyContainerToObject(Container container,
        Object object) {
        final List fieldList = getInputFields(container);
        copyContainerToObject(container, object, fieldList);
    }

    /**
     * Populate the given Container field values from the object attributes.
     * <p/>
     * If a Field and object attribute matches, the Field value is set to the
     * object attribute using the method
     * {@link net.sf.click.control.Field#setValueObject(java.lang.Object)}. If
     * an object attribute is a primitive it is first converted to its proper
     * wrapper class e.g. int will become Integer and boolean will become
     * Boolean.
     * <p/>
     * The fieldList specifies which fields to populate from the object. This
     * allows one to exclude or include specific fields.
     * <p/>
     * The specified Object can either be a POJO (plain old java object) or
     * a {@link java.util.Map}. If a POJO is specified, its attributes are
     * copied to matching container fields. If a map is specified, its key/value
     * pairs are copied to matching container fields.
     *
     * @param object the object to obtain attribute values from
     * @param container the Container to populate
     * @param fieldList the list of fields to populate from the object
     * attributes
     */
    public static void copyObjectToContainer(Object object,
        Container container, List fieldList) {
        if (object == null) {
            throw new IllegalArgumentException("Null object parameter");
        }

        if (container == null) {
            throw new IllegalArgumentException("Null container parameter");
        }

        if (container == null) {
            throw new IllegalArgumentException("Null fieldList parameter");
        }

        if (fieldList.isEmpty()) {
            LogService logService = ClickUtils.getLogService();
            if (logService.isDebugEnabled()) {
                String containerClassName =
                    ClassUtils.getShortClassName(container.getClass());
                logService.debug("   " + containerClassName
                    + " has no fields to copy to");
            }
            //Exit early.
            return;
        }

        String objectClassname = object.getClass().getName();
        objectClassname =
            objectClassname.substring(objectClassname.lastIndexOf(".") + 1);

        //If the given object is a map, populate the fields name/value from
        //the maps key/value pair.
        if (object instanceof Map) {

            copyMapToFields((Map) object, fieldList);
            //Exit after populating the fields.
            return;
        }

        Set properties = getObjectPropertyNames(object);

        LogService logService = ClickUtils.getLogService();

        for (int i = 0,  size = fieldList.size(); i < size; i++) {
            Field field = (Field) fieldList.get(i);

            if (!hasMatchingProperty(field, properties)) {
                continue;
            }

            String fieldName = field.getName();
            try {
                Object result = PropertyUtils.getValue(object, fieldName);

                field.setValueObject(result);

                if (logService.isDebugEnabled()) {
                    String containerClassName =
                        ClassUtils.getShortClassName(container.getClass());
                    String msg = "    " + containerClassName + " <- "
                        + objectClassname + "." + fieldName + " : "
                        + result;
                    logService.debug(msg);
                }

            } catch (Exception e) {
                String msg = "Error incurred invoking " + objectClassname + "."
                    + fieldName + " error: " + e.toString();

                logService.debug(msg);
            }
        }
    }

    /**
     * Populate the given Container field values from the object attributes.
     *
     * @see #copyObjectToContainer(java.lang.Object, net.sf.click.control.Container, java.util.List)
     *
     * @param object the object to obtain attribute values from
     * @param container the Container to populate
     */
    public static void copyObjectToContainer(Object object,
        Container container) {

        final List fieldList = getInputFields(container);
        copyObjectToContainer(object, container, fieldList);
    }

    /**
     * Return how deep the control is in the container hierarchy.
     *
     * @param control the control which depth to return
     * @return the depth of the control in the container hierarchy
     */
    protected int getDepth(Control control) {
        int depth = 1;

        while (control.getParent() != null && !(control.getParent() instanceof Page)) {
            control = (Control) control.getParent();
            depth++;
        }
        return depth;
    }

    // -------------------------------------------------------- Private Methods

    /**
     * Extract and return the specified object property names.
     * <p/>
     * If the object is a Map instance, this method returns the maps key set.
     *
     * @param object the object to extract property names from
     * @return the unique set of property names
     */
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

    /**
     * Return true if the specified field name is contained within the
     * specified set of properties.
     *
     * @param field the field which name should be checked
     * @param properties set of properties to check
     * @return true if the specified field name is contained in the properties,
     * false otherwise
     */
    private static boolean hasMatchingProperty(Field field, Set properties) {
        String fieldName = field.getName();
        if (fieldName.indexOf(".") != -1) {
            fieldName = fieldName.substring(0, fieldName.indexOf("."));
        }
        return properties.contains(fieldName);
    }

    /**
     * This method ensures that the object can safely be navigated according
     * to the specified path.
     * <p/>
     * If any object in the graph is null, a new instance of that object class
     * is instantiated.
     *
     * @param object the object which path must be navigatable without
     * encountering null values
     * @param path the navigation path
     */
    private static void ensureObjectPathNotNull(Object object, String path) {

        final int index = path.indexOf('.');

        if (index == -1) {
            return;
        }

        String property = path.substring(0, index);
        Method getterMethod = findGetter(object, property, path);
        Object result = invokeGetter(getterMethod, object, property, path);

        if (result == null) {
            // Find the target class of the object in the path to create
            Class targetClass = getterMethod.getReturnType();

            Constructor constructor = null;
            try {
                // Lookup default no-arg constructor
                constructor = targetClass.getConstructor((Class[]) null);

            } catch (NoSuchMethodException e) {
                // Log detailed error message of looking up constructor failed
                HtmlStringBuffer buffer = new HtmlStringBuffer();
                logBasicDescription(buffer, object, path, property);
                buffer.append("Attempt to construct instance of class '");
                buffer.append(targetClass.getName()).append(" resulted in error: '");
                buffer.append(targetClass.getName()).append("' does not seem");
                buffer.append(" to have a default no argument constrcutor.");
                buffer.append(" Please note another common problem is that the");
                buffer.append(" class is either not public or not static.");
                throw new RuntimeException(buffer.toString(), e);
            }

            try {
                // Create target object instance
                result = constructor.newInstance(new Object[]{});

            } catch (Exception e) {
                // Log detailed error message of why creating target failed
                HtmlStringBuffer buffer = new HtmlStringBuffer();
                logBasicDescription(buffer, object, path, property);
                buffer.append("Result: could not create");
                buffer.append(" object with constructor '");
                buffer.append(constructor.getName()).append("'.");
                throw new RuntimeException(buffer.toString(), e);
            }

            Method setterMethod = findSetter(object, property, targetClass, path);
            invokeSetter(setterMethod, object, result, property, path);
        }

        String remainingPath = path.substring(index + 1);

        ensureObjectPathNotNull(result, remainingPath);
    }

    /**
     * Find the object getter method for the given property.
     * <p/>
     * If this method cannot find a 'get' property it tries to lookup an 'is'
     * property.
     *
     * @param object the object to find the getter method on
     * @param property the getter property name specifying the getter to lookup
     * @param path the full expression path (used for logging purposes)
     * @return the getter method
     */
    private final static Method findGetter(Object object, String property,
        String path) {

        // Find the getter for property
        String getterName = ClickUtils.toGetterName(property);

        Method method = null;
        Class sourceClass = object.getClass();

        try {
            method = sourceClass.getMethod(getterName, null);
        } catch (Exception e) {
        }

        if (method == null) {
            String isGetterName = ClickUtils.toIsGetterName(property);
            try {
                method = sourceClass.getMethod(isGetterName, null);
            } catch (Exception e) {
                HtmlStringBuffer buffer = new HtmlStringBuffer();
                logBasicDescription(buffer, object, path, property);
                buffer.append("Result: neither getter methods '");
                buffer.append(getterName).append("()' nor '");
                buffer.append(isGetterName).append("()' was found on class: '");
                buffer.append(object.getClass().getName()).append("'.");
                throw new RuntimeException(buffer.toString(), e);
            }
        }
        return method;
    }

    /**
     * Invoke the getterMethod for the given source object.
     *
     * @param getterMethod the getter method to invoke
     * @param source the source object to invoke the getter method on
     * @param property the getter method property name (used for logging)
     * @param path the full expression path (used for logging)
     * @return the getter result
     */
    private final static Object invokeGetter(Method getterMethod, Object source,
        String property, String path) {

        try {
            // Retrieve target object from getter
            return getterMethod.invoke(source, new Object[0]);

        } catch (Exception e) {
            // Log detailed error message of why getter failed
            HtmlStringBuffer buffer = new HtmlStringBuffer();
            logBasicDescription(buffer, source, path, property);
            buffer.append("Result: error occurred while trying to get");
            buffer.append(" instance of '");
            buffer.append(getterMethod.getReturnType().getName());
            buffer.append("' using method: '");
            buffer.append(getterMethod.getName()).append("()' of class '");
            buffer.append(source.getClass().getName()).append("'.");
            throw new RuntimeException(buffer.toString(), e);
        }
    }

    /**
     * Find the source object setter method for the given property.
     *
     * @param source the source object to find the setter method on
     * @param property the property which setter needs to be looked up
     * @param targetClass the setter parameter type
     * @param path the full expression path (used for logging purposes)
     * @return the setter method
     */
    private final static Method findSetter(Object source,
        String property, Class targetClass, String path) {
        Method method = null;

        // Find the setter for property
        String setterName = ClickUtils.toSetterName(property);

        Class sourceClass = source.getClass();
        Class[] classArgs = { targetClass };
        try {
            method = sourceClass.getMethod(setterName, classArgs);
        } catch (Exception e) {
            // Log detailed error message of why setter lookup failed
            HtmlStringBuffer buffer = new HtmlStringBuffer();
            logBasicDescription(buffer, source, path, property);
            buffer.append("Result: setter method '");
            buffer.append(setterName).append("(").append(targetClass.getName());
            buffer.append(")' was not found on class '");
            buffer.append(source.getClass().getName()).append("'.");
            throw new RuntimeException(buffer.toString(), e);
        }
        return method;
    }

    /**
     * Invoke the setter method for the given source and target object.
     *
     * @param setterMethod the setter method to invoke
     * @param source the source object to invoke the setter method on
     * @param target the target object to set
     * @param property the setter method property name (used for logging)
     * @param path the full expression path (used for logging)
     */
    private final static void invokeSetter(Method setterMethod, Object source,
        Object target, String property, String path) {

        try {
            Object[] objectArgs = {target};
            setterMethod.invoke(source, objectArgs);

        } catch (Exception e) {
            // Log detailed error message of why setter failed
            HtmlStringBuffer buffer = new HtmlStringBuffer();
            logBasicDescription(buffer, source, path, property);
            buffer.append("Result: error occurred while trying to set an");
            buffer.append(" instance of '");
            buffer.append(target.getClass().getName()).append("' using method '");
            buffer.append(setterMethod.getName()).append("(");
            buffer.append(target.getClass());
            buffer.append(")' of class '").append(source.getClass()).append("'.");
            throw new RuntimeException(buffer.toString(), e);
        }
    }

    /**
     * Log a generic error message to the specified buffer for the given object,
     * path and property.
     *
     * @param buffer the buffer to append log message to
     * @param object the active object when the exception occurred
     * @param path the current expression path
     * @param property the current property being processed
     */
    private static void logBasicDescription(HtmlStringBuffer buffer, Object object,
        String path, String property) {
        buffer.append("Invoked ensureObjectPathNotNull");
        buffer.append(" for class: '").append(object.getClass().getName());
        buffer.append("', path: '").append(path).append("' and property: '");
        buffer.append(property).append("'. ");
    }

   /**
    * Populate the given map from the values of the specified fieldList. The
    * map's key/value pairs are populated from the fields name/value. The keys
    * of the map are matched against each field name. If a key matches a field
    * name will the value of the field be copied to the map.
    *
    * @param fieldList the forms list of fields to obtain field values from
    * @param map the map to populate with field values
    * @param debug log debug statements when populating the map
    */
    private static void copyFieldsToMap(List fieldList, Map map) {

        LogService logService = ClickUtils.getLogService();

        String objectClassname = map.getClass().getName();
        objectClassname =
            objectClassname.substring(objectClassname.lastIndexOf(".") + 1);

        for (int i = 0, size = fieldList.size(); i < size; i++) {
            Field field = (Field) fieldList.get(i);

            // Check if the map contains the fields name. The fields name can
            // also be a path for example 'foo.bar'
            String fieldName = field.getName();
            if (map.containsKey(fieldName)) {

                map.put(fieldName, field.getValueObject());

                if (logService.isDebugEnabled()) {
                    String msg = "   Form -> " + objectClassname + "."
                         + fieldName + " : " + field.getValueObject();

                    logService.debug(msg);
                }
            }
        }
    }

    /**
     * Copy the map values to the specified fieldList. For every field in the
     * field list, a lookup is done in the map for a matching value. A match is
     * found if a field name matches against a key in the map. The matching
     * value is then copied to the field.
     *
     * @param map the map containing values to populate the fields with
     * @param fieldList the forms list of fields to be populated
     * @param debug log debug statements when populating the object
     */
    private static void copyMapToFields(Map map, List fieldList) {

        LogService logService = ClickUtils.getLogService();

        String objectClassname = map.getClass().getName();
        objectClassname =
            objectClassname.substring(objectClassname.lastIndexOf(".") + 1);

        for (int i = 0, size = fieldList.size(); i < size; i++) {
            Field field = (Field) fieldList.get(i);
            String fieldName = field.getName();

            // Check if the fieldName is contained in the map. For
            // example if a field has the name 'user.address', check if
            // 'user.address' is contained in the map.
            if (map.containsKey(fieldName)) {

                Object result = map.get(fieldName);

                field.setValueObject(result);

                if (logService.isDebugEnabled()) {
                    String msg = "   Form <- " + objectClassname + "."
                        + fieldName + " : " + result;
                    logService.debug(msg);
                }
            }
        }
    }

    /**
     * Add buttons for the given Container to the specified buttons list,
     * recursively including any Fields contained in child containers. The list
     * of returned buttons will exclude any <tt>Button</tt> or <tt>Label</tt>
     * fields.
     *
     * @param container the container to obtain the fields from
     * @param buttons the list of contained fields
     */
    private static void addButtons(final Container container, final List buttons) {
        for (int i = 0; i < container.getControls().size(); i++) {
            Control control = (Control) container.getControls().get(i);
            if (control instanceof Container) {
                // Include buttons that are containers
                if (control instanceof Button) {
                    buttons.add(control);
                }
                Container childContainer = (Container) control;
                addButtons(childContainer, buttons);
            } else if (control instanceof Button) {
                buttons.add(control);
            }
        }
    }

    /**
     * Add fields for the given Container to the specified field list,
     * recursively including any Fields contained in child containers.
     *
     * @param container the container to obtain the fields from
     * @param the list of contained fields
     */
    private static void addFields(final Container container, final List fields) {
        for (int i = 0; i < container.getControls().size(); i++) {
            Control control = (Control) container.getControls().get(i);
            if (control instanceof Container) {
                // Include fields that are containers
                if (control instanceof Field) {
                    fields.add(control);
                }
                Container childContainer = (Container) control;
                addFields(childContainer, fields);
            } else if (control instanceof Field) {
                fields.add(control);
            }
        }
    }

    /**
     * Add input fields (TextField, Select, Radio, Checkbox etc.) for the given
     * Container to the specified field list, recursively including any Fields
     * contained in child containers. The list of returned fields will exclude
     * any <tt>Button</tt>, <tt>FieldSet</tt> and <tt>Label</tt> fields.
     *
     * @param container the container to obtain the fields from
     * @param the list of contained fields
     */
    private static void addInputFields(final Container container, final List fields) {
        for (int i = 0; i < container.getControls().size(); i++) {
            Control control = (Control) container.getControls().get(i);
            if (control instanceof Label || control instanceof Button) {
                // Skip buttons and labels
                continue;

            } else if (control instanceof Container) {
                // Include fields but skip fieldSets
                if (control instanceof Field && !(control instanceof FieldSet)) {
                    fields.add(control);
                }
                Container childContainer = (Container) control;
                addInputFields(childContainer, fields);
            } else if (control instanceof Field) {
                fields.add(control);
            }
        }
    }

    /**
     * Add hidden fields for the given Container to the specified field list,
     * recursively including any Fields contained in child containers. The list
     * of returned fields will exclude any <tt>Button</tt>, <tt>FieldSet</tt>
     * and <tt>Label</tt> fields.
     *
     * @param container the container to obtain the hidden fields from
     * @param the list of contained fields
     */
    private static void addHiddenFields(final Container container, final List fields) {
        for (int i = 0; i < container.getControls().size(); i++) {
            Control control = (Control) container.getControls().get(i);
            if (control instanceof Label || control instanceof Button) {
                // Skip buttons and labels
                continue;
            } else if (control instanceof Container) {
                // Include fields but skip fieldSets
                if (control instanceof Field && !(control instanceof FieldSet)) {
                    Field field = (Field) control;
                    if (field.isHidden()) {
                        fields.add(control);
                    }
                }

                Container childContainer = (Container) control;
                addHiddenFields(childContainer, fields);

            } else if (control instanceof Field) {
                Field field = (Field) control;
                if (field.isHidden()) {
                    fields.add(control);
                }
            }
        }
    }

    /**
     * Add fields for the container to the specified field list, recursively
     * including any Fields contained in child containers. The list
     * of returned fields will exclude any <tt>Button</tt> and <tt>FieldSet</tt>
     * fields.
     *
     * @param container the container to obtain the fields from
     * @param the list of contained fields
     */
    private static void addFieldsAndLabels(final Container container, final List fields) {
        for (int i = 0; i < container.getControls().size(); i++) {
            Control control = (Control) container.getControls().get(i);
            if (control instanceof Button) {
                // Skip buttons
                continue;

            } else if (control instanceof Container) {
                // Include fields but skip fieldSets
                if (control instanceof Field && !(control instanceof FieldSet)) {
                    fields.add(control);
                }
                Container childContainer = (Container) control;
                addFieldsAndLabels(childContainer, fields);
            } else if (control instanceof Field) {
                fields.add(control);
            }
        }
    }

    /**
     * Add all the Fields for the given Container to the specified map,
     * recursively including any Fields contained in child containers.
     * <p/>
     * The map's key / value pair will consist of the control name and instance.
     *
     * @param container the container to obtain the fields from
     * @param the map of contained fields
     */
    private static void addFields(final Container container, final Map fields) {
        for (int i = 0; i < container.getControls().size(); i++) {
            Control control = (Control) container.getControls().get(i);
            if (control instanceof Container) {
                // Include fields that are containers
                if (control instanceof Field) {
                    fields.put(control.getName(), control);
                }
                Container childContainer = (Container) control;
                addFields(childContainer, fields);
            } else if (control instanceof Field) {
                fields.put(control.getName(), control);
            }
        }
    }

    /**
     * Add the list of container fields to the specified list of fields, which
     * are not valid, not hidden and not disabled.
     * <p/>
     * The list of returned invalid fields will exclude any <tt>Button</tt>
     * fields.
     *
     * @param container the container to obtain the fields from
     * @return list of form fields which are not valid, not hidden and not
     *  disabled
     */
    private static void addErrorFields(final Container container, final List fields) {
        for (int i = 0; i < container.getControls().size(); i++) {
            Control control = (Control) container.getControls().get(i);
            if (control instanceof Button) {
                // Skip buttons
                continue;

            } else if (control instanceof Container) {
                if (control instanceof Field) {
                    Field field = (Field) control;
                    if (!field.isValid() && !field.isHidden()
                        && !field.isDisabled()) {
                        fields.add(control);
                    }
                }
                Container childContainer = (Container) control;
                addErrorFields(childContainer, fields);

            } else if (control instanceof Field) {
                Field field = (Field) control;
                if (!field.isValid() && !field.isHidden()
                    && !field.isDisabled()) {
                    fields.add(control);
                }
            }
        }
    }
}
