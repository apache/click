/*
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
package org.apache.click.examples.page.control;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.click.control.Form;
import org.apache.click.control.Option;
import org.apache.click.control.Submit;
import org.apache.click.examples.page.BorderPage;
import org.apache.click.examples.page.HomePage;
import org.apache.click.extras.control.CheckList;
import org.apache.click.extras.control.PageSubmit;

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

    public  Form form = new Form();

    private CheckList standardCheckList;
    private CheckList sortableCheckList;

    public CheckListDemo() {
        form.setFieldStyle("width:20em;");

        standardCheckList = new CheckList("standardList", "Standard List ", true);
        standardCheckList.setOptionList(STANDARD_OPTIONS);
        form.add(standardCheckList);

        sortableCheckList = new CheckList("sortableList", "Sortable List ");
        sortableCheckList.setOptionList(SORTABLE_OPTIONS);
        sortableCheckList.addStyleClass("cl2");
        sortableCheckList.setSortable(true);
        sortableCheckList.setStyle("width", "100%;");
        form.add(sortableCheckList);

        form.add(new Submit("ok", "  OK  ",  this, "onOkClick"));
        form.add(new PageSubmit("cancel", HomePage.class));
    }

    public boolean onOkClick() {
        if (form.isValid()) {
            addModel("showSelected", Boolean.TRUE);
            addModel("list1", standardCheckList.getSelectedValues());
            addModel("list2", sortableCheckList.getSelectedValues());

            // Use the sort out of the list2
            List order = sortableCheckList.getSortorder();
            addModel("list2Order", order);
        }
        return true;
    }

}
