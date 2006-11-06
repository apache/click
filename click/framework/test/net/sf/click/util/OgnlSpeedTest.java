package net.sf.click.util;

import java.lang.reflect.Method;

import junit.framework.Assert;
import junit.framework.TestCase;
import ognl.Ognl;

public class OgnlSpeedTest extends TestCase {

    private int loops = 10000;
    private Dto dto;
    private Integer two;
    private Integer three;

    public void setUp() {
        this.dto = new Dto();
        this.two = new Integer(2);
        this.three = new Integer(3);
        this.dto.setInteger(this.two);
    }

    public void testGetOgnlNoCaching() throws Exception {
    	long start = System.currentTimeMillis();
    	String method = "testGetOgnlNoCaching";
    	
        // Once to make sure it worked
        Assert.assertEquals(this.two, Ognl.getValue("integer", this.dto));

        for (int i = 0; i < loops; i++) {
        	Assert.assertNotNull(Ognl.getValue("integer", this.dto));
        }
        
        logTime(start, method);
    }

    public void testGetOgnlWithCaching() throws Exception {
    	long start = System.currentTimeMillis();
    	String method = "testGetOgnlWithCaching";
    	
        // Once to make sure it worked
        Object expression = Ognl.parseExpression("integer");
        Assert.assertEquals(this.two, Ognl.getValue(expression, this.dto));

        for (int i = 0; i < loops; i++) {
        	Assert.assertNotNull(Ognl.getValue(expression, this.dto));
        }
        
        logTime(start, method);
    }

    public void testGetReflectionNoCaching() throws Exception {
    	long start = System.currentTimeMillis();
    	String method = "testGetReflectionNoCaching";
    	
        // Once to make sure it worked
        Method getter = Dto.class.getMethod("getInteger", new Class[] {});
        Assert.assertEquals(this.two, getter.invoke(this.dto, new Object[] {}));

        for (int i = 0; i < loops; i++) {
            getter = Dto.class.getMethod("getInteger", new Class[] {});
            Assert.assertNotNull(getter.invoke(this.dto, new Object[] {}));
        }
        
        logTime(start, method);
    }

    public void testGetReflectionWithCaching() throws Exception {
    	long start = System.currentTimeMillis();
    	String method = "testGetReflectionWithCaching";
    	
        // Once to make sure it worked
        Method getter = Dto.class.getMethod("getInteger", new Class[] {});
        Assert.assertEquals(this.two, getter.invoke(this.dto, new Object[] {}));

        for (int i = 0; i < loops; i++) {
        	Assert.assertNotNull(getter.invoke(this.dto, new Object[] {}));
        }
        
        logTime(start, method);
    }

    public void testGetDirect() throws Exception {
    	long start = System.currentTimeMillis();
    	String method = "testGetDirect";
    	
        // Once to make sure it worked
        Assert.assertEquals(this.two, this.dto.getInteger());

        for (int i = 0; i < loops; i++) {
        	Assert.assertNotNull(this.dto.getInteger());
        }
        
        logTime(start, method);
    }

    public void testSetOgnlNoCaching() throws Exception {
    	long start = System.currentTimeMillis();
    	String method = "testSetOgnlNoCaching";
    	
        // Once to make sure it worked
        Ognl.setValue("integer", this.dto, this.three);
        Assert.assertEquals(3, this.dto.getInteger().intValue());

        for (int i = 0; i < loops; i++) {
            Ognl.setValue("integer", this.dto, this.three);
        }
        
        logTime(start, method);
    }

    public void testSetOgnlWithCaching() throws Exception {
    	long start = System.currentTimeMillis();
    	String method = "testSetOgnlWithCaching";
    	
        // Once to make sure it worked
        Object expression = Ognl.parseExpression("integer");
        Ognl.setValue(expression, this.dto, this.three);
        Assert.assertEquals(3, this.dto.getInteger().intValue());

        for (int i = 0; i < loops; i++) {
            Ognl.setValue(expression, this.dto, this.three);
        }
        
        logTime(start, method);
    }

    public void testSetReflectionNoCaching() throws Exception {
    	long start = System.currentTimeMillis();
    	String method = "testSetReflectionNoCaching";
    	
        // Once to make sure it worked
        Method setter = Dto.class.getMethod("setInteger", new Class[] { Integer.class });
        setter.invoke(this.dto, new Object[] { this.three });
        Assert.assertEquals(3, this.dto.getInteger().intValue());

        for (int i = 0; i < loops; i++) {
            setter = Dto.class.getMethod("setInteger", new Class[] { Integer.class });
            setter.invoke(this.dto, new Object[] { this.three });
        }
        
        logTime(start, method);
    }

    public void testSetReflectionWithCaching() throws Exception {
    	long start = System.currentTimeMillis();
    	String method = "testSetReflectionWithCaching";
    	
        // Once to make sure it worked
        Method setter = Dto.class.getMethod("setInteger", new Class[] { Integer.class });
        setter.invoke(this.dto, new Object[] { this.three });
        Assert.assertEquals(3, this.dto.getInteger().intValue());

        for (int i = 0; i < loops; i++) {
            setter.invoke(this.dto, new Object[] { this.three });
        }
        
        logTime(start, method);
    }

    public void testSetDirect() throws Exception {
    	long start = System.currentTimeMillis();
    	String method = "testSetDirect";
    	
        // Once to make sure it worked
        this.dto.setInteger(this.three);
        Assert.assertEquals(3, this.dto.getInteger().intValue());

        for (int i = 0; i < loops; i++) {
            this.dto.setInteger(this.three);
        }
        
        logTime(start, method);
    }

    private static class Dto {
        private Integer integer;

        public Integer getInteger() {
            return this.integer;
        }

        public void setInteger(Integer integer) {
            this.integer = integer;
        }
    }
    
    private void logTime(long start, String method) {
        System.out.println(method + ": " + (System.currentTimeMillis() - start) + "ms");
    }

}

