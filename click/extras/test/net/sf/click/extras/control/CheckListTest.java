package net.sf.click.extras.control;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sf.click.control.Option;

import junit.framework.TestCase;

public class CheckListTest extends TestCase {

    public CheckListTest() {
        super();
    }
    
    public void testSortOptions() {
        CheckList cL = new CheckList();
        int[] in = {1,2,3,4,5,6};
        List oL = createOptionsList(in);

        cL.setOptionList(oL);
        int[] sort = {6,4,2,1,3,5};
        cL.sortOptions(createValues(sort));
        compareOptions(sort,cL.getOptionList());
        
        cL.setOptionList(oL);
        cL.sortOptions(createValues(in));
        compareOptions(in,cL.getOptionList());
        
        cL.setOptionList(oL);
        sort = new int[]{5,4,6,10};
        cL.sortOptions(createValues(sort));
        compareOptions(new int[]{5,4,6,1,2,3},cL.getOptionList());
        
        cL.setOptionList(oL);
        sort = new int[]{7,10,5,9,4,6,1,2,3,8};
        cL.sortOptions(createValues(sort));
        compareOptions(new int[]{5,4,6,1,2,3},cL.getOptionList());
        
    }
    
    private String[] createValues(int[] is) {
        String[] ret = new String[is.length];
        for (int i = 0; i < is.length; i++) {
            ret[i] = Integer.toString(is[i]);
        }
        return ret;
    }
    
    private List createOptionsList(int[] values) {
        List ret = new ArrayList();
        for(int i=0; i<values.length; i++) {
            String value = Integer.toString(values[i]);
            String label = "Label: "+i;
            ret.add(new Option(value,label));
        }
        return ret;
    }
    
    private void compareOptions(int[] values,List options) {
        assertNotNull(options);
        String demanded ="";
        String given = "";
        for(int i=0;i<values.length;i++) {
            demanded += values[i]+",";
        }
        for (Iterator it = options.iterator(); it.hasNext(); ) {
            Option opt = (Option) it.next();
            assertNotNull(opt);
            given += opt.getValue()+",";
        }
        
        assertEquals(demanded,given);
    }

}
