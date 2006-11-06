package net.sf.click.util;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Method;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ognl.Ognl;
import junit.framework.TestCase;

public class PropertyPerformanceTest extends TestCase {
	
    public static Object getOgnlPropertyValue(Object source, String name, Map context) {

    	try {
    		return Ognl.getValue(name, context, source);

    	} catch (Exception e) {
    		e.printStackTrace();
    		throw new RuntimeException(e);
    	}
    }

    public static Object getOgnlPropertyValueCaching(Object source, String name, Map cache, Map context) {

    	try {
    		Object expressionTree = null;

    		synchronized (cache) {
    			expressionTree = cache.get(name);
    			if (expressionTree == null) {
    				expressionTree = Ognl.parseExpression(name);
    				cache.put(name, expressionTree);
    			}
			}

    		return Ognl.getValue(expressionTree, context, source);


    	} catch (Exception e) {
    		e.printStackTrace();
    		throw new RuntimeException(e);
    	}
    }
    
    private static Object getObjectPropertyValue(Object source, String name) {
        Method method = null;
        try {
        	method = source.getClass().getMethod(ClickUtils.toGetterName(name), null);

            return method.invoke(source, null);

        } catch (NoSuchMethodException nsme) {
            try {
                method = source.getClass().getMethod(ClickUtils.toIsGetterName(name), null);

                return method.invoke(source, null);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }    
    
    public static Object getPropertyValue(Object source, String name) {
        String basePart = name;
        String remainingPart = null;

        int baseIndex = name.indexOf(".");
        if (baseIndex != -1) {
            basePart = name.substring(0, baseIndex);
            remainingPart = name.substring(baseIndex + 1);
        }

        Object value = getObjectPropertyValue(source, basePart);

        if (remainingPart == null || value == null) {
            return value;

        } else {
            return getPropertyValue(value, remainingPart);
        }
    }    
    
    private static Object getObjectPropertyValueCaching(Object source, String name, Map cache) {
        String methodNameKey = source.getClass().getName() + "." + name;;

        Method method = null;
        try {
        	synchronized(cache) {
        		method = (Method) cache.get(methodNameKey);

        		if (method == null) {

        			method = source.getClass().getMethod(ClickUtils.toGetterName(name), null);
        			cache.put(methodNameKey, method);
        		}
        	}

            return method.invoke(source, null);

        } catch (NoSuchMethodException nsme) {
            try {
                method = source.getClass().getMethod(ClickUtils.toIsGetterName(name), null);
                
            	synchronized(cache) {
            		cache.put(methodNameKey, method);
            	}

                return method.invoke(source, null);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }    
    
    public static Object getPropertyValueCaching(Object source, String name, Map cache) {
        String basePart = name;
        String remainingPart = null;

        int baseIndex = name.indexOf(".");
        if (baseIndex != -1) {
            basePart = name.substring(0, baseIndex);
            remainingPart = name.substring(baseIndex + 1);
        }

        Object value = getObjectPropertyValueCaching(source, basePart, cache);

        if (remainingPart == null || value == null) {
            return value;

        } else {
            return getPropertyValueCaching(value, remainingPart, cache);
        }
    }    
	
	
	private final int loopIterations = 1000;
	private final ParentObject parentObject = new ParentObject("malcolm", null, new Date(), Boolean.TRUE, new ChildObject("edgar"));
	
	private int threadCount;
	private long cumulativeTime;
	private List threadList = new ArrayList();
	
    // ---------------------------------------------------------- Test Methods
	
	/**
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		threadCount = 20;
		cumulativeTime = 0;
		threadList.clear();
	}
	
	protected void tearDown() throws Exception {
		for (int i = 0; i < threadList.size(); i++) {
			Thread thread = (Thread) threadList.get(i);
			try {
				thread.stop();
			} catch (Exception e) {				
			}
		}
	}
	
	public void testOgnl() throws Exception {
		
		final Map context = new HashMap();

		for (int i = 0; i < threadCount; i++) {
			OgnlTest ot = new OgnlTest(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						Long time = (Long) e.getSource();
						ognlTestComplete(time);
					}
				},
				loopIterations,
				parentObject,
				context
			);
			
			Thread thread = new Thread(ot);
			thread.start();
			threadList.add(thread);
		}		
		
		while (threadCount > 1) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException ie) {
				ie.printStackTrace();
			}
		}
		
		logTime("OGNL cumulative time", cumulativeTime);
	}

	public void testOgnlCaching() throws Exception {
		
		final Map cache = new HashMap();
		final Map context = new HashMap();

		for (int i = 0; i < threadCount; i++) {
			OgnlCachingTest ot = new OgnlCachingTest(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						Long time = (Long) e.getSource();
						ognlTestComplete(time);
					}
				},
				loopIterations,
				parentObject,
				cache,
				context
			);
			
			Thread thread = new Thread(ot);
			thread.start();
			threadList.add(thread);
		}		
		
		while (threadCount > 1) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException ie) {
				ie.printStackTrace();
			}
		}
		
		logTime("OGNL with caching cumulative time", cumulativeTime);
	}
	
	public void testReflection() throws Exception {
		
		for (int i = 0; i < threadCount; i++) {
			ReflectionTest rt = new ReflectionTest(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						Long time = (Long) e.getSource();
						ognlTestComplete(time);
					}
				},
				loopIterations,
				parentObject
			);
			
			Thread thread = new Thread(rt);
			thread.start();
			threadList.add(thread);
		}		
		
		while (threadCount > 1) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException ie) {
				ie.printStackTrace();
			}
		}
		
		logTime("Reflection cumulative time", cumulativeTime);
	}

	public void testReflectionCaching() throws Exception {
		
		Map cache = new HashMap();
		
		for (int i = 0; i < threadCount; i++) {
			ReflectionCachingTest rt = new ReflectionCachingTest(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						Long time = (Long) e.getSource();
						ognlTestComplete(time);
					}
				},
				loopIterations,
				parentObject,
				cache
			);
			
			Thread thread = new Thread(rt);
			thread.start();
			threadList.add(thread);
		}		
		
		while (threadCount > 1) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException ie) {
				ie.printStackTrace();
			}
		}
		
		logTime("Reflection with caching cumulative time", cumulativeTime);
	}

	private synchronized void ognlTestComplete(Long time) {		
		threadCount--;
		cumulativeTime += time.longValue();
	}
	
	private void logTime(String msg, long time) {
		System.out.println(msg + ": " + NumberFormat.getNumberInstance().format(cumulativeTime) + "ms");
	}
	
	// ----------------------------------------------------------- Test Classes

	public static class OgnlTest implements Runnable {
		private final ActionListener listener;
		private final int iterations;
		private final ParentObject testObject;
		private final Map context;
		
		public OgnlTest(ActionListener listener, int iterations, ParentObject testObject, Map context) {
			this.listener = listener;
			this.iterations = iterations;
			this.testObject = testObject;
			this.context = context;
		}
		
		public void run() {
			long start = System.currentTimeMillis();

			for (int i = 0; i < iterations; i++) {
				assertNotNull(getOgnlPropertyValue(testObject, "name", context));
				assertNull(getOgnlPropertyValue(testObject, "value", context));
				assertNotNull(getOgnlPropertyValue(testObject, "date", context));
				assertNotNull(getOgnlPropertyValue(testObject, "valid", context));
				assertNotNull(getOgnlPropertyValue(testObject, "child.name", context));
			}

			Long time = new Long((System.currentTimeMillis() - start));
			
			listener.actionPerformed(new ActionEvent(time, ActionEvent.ACTION_PERFORMED, "complete"));
		}
	}	
	
	public static class OgnlCachingTest implements Runnable {
		private final ActionListener listener;
		private final int iterations;
		private final ParentObject testObject;
		private final Map cache;
		private final Map context;
		
		public OgnlCachingTest(ActionListener listener, int iterations, ParentObject testObject, Map cache, Map context) {
			this.listener = listener;
			this.iterations = iterations;
			this.testObject = testObject;
			this.cache = cache;
			this.context = context;
		}
		
		public void run() {
			long start = System.currentTimeMillis();

			for (int i = 0; i < iterations; i++) {
				assertNotNull(getOgnlPropertyValueCaching(testObject, "name", cache, context));
				assertNull(getOgnlPropertyValueCaching(testObject, "value", cache, context));
				assertNotNull(getOgnlPropertyValueCaching(testObject, "date", cache, context));
				assertNotNull(getOgnlPropertyValueCaching(testObject, "valid", cache, context));
				assertNotNull(getOgnlPropertyValueCaching(testObject, "child.name", cache, context));
			}

			Long time = new Long((System.currentTimeMillis() - start));
			
			listener.actionPerformed(new ActionEvent(time, ActionEvent.ACTION_PERFORMED, "complete"));
		}
	}

	public static class ReflectionTest implements Runnable {
		private final ActionListener listener;
		private final int iterations;
		private final ParentObject testObject;
		
		public ReflectionTest(ActionListener listener, int iterations, ParentObject testObject) {
			this.listener = listener;
			this.iterations = iterations;
			this.testObject = testObject;
		}
		
		public void run() {
			long start = System.currentTimeMillis();

			for (int i = 0; i < iterations; i++) {
				assertNotNull(getPropertyValue(testObject, "name"));
				assertNull(getPropertyValue(testObject, "value"));
				assertNotNull(getPropertyValue(testObject, "date"));
				assertNotNull(getPropertyValue(testObject, "valid"));
				assertNotNull(getPropertyValue(testObject, "child.name"));
			}

			Long time = new Long((System.currentTimeMillis() - start));
			
			listener.actionPerformed(new ActionEvent(time, ActionEvent.ACTION_PERFORMED, "complete"));
		}
	}	
	
	public static class ReflectionCachingTest implements Runnable {
		private final ActionListener listener;
		private final int iterations;
		private final ParentObject testObject;
		private final Map cache;
		
		public ReflectionCachingTest(ActionListener listener, int iterations, ParentObject testObject, Map cache) {
			this.listener = listener;
			this.iterations = iterations;
			this.testObject = testObject;
			this.cache = cache;
		}
		
		public void run() {
			long start = System.currentTimeMillis();

			for (int i = 0; i < iterations; i++) {
				assertNotNull(getPropertyValueCaching(testObject, "name", cache));
				assertNull(getPropertyValueCaching(testObject, "value", cache));
				assertNotNull(getPropertyValueCaching(testObject, "date", cache));
				assertNotNull(getPropertyValueCaching(testObject, "valid", cache));
				assertNotNull(getPropertyValueCaching(testObject, "child.name", cache));
			}

			Long time = new Long((System.currentTimeMillis() - start));
			
			listener.actionPerformed(new ActionEvent(time, ActionEvent.ACTION_PERFORMED, "complete"));
		}
	}	
	
	
}
