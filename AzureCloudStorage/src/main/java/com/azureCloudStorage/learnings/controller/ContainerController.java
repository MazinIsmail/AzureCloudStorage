package com.azureCloudStorage.learnings.controller;

import java.net.URI;
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

import com.azureCloudStorage.learnings.service.ContainerService;

@RestController
@RequestMapping(value = "/container")
public class ContainerController {

	private static final Logger logger = LoggerFactory.getLogger(ContainerController.class);

	@Autowired
	private ContainerService containerService;

	@GetMapping("/")
	public ResponseEntity<?> getAllContainers() {
		List<String> containers = containerService.listContainers();
		return ResponseEntity.ok(containers);
	}

	@GetMapping("/blobs")
	public ResponseEntity<?> getAllContainerBlobs(@RequestParam("containerName") @NotEmpty String container) {
		List<String> containerBlobs = containerService.getContainerBlobs(container);
		return ResponseEntity.ok(containerBlobs);
	}

	@GetMapping("/subdirectory")
	public ResponseEntity<?> getAllSubDirectories(@RequestParam("containerName") @NotEmpty String container) {
		List<String> subDirectories = containerService.getListSubDirectories(container);
		return ResponseEntity.ok(subDirectories);
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
	public ResponseEntity<List<URI>> uploadFilesToContainer(
			@RequestParam("files") @NotEmpty MultipartFile[] multipartFiles,
			@RequestParam("containerName") @NotEmpty String containerName) {
		logger.info("In uploadFilesToContainer() ");
		List<URI> urlList = containerService.uploadMultipleBlobToContainer(multipartFiles, containerName);
		return new ResponseEntity<>(urlList, HttpStatus.OK);
	}

	@PostMapping("/uploadfiles/subdirectory")
	public ResponseEntity<?> uploadToSubDirectory(@RequestParam("files") @NotEmpty MultipartFile[] multipartFiles,
			@RequestParam("containerName") @NotEmpty String containerName,
			@RequestParam("subDirectoryName") @NotEmpty String subDirectoryName) {
		List<URI> urlList = containerService.uploadMultipleBlobToSubDirectory(multipartFiles, containerName,
				subDirectoryName);
		return ResponseEntity.ok(urlList);
	}

	@DeleteMapping("/clear")
	public ResponseEntity<?> clearContainer(@RequestParam(name = "containerName") @NotEmpty String container) {
		containerService.clearContainer(container);
		return new ResponseEntity<>("", HttpStatus.OK);
	}

	@DeleteMapping("/clear/subdirectory")
	public ResponseEntity<?> clearSubdirectory(@RequestParam(name = "containerName") @NotEmpty String container,
			@RequestParam(name = "subdirectory") @NotEmpty String subdirectory) {
		containerService.clearSubDirectory(container, subdirectory);
		return new ResponseEntity<>("", HttpStatus.OK);
	}

}
