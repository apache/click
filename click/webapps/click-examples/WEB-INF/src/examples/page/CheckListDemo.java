package examples.page;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.sf.click.control.Form;
import net.sf.click.control.Option;
import net.sf.click.control.Submit;
import net.sf.click.extras.control.CheckList;

public class CheckListDemo extends BorderedPage{

    private static List OPTIONS = new ArrayList();
    private static List SORT_OPTIONS = new ArrayList();
    static{
        for(int i=0;i<30;i++) {
            OPTIONS.add(new Option(Integer.toString(i),"tutam gallia deviso est in partes "+i));
        }
        OPTIONS = Collections.unmodifiableList(OPTIONS);

        for(int i=0;i<15;i++) {
            SORT_OPTIONS.add(new Option(Integer.toString(i),"drag to sort me "+i));
        }
    }

    private Form form;
    private CheckList list1;
    private CheckList list2;
    private CheckList list3;

    public CheckListDemo() {
        super();
    }

    /* (non-Javadoc)
     * @see net.sf.click.Page#onInit()
     */
    public void onInit() {
        super.onInit();

        form = new Form("form");
        addControl(form);

        list1 = new CheckList("list1", "First List", false);
        list1.setOptionList(SORT_OPTIONS);
        list1.setAttribute("class", "cl2");
        list1.setSortable(true);
        form.add(list1);

        list2 = new CheckList("list2", "Second List", true);
        list2.setOptionList(OPTIONS);
        List selected = new ArrayList(Arrays.asList(new String[]{"1","3","6","10"}));
        list2.setValues(selected);
        list2.setAttribute("class", "cl2");
        list2.setHeight("25em");
        form.add(list2);

        list3 = new CheckList("list3", "Third List",false);
        list3.setOptionList(OPTIONS);
        list3.addStyle("width: 50em; height: 15em");
        list3.setAttribute("class", "cl3");
        form.add(list3);

        form.add(new Submit("ok","   Ok   ",this,"onOk"));
    }

    public boolean onOk(){
        if(form.isValid()){
            addModel("showSelected",Boolean.TRUE);
            addModel("list1",list1.getValues());
            addModel("list2",list2.getValues());
            addModel("list3",list3.getValues());

            //take the sort out of the list1
            List order = list1.getSortorder();
            Option[] tmp = new Option[order.size()];
            for (Iterator it = SORT_OPTIONS.iterator(); it.hasNext(); ) {
                Option opt = (Option) it.next();
                String value = opt.getValue();
                int i = order.indexOf(value);
                tmp[i] = opt;
            }
            SORT_OPTIONS = new ArrayList(Arrays.asList(tmp));
            list1.setOptionList(SORT_OPTIONS);
            addModel("list1Order",order);
        }
        return true;
    }
}
