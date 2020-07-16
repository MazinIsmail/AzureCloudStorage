package com.azureCloudStorage.learnings.model;

public class BlobMove {
	
	private String srcBlobName;
	private String containerName;
	private String destContainerName;
	private String destBlobName;
	public String getSrcBlobName() {
		return srcBlobName;
	}
	public void setSrcBlobName(String srcBlobName) {
		this.srcBlobName = srcBlobName;
	}
	public String getContainerName() {
		return containerName;
	}
	public void setContainerName(String containerName) {
		this.containerName = containerName;
	}
	public String getDestContainerName() {
		return destContainerName;
	}
	public void setDestContainerName(String destContainerName) {
		this.destContainerName = destContainerName;
	}
	public String getDestBlobName() {
		return destBlobName;
	}
	public void setDestBlobName(String destBlobName) {
		this.destBlobName = destBlobName;
	}
	@Override
	public String toString() {
		return "BlobMove [srcBlobName=" + srcBlobName + ", containerName="
				+ containerName + ", destContainerName=" + destContainerName
				+ ", destBlobName=" + destBlobName + "]";
	}

}
