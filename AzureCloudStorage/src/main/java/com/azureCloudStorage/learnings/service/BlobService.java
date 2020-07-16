package com.azureCloudStorage.learnings.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.azureCloudStorage.learnings.exception.AzureStorageException;
import com.azureCloudStorage.learnings.model.FileDetails;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.BlobProperties;
import com.microsoft.azure.storage.blob.CloudBlob;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlobDirectory;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import com.microsoft.azure.storage.blob.ListBlobItem;

@Service
public class BlobService {

	private static final Logger logger = LoggerFactory.getLogger(BlobService.class);
	@Autowired
	private CloudBlobClient cloudBlobClient;

	public List<URI> listBlobs(String containerName) {
		logger.debug("Start : In listBlobs()");
		List<URI> uris = new ArrayList<>();
		try {
			CloudBlobContainer container = cloudBlobClient.getContainerReference(containerName);
			for (ListBlobItem blobItem : container.listBlobs("", true)) {
				if (StringUtils.isEmpty(blobItem.getParent().getPrefix())) {
					uris.add(blobItem.getUri());
					CloudBlob blob = (CloudBlob) blobItem;
					BlobProperties prop = blob.getProperties();
					logger.info("the pro {}", prop.getLastModified());
					logger.info("the create {}", prop.getCreatedTime());
					logger.info("storage per .. {}", blobItem.getContainer().getProperties());
				}

			}
		} catch (URISyntaxException e) {
			throw new AzureStorageException("URI Syntax Exception");
		} catch (StorageException e) {
			throw new AzureStorageException("Storage Exception");
		} catch (Exception e) {
			throw new AzureStorageException("Exception while download");
		}
		logger.debug("End : In listBlobs()");
		return uris;
	}

	public boolean deleteBlob(String containerName, String blobName) {
		logger.info("In deleteBlob()");
		boolean flag = false;
		try {
			CloudBlobContainer container = cloudBlobClient.getContainerReference(containerName);
			CloudBlockBlob blobToBeDeleted = container.getBlockBlobReference(blobName);
			flag = blobToBeDeleted.deleteIfExists();
		} catch (URISyntaxException e) {
			logger.error("Error!!!,CAUSE:{} {}", e, e.getStackTrace());
		} catch (StorageException e) {
			logger.error("Error!!!,CAUSE:{} {}", e, e.getStackTrace());
		}
		return flag;
	}

	public List<FileDetails> listFileDetails(String containerName) {
		logger.info("In listBlobs()");
		List<FileDetails> uris = new ArrayList<>();
		try {
			CloudBlobContainer container = cloudBlobClient.getContainerReference(containerName);
			for (ListBlobItem blobItem : container.listBlobs("", true)) {
				if (StringUtils.isEmpty(blobItem.getParent().getPrefix())) {
					FileDetails fileDetails = new FileDetails();
					CloudBlob blob = (CloudBlob) blobItem;
					BlobProperties prop = blob.getProperties();
					fileDetails.setFileUrl(blobItem.getUri().toString());
					fileDetails.setFileDate(prop.getLastModified().toString());
					uris.add(fileDetails);
					logger.info("the pro {}", prop.getLastModified());
					logger.info("the create {}", prop.getCreatedTime());
					logger.info("storage per .. {}", blobItem.getContainer().getProperties());
				}

			}
		} catch (URISyntaxException e) {
			logger.error("Error!!!,CAUSE:{} {}", e, e.getStackTrace());
		} catch (StorageException e) {
			logger.error("Error!!!,CAUSE:{} {}", e, e.getStackTrace());
		} catch (Exception e) {
			logger.error("Error!!!,CAUSE:{} {}", e, e.getStackTrace());
		}
		return uris;
	}

	public List<FileDetails> getDirectoryListDetails(String directoryName, String subDirectoryName) {
		logger.info("In getDirectoryListDetails()");
		CloudBlobContainer container = null;
		CloudBlobDirectory subDirectory = null;
		// List<URI> uris = new ArrayList<>();
		List<FileDetails> uris = new ArrayList<>();
		try {
			container = cloudBlobClient.getContainerReference(directoryName);
			logger.info("Container Name:{}", container.getName());
			logger.info("Is Container Exists:{}", container.exists());

			subDirectory = container.getDirectoryReference(subDirectoryName);
			logger.info("Sub Directory Path:{}", subDirectory.getPrefix());
			logger.info("Subdirectory size : {}", subDirectory.listBlobs());
			for (ListBlobItem blobItem : subDirectory.listBlobs()) {
				FileDetails fileDetails = new FileDetails();
				CloudBlob blob = (CloudBlob) blobItem;
				BlobProperties prop = blob.getProperties();
				fileDetails.setFileUrl(blobItem.getUri().toString());
				fileDetails.setFileDate(prop.getLastModified().toString());
				// uris.add(blobItem.getUri());
				uris.add(fileDetails);
				logger.info("the pro {}", prop.getLastModified());
				logger.info("the create {}", prop.getCreatedTime());
				logger.info("storage per .. {}", blobItem.getContainer().getProperties());
			}
			// Iterable<ListBlobItem> blobs = subDirectory.listBlobs();
			// while (blobs.iterator().hasNext()) {
			// ListBlobItem blob = blobs.iterator().next();
			// uris.add(blob.getUri());
			// }
		} catch (URISyntaxException e) {
			logger.error("Error!!!,CAUSE:{} {}", e, e.getStackTrace());
		} catch (StorageException e) {
			logger.error("Error!!!,CAUSE:{} {}", e, e.getStackTrace());
		} catch (Exception e) {
			logger.error("Error!!!,CAUSE:{} {}", e, e.getStackTrace());
		}
		return uris;

	}

	public URI moveBlobFromOneContainerToAnother(String srcBlobName, String containerName, String destContainerName,
			String destBlobName) {
		logger.info("In moveBlobFromOneContainerToAnother()");

		CloudBlockBlob destBlob = null;
		try {
			CloudBlobContainer srcContainer = cloudBlobClient.getContainerReference(containerName);
			CloudBlobContainer destContainer = cloudBlobClient.getContainerReference(destContainerName);
			CloudBlockBlob srcBlob = srcContainer.getBlockBlobReference(srcBlobName);
			destBlob = destContainer.getBlockBlobReference(destBlobName);
			destBlob.startCopy(srcBlob);
			srcBlob.deleteIfExists();

		} catch (URISyntaxException | StorageException e) {
			logger.error("Error!!!,CAUSE:{} {}", e, e.getStackTrace());
		}
		return destBlob.getUri();
	}

	public URI download(String containerName, String blobName) {
		logger.info("In download()");
		URI uri = null;
		CloudBlockBlob blob = null;
		CloudBlobContainer cloudBlobContainer = null;
		try (ByteArrayOutputStream outStream = new ByteArrayOutputStream()) {
			cloudBlobContainer = cloudBlobClient.getContainerReference(containerName);
			blob = cloudBlobContainer.getBlockBlobReference(blobName);
			// blob.download(outStream);
			uri = blob.getUri();

		} catch (URISyntaxException e) {
			logger.error("Error!!!,CAUSE:{} {}", e, e.getStackTrace());
		} catch (StorageException e) {
			logger.error("Error!!!,CAUSE:{} {}", e, e.getStackTrace());
		} catch (IOException e) {
			logger.error("Error!!!,CAUSE:{} {}", e, e.getStackTrace());
		}
		return uri;
	}

}
