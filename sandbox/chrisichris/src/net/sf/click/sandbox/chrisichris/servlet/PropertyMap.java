package net.sf.click.sandbox.chrisichris.servlet;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import net.sf.click.Page;

/**
 * A map which maps to properteis and public fields of an Object. Properties are mapped first.
 * @author Christian
 *
 */
public class PropertyMap extends AbstractMap {

    private final Object page;
    private final Map/*<String,Invoker>*/ beanMap;
    
    public PropertyMap(Object page) {
        beanMap = getInvokers(page.getClass());
        this.page = page;
    }
    
    public int size() {
        return beanMap.size();
    }

    public boolean isEmpty() {
        return beanMap.isEmpty();
    }
    

    public boolean containsKey(Object key) {
        return beanMap.containsKey(key);
    }

    public Object get(Object key) {
        Invoker inv = (Invoker) beanMap.get(key);
        if(inv != null) {
            return inv.getValue(page);
        } else {
            return null;
        }
    }

    public Object put(Object key, Object value) {
        Object ret = get(key);
        Invoker inv = (Invoker) beanMap.get(key);
        if(inv != null) {
            inv.setValue(page,value);
        } else {
            throw new UnsupportedOperationException("Can not set ["+key+"] prop on bean: "+page.getClass());
        }
        return ret;
    }

    public Object remove(Object key) {
        throw new UnsupportedOperationException();
    }

    public void clear() {
        throw new UnsupportedOperationException();
    }

    public Set entrySet() {
        final Set wrappedSet = beanMap.entrySet();
        Set ret = new AbstractSet() {

            public int size() {
                return wrappedSet.size();
            }
            
            public Iterator iterator() {
                final Iterator wrapped = wrappedSet.iterator();
                Iterator ret = new Iterator() {

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }

                    public boolean hasNext() {
                        return wrapped.hasNext();
                    }

                    public Object next() {
                        return new MapEntry((Map.Entry)wrapped.next());
                    }
                    
                };
                return ret;
            }
            
        };
        return ret;
    }
    
    private class MapEntry implements Map.Entry {

        final Map.Entry wrapped;
        
        MapEntry(Map.Entry wrapped) {
            this.wrapped = wrapped;
        }
        
        public Object getKey() {
            return wrapped.getKey();
        }

        public Object getValue() {
            Object v = wrapped.getValue();
            if(wrapped == null) {
                return null;
            }
            Object ret = ((Invoker)v).getValue(page);
            return ret;
        }

        public Object setValue(Object value) {
            Object v = wrapped.getValue();
            if(wrapped == null) {
                throw new UnsupportedOperationException("No property");
            }
            Invoker inv = (Invoker) v;
            Object ret = getValue();
            inv.setValue(page,value);
            return ret;
        }
        
        public boolean equals(Object obj) {
            if(obj instanceof MapEntry) {
                return wrapped.equals(((MapEntry)obj).wrapped);
            }
            return false;
        }
        
        public int hashCode() {
            return wrapped.hashCode();
        }
        
    }
    
    private static final Map/*<Class,Map<String,Invoker>>*/ invokersMaps = new HashMap();
    
    private static Map getInvokers(Class pageClass) {
        Map/*<String,Invoker>*/ iM = (Map) invokersMaps.get(pageClass);
        if(iM != null) {
            return iM;
        }
        iM = new HashMap();
        //collect all properties
        BeanInfo info;
        try {
            info = Introspector.getBeanInfo(pageClass,Page.class);
        } catch (IntrospectionException e) {
            RuntimeException ex = new RuntimeException("Error retrieving properties for class:"+pageClass);
            ex.initCause(e);
            throw ex;
        }
        
        PropertyDescriptor[] props = info.getPropertyDescriptors();
        for (int i = 0; i < props.length; i++) {
            PropertyDescriptor prop = props[i];
            String name = prop.getName();
            if(!iM.containsKey(name)) {
                Method m = prop.getReadMethod();
                if(m != null && m.getParameterTypes().length == 0) {
                    Method sm = prop.getWriteMethod();
                    if(sm != null && sm.getParameterTypes().length != 1) {
                        sm = null;
                    }
                    iM.put(name,new PropertyInvoker(name,m,sm));
                    
                }
            }
        }

        //collect all Fields
        Field[] fields = pageClass.getFields();
        for (int i = 0; i < fields.length; i++) {
            if(!iM.containsKey(fields[i].getName())) {
                iM.put(fields[i].getName(),new FieldInvoker(fields[i].getName(),fields[i]));
            }
        }
        
        invokersMaps.put(pageClass,iM);
        return iM;
    }

    abstract private static class Invoker {
        private final String name;

        Invoker(String name) {
            this.name = name;
        }

        abstract Object getValue(Object target);
        
        abstract void setValue(Object target,Object value);
    }

    private static class PropertyInvoker extends Invoker {

        private final Method method;
        private final Method setMethod;

        public PropertyInvoker(String name, Method method,Method setMethod) {
            super(name);
            this.method = method;
            this.setMethod = setMethod;
        }

        Object getValue(Object target) {
            try {
                return method.invoke(target, null);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("this is a bug please report");
            } catch (IllegalAccessException e) {
                throw new RuntimeException("This is a bug please report");
            } catch (InvocationTargetException e) {
                Throwable ex = e.getTargetException();
                if (ex instanceof RuntimeException) {
                    throw ((RuntimeException) ex);
                }
                if (ex instanceof Error) {
                    throw (Error) ex;
                }

                RuntimeException ex1 = new RuntimeException(
                        "Error invoking property: " + ex);
                ex1.initCause(ex);
                throw ex1;
            }
        }

        void setValue(Object target,Object value) {
            if(setMethod == null) {
                throw new UnsupportedOperationException("No set property");
            }
            
            try {
                setMethod.invoke(target,new Object[]{value});
            } catch (IllegalArgumentException e) {
                throw e;
            } catch (IllegalAccessException e) {
                throw new RuntimeException("This is a bug please report");
            } catch (InvocationTargetException e) {
                Throwable ex = e.getTargetException();
                if (ex instanceof RuntimeException) {
                    throw ((RuntimeException) ex);
                }
                if (ex instanceof Error) {
                    throw (Error) ex;
                }

                RuntimeException ex1 = new RuntimeException(
                        "Error invoking property setter: " + ex);
                ex1.initCause(ex);
                throw ex1;
            }
        }

    }

    private static class FieldInvoker extends Invoker {

        private final Field field;

        public FieldInvoker(String name, Field field) {
            super(name);
            this.field = field;
        }

        Object getValue(Object target) {

            try {
                return field.get(target);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("this is a bug please report");
            } catch (IllegalAccessException e) {
                throw new RuntimeException("This is a bug please report");
            }
        }

        void setValue(Object target, Object value) {
            try {
                field.set(target,value);
            } catch (IllegalArgumentException e) {
                throw e;
            } catch (IllegalAccessException e) {
                RuntimeException ex = new RuntimeException("Could not set value on field: "+e);
                ex.initCause(e);
                throw ex;
            }
        }
    }
}
