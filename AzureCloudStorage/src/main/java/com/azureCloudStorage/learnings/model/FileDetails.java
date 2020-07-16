/**
 * 
 */
package com.azureCloudStorage.learnings.model;


/**
 * @author VI362774
 *
 */
public class FileDetails {
	
	private String fileUrl;

	private String fileDate;
	
	public String getFileUrl() {
		return fileUrl;
	}

	public void setFileUrl(String fileUrl) {
		this.fileUrl = fileUrl;
	}

	public String getFileDate() {
		return fileDate;
	}

	public void setFileDate(String fileDate) {
		this.fileDate = fileDate;
	}

	@Override
	public String toString() {
		return "FileDetails [fileUrl=" + fileUrl + ", fileDate=" + fileDate + "]";
	}
	
}
