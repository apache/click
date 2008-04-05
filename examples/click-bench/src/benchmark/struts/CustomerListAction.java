package benchmark.struts;

import benchmark.dao.CustomerDao;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForward;

/**
 *
 * @author Bob Schellink
 */
public class CustomerListAction extends Action {
    
    private final static String SUCCESS = "success";
    
    public ActionForward execute(ActionMapping mapping, ActionForm  form,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        
        request.setAttribute("customers", CustomerDao.getInstance().findAll());
        
        return mapping.findForward(SUCCESS);
        
    }
}
