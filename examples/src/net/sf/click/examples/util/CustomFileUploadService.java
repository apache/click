package net.sf.click.examples.util;

import javax.servlet.http.HttpServletRequest;
import net.sf.click.util.FileUploadService;
import org.apache.commons.fileupload.FileUploadBase;

/**
 * Custom FileUploadService that specifies various upload sizes.
 *
 * @author Bob Schellink
 */
public class CustomFileUploadService extends FileUploadService {

    public FileUploadBase createFileUpload(HttpServletRequest request) {
        FileUploadBase fileUploadBase = super.createFileUpload(request);

        //Set total request maximum size to 5meg
        fileUploadBase.setSizeMax(5000000);

        //Set file maximum size to 1meg
        fileUploadBase.setFileSizeMax(1000000);
        return fileUploadBase;
    }
}
