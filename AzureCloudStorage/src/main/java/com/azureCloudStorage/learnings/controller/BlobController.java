package com.azureCloudStorage.learnings.controller;

import java.net.URI;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.azureCloudStorage.learnings.model.BlobMove;
import com.azureCloudStorage.learnings.model.FileDetails;
import com.azureCloudStorage.learnings.service.BlobService;
import com.azureCloudStorage.learnings.service.ContainerService;

@RestController
@RequestMapping(value = "/blob")
public class BlobController {

	private static final Logger logger = LoggerFactory.getLogger(BlobController.class);

	@Autowired
	private BlobService blobService;

	@Autowired
	private ContainerService containerService;
	
	@GetMapping("/")
	public ResponseEntity<?> getBlob(@RequestParam String containerName, @RequestParam String blobName) {
		URI uri = blobService.download(containerName, blobName);
		return ResponseEntity.ok(uri);
	}

	@DeleteMapping("/")
	public ResponseEntity<?> deleteBlob(@RequestParam String containerName, @RequestParam String blobName) {
		blobService.deleteBlob(containerName, blobName);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/all")
	public ResponseEntity<?> deleteAllBlob(@RequestParam String containerName, @RequestParam String subDirectoryName) {
		containerService.clearSubDirectory(containerName, subDirectoryName);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/{containerName}")
	public ResponseEntity<?> getAllBlobs(@PathVariable("containerName") String containerName) {
		List<?> uris = blobService.listFileDetails(containerName);
		return ResponseEntity.ok(uris);
	}

	@GetMapping("/{containerName}/{subdirectory}")
	public ResponseEntity<?> getAllBlobsFromSubDirectory(@PathVariable("containerName") String containerName,
			@PathVariable("subdirectory") String subDirectoryName) {
		List<FileDetails> uris = blobService.getDirectoryListDetails(containerName, subDirectoryName);
		return ResponseEntity.ok(uris);
	}

	@PostMapping("/move")
	public ResponseEntity<URI> moveBlobToOneToAnotherContainerWithSubDir(@RequestBody BlobMove moveFile) {
		URI status = blobService.moveBlobFromOneContainerToAnother(moveFile.getSrcBlobName(),
				moveFile.getContainerName(), moveFile.getDestContainerName(), moveFile.getDestBlobName());
		return ResponseEntity.ok(status);
	}

}
