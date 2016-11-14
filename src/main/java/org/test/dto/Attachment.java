package org.test.dto;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Version;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

@Entity
@Table(name="ATTACHMENT")
public class Attachment {

	@Id
    @Column(name="ATTACHMENT_ID")
    @GeneratedValue
	private Long id;
	
	@Version
	@Column(name="VERSION")
	private Long version;
	
	@Column(name="FILENAME")
	private String filename;
	
	// Enable this after this bug is fixed: https://hibernate.atlassian.net/browse/HHH-11173
	//@Basic(fetch = FetchType.LAZY)
	@Column(name="BYTES")
	@Lob
	private byte[] file;

	public Attachment() {
		super();
	}

	public Attachment(String filename, byte[] file) {
		super();
		this.filename = filename;
		this.file = file;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public byte[] getFile() {
		return file;
	}

	public void setFile(byte[] file) {
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
