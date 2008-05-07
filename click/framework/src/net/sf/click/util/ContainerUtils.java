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
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.sf.click.control.Container;
import net.sf.click.Control;
import net.sf.click.Page;
import net.sf.click.control.BasicForm;
import net.sf.click.control.Button;
import net.sf.click.control.Field;
import net.sf.click.control.Label;
import net.sf.click.service.LogService;
import org.apache.commons.lang.ClassUtils;

/**
 *
 * @author Bob Schellink
 */
public class ContainerUtils {

    /**
     * Return the list of Fields for the given Container, recursively including
     * any Fields contained in child containers. The list of returned fields
     * will exclude any <tt>Button</tt> and <tt>Label</tt> fields.
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
     * Return the list of Fields for the given Container, recursively including
     * any Fields contained in child containers. The list of returned fields
     * will exclude any <tt>Button</tt> fields.
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
     * Find and return the specified controls parent BasicForm or null
     * if no BasicForm is present.
     *
     * @param control the control to check for BasicForm
     * @return the controls parent BasicForm or null if no parent is a
     * BasicForm
     */
    public static BasicForm findForm(Control control) {
        while (control.getParent() != null && !(control.getParent() instanceof Page)) {
            control = (Control) control.getParent();
            if (control instanceof BasicForm) {
                return (BasicForm) control;
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
                    return findControlByName(childContainer, name);
                }

            }
        }
        return null;
    }

    /**
     * Populate the given object's attributes with the Containers's field values.
     * <p/>
     * The specified Object can either be a POJO (plain old java object) or
     * a {@link java.util.Map}. If a POJO is specified, its attributes are
     * populated from  matching container fields. If a map is specified, its
     * key/value pairs are populated from matching container fields.
     *
     * @param container the Container to obtain field values from
     * @param object the object to populate with field values
     */
    public static void copyContainerToObject(Container container,
        Object object) {

        if (container == null) {
            throw new IllegalArgumentException("Null container parameter");
        }

        if (object == null) {
            throw new IllegalArgumentException("Null object parameter");
        }

        final List fieldList = getFields(container);

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

            ensureObjectPathNotNull(object, field.getName());

            try {
                PropertyUtils.setValueOgnl(object, field.getName(), field.getValueObject(), ognlContext);

                if (logService.isDebugEnabled()) {
                    String containerClassName =
                        ClassUtils.getShortClassName(container.getClass());
                    String msg = "    " + containerClassName + " -> "
                        + objectClassname + "." + field.getName() + " : "
                        + field.getValueObject();

                    logService.debug(msg);
                }

            } catch (Exception e) {
                String msg =
                    "Error incurred invoking " + objectClassname + "." +
                    field.getName() + " with " + field.getValueObject() +
                    " error: " + e.toString();

                logService.debug(msg);
            }
        }
    }

    /**
     * Populate the given Container field values with the object's attributes.
     * <p/>
     * The specified Object can either be a POJO (plain old java object) or
     * a {@link java.util.Map}. If a POJO is specified, its attributes are
     * copied to matching container fields. If a map is specified, its key/value
     * pairs are copied to matching container fields.
     *
     * @param object the object to obtain attribute values from
     * @param container the Container to populate
     */
    public static void copyObjectToContainer(Object object,
        Container container) {

        if (object == null) {
            throw new IllegalArgumentException("Null object parameter");
        }

        if (container == null) {
            throw new IllegalArgumentException("Null container parameter");
        }

        final List fieldList = getFields(container);

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

            try {
                Object result = PropertyUtils.getValue(object, field.getName());

                field.setValueObject(result);

                if (logService.isDebugEnabled()) {
                    String containerClassName =
                        ClassUtils.getShortClassName(container.getClass());
                    String msg = "    " + containerClassName + " <- "
                        + objectClassname + "." + field.getName() + " : "
                        + result;
                    logService.debug(msg);
                }

            } catch (Exception e) {
                String msg = "Error incurred invoking " + objectClassname + "." +
                    field.getName() + " error: " + e.toString();

                logService.debug(msg);
            }
        }
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
     * Return true if the specified field's name is contained within the
     * specified set of properties
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
     * encoutering null values
     * @param path the navigation path
     */
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
            if (map.containsKey(field.getName())) {

                map.put(field.getName(), field.getValueObject());

                if (logService.isDebugEnabled()) {
                    String msg = "   Form -> " + objectClassname + "."
                         + field.getName() + " : " + field.getValueObject();

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

                Object result = map.get(field.getName());

                field.setValueObject(result);

                if (logService.isDebugEnabled()) {
                    String msg = "   Form <- " + objectClassname + "."
                        + field.getName() + " : " + result;
                    logService.debug(msg);
                }
            }
        }
    }

    /**
     * Adds the HTML head imports for the container and all its child controls
     * to the specified includeSet.
     *
     * {@link net.sf.click.Control#getHtmlImports()}
     * 
     * @param container the Container to obtain html head imports from
     * @param includeSet the set containing all the HTML head imports for the
     * container and its child controls
     */
    private static void addAllHtmlImports(Container container, Set includeSet) {
        List list = container.getControls();

        String thisInclude = container.getHtmlImports();
        if (thisInclude != null) {
            includeSet.add(thisInclude);
        }

        for (int i = 0,  size = list.size(); i < size; i++) {
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

    /**
     * Add fields for the given Container to the specified field list,
     * recursively including any Fields contained in child containers. The list
     * of returned fields will exclude any <tt>Button</tt> or <tt>Label</tt>
     * fields.
     *
     * @param container the container to obtain the fields from
     * @param the list of contained fields
     */
    private static void addFields(final Container container, final List fields) {
        for (int i = 0; i < container.getControls().size(); i++) {
            Control control = (Control) container.getControls().get(i);
            if (control instanceof Label || control instanceof Button) {
                // Skip buttons and labels
                continue;

            } else if (control instanceof Container) {
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
     * Add fields for the container to the specified field list, recursively
     * including any Fields contained in child containers. The list
     * of returned fields will exclude any <tt>Button</tt> fields.
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
                    if (!field.isValid() && !field.isHidden() &&
                       !field.isDisabled()) {
                        fields.add(control);
                    }
                }
                Container childContainer = (Container) control;
                addErrorFields(childContainer, fields);
            } else if (control instanceof Field) {
                Field field = (Field) control;
                if (!field.isValid() && !field.isHidden() &&
                    !field.isDisabled()) {
                    fields.add(control);
                }
            }
        }
    }
}
