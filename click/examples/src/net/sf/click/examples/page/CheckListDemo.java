package net.sf.click.examples.page;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sf.click.control.Form;
import net.sf.click.control.Option;
import net.sf.click.control.Submit;
import net.sf.click.extras.control.CheckList;
import net.sf.click.extras.control.PageSubmit;

/**
 * Provides CheckList control example.
 *
 * @author Christian Essel
 */
public class CheckListDemo extends BorderPage{

    private static final List STANDARD_OPTIONS;
    private static final List SORTABLE_OPTIONS = new ArrayList();

    static {
        List list = new ArrayList();
        for (int i = 1; i <= 4; i++) {
            list.add(new Option(Integer.toString(i),
                     "Tutam gallia deviso est in partes " + i));
        }
        STANDARD_OPTIONS = Collections.unmodifiableList(list);

        for(int i = 1; i <= 6; i++) {
            SORTABLE_OPTIONS.add(new Option(Integer.toString(i),
                                 "Drag to sort me " + i));
        }
    }

    private Form form;
    private CheckList standardCheckList;
    private CheckList sortableCheckList;

    public CheckListDemo() {
        form = new Form("form");
        form.setFieldStyle("width:20em;");

        standardCheckList = new CheckList("standardList", "Standard List ", true);
        standardCheckList.setOptionList(STANDARD_OPTIONS);
        form.add(standardCheckList);

        sortableCheckList = new CheckList("sortableList", "Sortable List ");
        sortableCheckList.setOptionList(SORTABLE_OPTIONS);
        sortableCheckList.setAttribute("class", "cl2");
        sortableCheckList.setSortable(true);
        sortableCheckList.addStyle("width: 100%;");
        form.add(sortableCheckList);

        form.add(new Submit("ok", "   OK   ",  this, "onOkClick"));
        form.add(new PageSubmit("cancel", HomePage.class));

        addControl(form);
    }

    public boolean onOkClick() {
        if (form.isValid()) {
            addModel("showSelected", Boolean.TRUE);
            addModel("list1", standardCheckList.getValues());
            addModel("list2", sortableCheckList.getValues());

            // Use the sort out of the list2
            List order = sortableCheckList.getSortorder();
            addModel("list2Order", order);
        }

        return true;
    }

}
