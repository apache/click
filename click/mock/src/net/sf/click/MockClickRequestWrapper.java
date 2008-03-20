package net.sf.click;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import net.sf.click.util.FileUploadService;

/**
 * Mock implementation of {@link net.sf.click.ClickRequestWrapper}.
 *
 * @author Bob Schellink
 */
class MockClickRequestWrapper extends ClickRequestWrapper {

    /**
     * Default constructor.
     *
     * @param request servlet request
     * @param fileUploadService the Commons FileUpload service instance
     */
    public MockClickRequestWrapper(HttpServletRequest request, FileUploadService fileUploadService) {
        super(request, fileUploadService);
    }

    /**
     * Return the original request instead of multipartParameterMap.
     *
     * @return the original request parameter map.
     */
    Map getMultipartParameterMap() {
        // Return the original request map instead of the multipartParamterMap.
        // This enables users to use request.setParameter(String) transparently.
        return getRequest().getParameterMap();
    }
}
