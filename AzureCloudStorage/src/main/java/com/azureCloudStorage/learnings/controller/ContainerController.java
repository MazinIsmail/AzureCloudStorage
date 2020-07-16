package com.azureCloudStorage.learnings.controller;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotEmpty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.azureCloudStorage.learnings.model.FileDetails;
import com.azureCloudStorage.learnings.service.BlobService;
import com.azureCloudStorage.learnings.service.ContainerService;

@RestController
@RequestMapping(value = "/container")
public class ContainerController {

	private static final Logger logger = LoggerFactory.getLogger(ContainerController.class);

	@Autowired
	private ContainerService containerService;

	@Autowired
	private BlobService blobService;

	@GetMapping("/")
	public ResponseEntity<?> getAllContainers() {
		List<String> containers = containerService.listContainers();
		return ResponseEntity.ok(containers);
	}

	@PostMapping("/")
	public ResponseEntity<?> createContainer(@RequestParam("containerName") @NotEmpty String containerName) {
		boolean created = containerService.createContainer(containerName);
		return ResponseEntity.ok(created);
	}

	@DeleteMapping("/")
	public ResponseEntity<?> deleteContainer(@RequestParam @NotEmpty String containerName) {
		containerService.deleteContainer(containerName);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/uploadfiles/container")
	public ResponseEntity<List<URI>> uploadFilesToContainer(@RequestParam("files") @NotEmpty MultipartFile[] multipartFiles,
			@RequestParam("containerName") @NotEmpty String containerName) {
		logger.info("In uploadFilesToContainer() ");
		List<URI> urlList = new ArrayList<>();
		for (MultipartFile multipartFile : multipartFiles) {
			URI url = containerService.uploadToContainer(multipartFile, containerName);
			urlList.add(url);
		}
		return new ResponseEntity<>(urlList, HttpStatus.OK);
	}

	@PostMapping("/uploadfiles/subdirectory")
	public ResponseEntity<?> uploadToSubDirectory(@RequestParam("files") @NotEmpty MultipartFile[] multipartFiles,
			@RequestParam("containerName")  @NotEmpty String containerName,
			@RequestParam("subDirectoryName") @NotEmpty String subDirectoryName) {
		List<URI> urlList = new ArrayList<>();
		for (MultipartFile multipartFile : multipartFiles) {
			URI url = containerService.uploadToSubDirectory(multipartFile, containerName, subDirectoryName);
			urlList.add(url);
		}
		return ResponseEntity.ok(urlList);
	}

	@DeleteMapping("/clear")
	public ResponseEntity<?> clearContainer(@RequestParam(name = "container") @NotEmpty String container) {

	List<URI> list = blobService.listBlobs(container);
	for(URI uri : list) {
		System.out.println(uri);
	}
		return new ResponseEntity<>("", HttpStatus.OK);
	}
	
	@DeleteMapping("/clear/subdirectory")
	public ResponseEntity<?> clearSubdirectory(@RequestParam(name = "container") @NotEmpty String container,
			@RequestParam(name = "subdirectory") @NotEmpty String subdirectory) {

		String containerName = container.split(File.separator)[0];
		String subDirectoryName = container.split(File.separator)[1];
		if (subDirectoryName.equalsIgnoreCase("All")) {
			containerService.deleteContainer(containerName);
			containerService.createContainer(containerName);
		} else {
			List<FileDetails> fileList = blobService.getDirectoryListDetails(containerName, subDirectoryName);
			for (FileDetails file : fileList) {
				File f = new File(file.getFileUrl());
				blobService.deleteBlob(containerName, subDirectoryName + File.separator + f.getName());
			}
		}
		return new ResponseEntity<>("", HttpStatus.OK);
	}
}
