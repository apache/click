package net.sf.clickide.core.validator;

import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.clickide.ClickPlugin;

import org.apache.velocity.runtime.RuntimeInstance;
import org.apache.velocity.runtime.parser.ParseException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.validation.internal.core.ValidationException;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.eclipse.wst.validation.internal.provisional.core.IValidationContext;
import org.eclipse.wst.validation.internal.provisional.core.IValidator;

/**
 * The <code>IValidator</code> implementation for Click Page Templates.
 * <p>
 * This class provides Velocity syntax checking.
 * 
 * @author Naoki Takezoe
 */
public class TemplateValidator implements IValidator {
	
	private static Pattern PATTERN = Pattern.compile(" at line ([0-9]+?),");
	
	public void cleanup(IReporter reporter) {
	}

	public void validate(IValidationContext helper, IReporter reporter) throws ValidationException {
		String[] uris = helper.getURIs();
		reporter.removeAllMessages(this);
		if (uris != null) {
			for (int i = 0; i < uris.length && !reporter.isCancelled(); i++) {
				IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(uris[i]));
				try {
					try {
						RuntimeInstance runtime = new RuntimeInstance();
						runtime.init();
						runtime.parse(new InputStreamReader(file.getContents(), file.getCharset()), file.getName());
						
					} catch(ParseException ex){
						Matcher matcher = PATTERN.matcher(ex.getMessage());
						if(matcher.find()){
							ValidationDocument doc = new ValidationDocument(file);
							int line = Integer.parseInt(matcher.group(1));
							int offset = doc.getOffsetByLine(line - 1);
							int length = doc.getLineLength(line - 1);
							
							String text = ex.getMessage();
							text = text.replaceAll("[ \t\r\n]+", " ");
							
							ValidatorUtils.createWarningMessage(this, reporter, file,
									"velocityParseError", new String[]{text}, offset, length, line);
						}
					}
				} catch(Exception ex){
					ClickPlugin.log(ex);
				}
			}
		}
	}

}
