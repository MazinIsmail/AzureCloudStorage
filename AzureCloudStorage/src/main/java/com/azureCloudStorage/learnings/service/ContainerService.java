package com.azureCloudStorage.learnings.service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.azureCloudStorage.learnings.exception.AzureStorageException;
import com.microsoft.azure.storage.OperationContext;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.BlobContainerPublicAccessType;
import com.microsoft.azure.storage.blob.BlobRequestOptions;
import com.microsoft.azure.storage.blob.CloudBlob;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlobDirectory;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import com.microsoft.azure.storage.blob.ListBlobItem;

@Service
public class ContainerService {

	private static final Logger logger = LoggerFactory.getLogger(ContainerService.class);
	@Autowired
	private CloudBlobClient cloudBlobClient;

	public boolean createContainer(String containerName) {
		logger.info("In createContainer()");
		boolean containerCreated = false;
		CloudBlobContainer container = null;
		try {
			container = cloudBlobClient.getContainerReference(containerName);
		} catch (URISyntaxException e) {
			logger.error(e.getMessage());
			logger.error("Error!!!,CAUSE:{} {}", e, e.getStackTrace());
		} catch (StorageException e) {
			logger.error(e.getMessage());
			logger.error("Error!!!,CAUSE:{} {}", e, e.getStackTrace());
		}
		try {
			if (container != null) {
				containerCreated = container.createIfNotExists(BlobContainerPublicAccessType.CONTAINER,
						new BlobRequestOptions(), new OperationContext());
			}
		} catch (StorageException e) {
			logger.error(e.getMessage());
			logger.error("Error!!!,CAUSE:{} {}", e, e.getStackTrace());
		}
		return containerCreated;
	}

	public URI uploadSingleBlobToContainer(MultipartFile multipartFile, String containerName) {
		logger.info("In uploadToContainer()");
		URI uri = null;
		CloudBlockBlob blob = null;
		CloudBlobContainer cloudBlobContainer = null;
		try {
			cloudBlobContainer = cloudBlobClient.getContainerReference(containerName);
			cloudBlobContainer.createIfNotExists();
			blob = cloudBlobContainer.getBlockBlobReference(multipartFile.getOriginalFilename());
			blob.upload(multipartFile.getInputStream(), -1);
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

	public List<URI> uploadMultipleBlobToContainer(MultipartFile[] multipartFiles, String containerName) {
		List<URI> urlList = new ArrayList<>();
		for (MultipartFile multipartFile : multipartFiles) {
			URI url = uploadSingleBlobToContainer(multipartFile, containerName);
			urlList.add(url);
		}
		return urlList;
	}

	public URI upload(MultipartFile multipartFile, String containerName, String subDirectoryName) {
		logger.debug("Start : In upload()");
		URI uri = null;
		CloudBlockBlob blob = null;
		CloudBlobContainer cloudBlobContainer = null;
		try {
			cloudBlobContainer = cloudBlobClient.getContainerReference(containerName);
			blob = cloudBlobContainer
					.getBlockBlobReference(subDirectoryName + "/" + multipartFile.getOriginalFilename());
			blob.upload(multipartFile.getInputStream(), -1);
			uri = blob.getUri();
		} catch (URISyntaxException e) {
			throw new AzureStorageException("URI Syntax Exception");
		} catch (StorageException e) {
			throw new AzureStorageException("Storage Exception");
		} catch (IOException e) {
			throw new AzureStorageException("IO Exception while upload");
		}
		logger.debug("End : In upload()");
		return uri;
	}

	public URI uploadSingleBlobToSubDirectory(MultipartFile multipartFile, String containerName,
			String subDirectoryName) {
		logger.info("uploadToSubDirectory()");
		URI uri = null;
		CloudBlockBlob blob = null;
		CloudBlobContainer cloudBlobContainer = null;
		try {
			cloudBlobContainer = cloudBlobClient.getContainerReference(containerName);
			blob = cloudBlobContainer
					.getBlockBlobReference(subDirectoryName + "/" + multipartFile.getOriginalFilename());
			blob.upload(multipartFile.getInputStream(), -1);
			logger.info("Blob {} uploaded", blob.getName());
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

	public List<URI> uploadMultipleBlobToSubDirectory(MultipartFile[] multipartFiles, String containerName,
			String subDirectoryName) {
		List<URI> urlList = new ArrayList<>();
		for (MultipartFile multipartFile : multipartFiles) {
			URI url = uploadSingleBlobToSubDirectory(multipartFile, containerName, subDirectoryName);
			urlList.add(url);
		}
		return urlList;
	}

	public List<String> getBlobList(String containerName, String subDirectoryName) {
		logger.info("In getDirectoryList()");
		CloudBlobContainer container = null;
		CloudBlobDirectory subDirectory = null;
		List<String> blobList = new ArrayList<>();
		try {
			container = cloudBlobClient.getContainerReference(containerName);
			logger.info("Container Name:{}", container.getName());
			logger.info("Is Container Exists:{}", container.exists());

			subDirectory = container.getDirectoryReference(subDirectoryName);
			logger.info("Sub Directory Path:{}", subDirectory.getPrefix());

			for (ListBlobItem blobItem : subDirectory.listBlobs()) {
				blobList.add(((CloudBlob) blobItem).getName());
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
		return blobList;

	}

	public void deleteContainer(String containerName) {
		logger.info("In deleteContainer()");
		try {
			CloudBlobContainer container = cloudBlobClient.getContainerReference(containerName);

			container.deleteIfExists();
		} catch (Exception e) {
			logger.error("Error!!!,CAUSE:{} {}", e, e.getStackTrace());
		}
	}

	public List<String> listContainers() {
		logger.info("In listContainers()");
		List<String> containers = new ArrayList<>();
		try {
			Iterable<CloudBlobContainer> containerRefs = cloudBlobClient.listContainers();
			if (containerRefs != null) {
				for (CloudBlobContainer container : cloudBlobClient.listContainers()) {
					containers.add(container.getName());
				}
			}

		} catch (Exception e) {
			logger.error("Error!!!,CAUSE:{} {}", e, e.getStackTrace());
		}
		return containers;
	}

	public List<String> getListSubDirectories(String containerName) {
		List<String> subDirectoryList = new ArrayList<>();
		try {
			CloudBlobContainer myCloudBlobContainer = cloudBlobClient.getContainerReference(containerName);
			Iterable<ListBlobItem> blobs = myCloudBlobContainer.listBlobs();
			for (ListBlobItem blob : blobs) {
				if (blob instanceof CloudBlobDirectory) {
					CloudBlobDirectory directory = (CloudBlobDirectory) blob;
					if (!subDirectoryList.contains(directory.getPrefix())) {
						subDirectoryList.add(directory.getPrefix());
					}
				}
			}
		} catch (URISyntaxException e) {
			logger.error("Error!!!,CAUSE:{} {}", e, e.getStackTrace());
		} catch (StorageException e) {
			logger.error("Error!!!,CAUSE:{} {}", e, e.getStackTrace());
		} catch (Exception e) {
			logger.error("Error!!!,CAUSE:{} {}", e, e.getStackTrace());
		}
		return subDirectoryList;
	}

	public List<String> getContainerBlobs(String containerName) {
		List<String> containerBlobList = new ArrayList<>();
		try {
			CloudBlobContainer myCloudBlobContainer = cloudBlobClient.getContainerReference(containerName);
			Iterable<ListBlobItem> blobs = myCloudBlobContainer.listBlobs();
			recursiveBlobs(containerBlobList, blobs);
		} catch (URISyntaxException e) {
			logger.error("Error!!!,CAUSE:{} {}", e, e.getStackTrace());
		} catch (StorageException e) {
			logger.error("Error!!!,CAUSE:{} {}", e, e.getStackTrace());
		} catch (Exception e) {
			logger.error("Error!!!,CAUSE:{} {}", e, e.getStackTrace());
		}
		return containerBlobList;
	}

	private void recursiveBlobs(List<String> containerBlobList, Iterable<ListBlobItem> blobs)
			throws StorageException, URISyntaxException {
		for (ListBlobItem blob : blobs) {
			if (blob instanceof CloudBlobDirectory) {
				CloudBlobDirectory directory = (CloudBlobDirectory) blob;
				Iterable<ListBlobItem> blobList = directory.listBlobs();
				for (ListBlobItem blobNew : blobList) {
					if (blobNew instanceof CloudBlob) {
						CloudBlob cloudBlob = (CloudBlob) blobNew;
						containerBlobList.add(cloudBlob.getName());
					} else if (blobNew instanceof CloudBlobDirectory) {
						CloudBlobDirectory directoryNew = (CloudBlobDirectory) blobNew;
						Iterable<ListBlobItem> blobListNew = directoryNew.listBlobs();
						recursiveBlobs(containerBlobList, blobListNew);
					}
				}
			} else {
				containerBlobList.add(((CloudBlob) blob).getName());
			}
		}
	}
}
