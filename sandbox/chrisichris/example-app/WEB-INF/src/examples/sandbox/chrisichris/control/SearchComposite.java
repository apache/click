package examples.sandbox.chrisichris.control;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import examples.domain.Customer;
import examples.domain.CustomerDAO;
import net.sf.click.control.Form;
import net.sf.click.control.Submit;
import net.sf.click.control.TextField;
import net.sf.click.sandbox.chrisichris.control.BaseForm;
import net.sf.click.sandbox.chrisichris.control.Composite;

public class SearchComposite extends Composite {

    private TextField searchText = new TextField("search","Search:",true); 
    private Form form;
    private SearchResultComposite searchResult = new SearchResultComposite("searchResult");
    public SearchComposite() {
        super();
    }

    public SearchComposite(String name) {
        super(name);
    }
    
    protected void onInit() {
        super.onInit();
        form = new BaseForm("form");
        form.add(searchText);
        form.add(new Submit("search-sub","Search:",this,"onSearchClicked"));
        addControl(form);
        
        addControl(searchResult);
    }
    
    public boolean onSearchClicked() {
        if(form.isValid()) {
            String txt = searchText.getValue();
            searchResult.setSearchString(txt);
            return false;
        }
        return true;
    }

}
