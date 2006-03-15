package examples.sandbox.chrisichris.page.prototype;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import examples.domain.Customer;
import examples.domain.CustomerDAO;
import net.sf.click.Context;
import net.sf.click.Page;
import net.sf.click.control.ActionLink;
import net.sf.click.control.Form;
import net.sf.click.control.Submit;
import net.sf.click.control.TextField;
import net.sf.click.sandbox.chrisichris.prototype.AjaxAction;
import net.sf.click.sandbox.chrisichris.prototype.AjaxPage;
import net.sf.click.util.ClickUtils;

/**
 * A normal page which demonstrates the use of actions. There are three actions one for feeding an autocomplete
 * textfield the other to demonstrate a dynamic image and a third which demonstrates a link which displays a customers.
 * renders a special value.
 * @author chris
 *
 */
public class PlainAjaxActionPage extends AjaxPage {

    /**
     * Action for autocomplete which retruns the customer names
     */
    public static final AjaxAction sampleAction = new AjaxAction() {

        public void doExecute(Page page) {
            String result = "sampleAction executed: <br/>";
            result += "Page class: "+page.getClass();
            result +="<br/>Id RequestParam: "+page.getContext().getRequestParameter("id");
            page.addModel("result",result);
        }
    };

    public static final AjaxAction sampleAction2 = new AjaxAction() {

        public void doExecute(Page page) {
            String result = "sampleAction2 executed: <br/>";
            result += "Page class: "+page.getClass();
            result +="<br/>name RequestParam: "+page.getContext().getRequestParameter("name");
            page.addModel("result",result);
        }
    };
    
    static {
        AjaxAction.createActionMap(PlainAjaxActionPage.class);
    }
    
    public PlainAjaxActionPage() {
        super();
    }

    public void onInitAlways() {
        addModel("actionUrl1",sampleAction.getUrl(this.getContext()));
        addModel("onInitAlways",Boolean.TRUE);
    }
    
    public void onInitPage() {
        addModel("onInitPage",Boolean.TRUE);
        
        ActionLink al = new ProcessActionLink("actionLink",this,"onActionLinkClicked");
        al.setLabel("Execute ActionLink");
        addControl(al);
    }
    
    public void onGet() {
        addModel("onGet",Boolean.TRUE);
    }
    
    public boolean onActionLinkClicked(){
        addModel("result","ActionLink was clicked");
        return true;
    }
    
    /**
     * Only overriden to indicate wheter it was processed at all or not 
     */
    public class ProcessActionLink extends ActionLink {

        public ProcessActionLink(String name,Object listener,String method) {
            super(name,listener,method);
        }
        
        public boolean onProcess() {
            PlainAjaxActionPage.this.addModel("onActionLinkProcess",Boolean.TRUE);
            return super.onProcess();
        }
        
    }

}
