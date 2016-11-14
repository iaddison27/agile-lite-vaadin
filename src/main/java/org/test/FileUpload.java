package org.test;

import java.io.ByteArrayOutputStream;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class FileUpload {

	private String filename;
	
	private ByteArrayOutputStream file;

	public FileUpload() {
		super();
	}

	public FileUpload(String filename, ByteArrayOutputStream file) {
		super();
		this.filename = filename;
		this.file = file;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public ByteArrayOutputStream getFile() {
		return file;
	}

	public void setFile(ByteArrayOutputStream file) {
		this.file = file;
	}
	
	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}
	
	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}
	
}
