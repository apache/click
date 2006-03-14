package net.sf.click.utilinvokelistener;

import net.sf.click.util.ClickUtils;
import junit.framework.TestCase;

/**
 * Tests ClickUtils.invokeLister(). This is in a seperate package 
 * because otherwise the protected, package-private restrictions
 * would have no meaning. 
 * 
 * @author Christian Essl
 *
 */
public class ClickUtilsInvokeListenerTest extends TestCase {

    public void testInvokeListener() {
        ListenerMock lm = new ListenerMock();
        
        assertTrue(ClickUtils.invokeListener(lm, "onClickTrue"));
        assertEquals(1,lm.called);
        lm.called = 0;
        
        assertFalse(ClickUtils.invokeListener(lm, "onClickFalse"));
        assertEquals(1,lm.called);
        lm.called = 0;
        
        try{
            ClickUtils.invokeListener(lm,"noReturn");
            fail();
        }catch(Exception e){}
       
        try{
            ClickUtils.invokeListener(lm,"privateMethod");
            fail();
        }catch(Exception e){}

        try{
            ClickUtils.invokeListener(lm,"protectedMethod");
            fail();
        }catch(Exception e){}
        
        try{
            ClickUtils.invokeListener(lm,"packagePrivateMethod");
            fail();
        }catch(Exception e){}
        
        PrivListenerMock pM = new PrivListenerMock();
        try{
            ClickUtils.invokeListener(pM,"onClick");
            fail();
        }catch(Exception e){}
        
        //the anonymous inner class
        Object anon = new Object() {
            public boolean onClick(){
                return true;
            }
            private boolean privateMethod(){
                return true;
            }
            protected boolean protectedMethod() {
                return true;
            }
            boolean packagePrivateMethod(){
                return false;
            }
        };
        
        assertTrue(ClickUtils.invokeListener(anon, "onClick"));
        
        try{
            ClickUtils.invokeListener(anon,"noMethod");
            fail();
        }catch(Exception e){}
       
        try{
            ClickUtils.invokeListener(anon,"privateMethod");
            fail();
        }catch(Exception e){}

        try{
            ClickUtils.invokeListener(anon,"protectedMethod");
            fail();
        }catch(Exception e){}
        
        try{
            ClickUtils.invokeListener(anon,"packagePrivateMethod");
            fail();
        }catch(Exception e){}
        
    }
    
    public static class ListenerMock {
        int called = 0;
        public boolean onClickTrue(){
           called++;
           return true;
        }
        
        public boolean onClickFalse() {
            called++;
            return false;
        }
        
        public void noReturn() {
        }
        
        private boolean privateMethod(){
            return true;
        }
        
        protected boolean protectedMethod() {
            return true;
        }
        
        boolean packagePrivateMethod(){
            return true;
        }
    }
    
    private static class PrivListenerMock {
        public boolean onClick(){
            return true;
        }
    }


}
