package org.apache.click.fileupload;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import org.apache.commons.fileupload.FileItem;

public class MockFileItem implements FileItem {

	private static final long serialVersionUID = 1L;

	@Override
	public void delete() {
	}

	@Override
	public byte[] get() {
		return null;
	}

	@Override
	public String getContentType() {
		return null;
	}

	@Override
	public String getFieldName() {
		return null;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return null;
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		return null;
	}

	@Override
	public long getSize() {
		return 0;
	}

	@Override
	public String getString() {
		return null;
	}

	@Override
	public String getString(String arg0) throws UnsupportedEncodingException {
		return null;
	}

	@Override
	public boolean isFormField() {
		return false;
	}

	@Override
	public boolean isInMemory() {
		return false;
	}

	@Override
	public void setFieldName(String arg0) {
	}

	@Override
	public void setFormField(boolean arg0) {
	}

	@Override
	public void write(File arg0) throws Exception {
	}

}
