package com.example.ems.service.impl;

import com.example.ems.config.FileStorageProperties;
import com.example.ems.exception.FileStorageException;
import com.example.ems.exception.ResourceNotFoundException;
import com.example.ems.service.FileStorageService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    private final FileStorageProperties fileStorageProperties;
    private final Path storageLocation;

    public FileStorageServiceImpl(FileStorageProperties fileStorageProperties) {
        this.fileStorageProperties = fileStorageProperties;
        this.storageLocation = Paths.get(fileStorageProperties.getUploadDir()).toAbsolutePath().normalize();
        try {
            Files.createDirectories(storageLocation);
        } catch (IOException ex) {
            throw new FileStorageException("Could not create the upload directory: " + storageLocation, ex);
        }
    }

    @Override
    public String store(MultipartFile file, Long employeeId) {
        if (file == null || file.isEmpty()) {
            throw new FileStorageException("Cannot store an empty file");
        }

        String contentType = file.getContentType();
        if (contentType == null || !fileStorageProperties.getAllowedContentTypesList().contains(contentType)) {
            throw new FileStorageException("Unsupported file type: " + contentType);
        }

        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename() == null ? "" : file.getOriginalFilename());
        if (originalFileName.contains("..")) {
            throw new FileStorageException("Filename contains an invalid path sequence: " + originalFileName);
        }

        String extension = "";
        int dotIndex = originalFileName.lastIndexOf('.');
        if (dotIndex >= 0) {
            extension = originalFileName.substring(dotIndex);
        }
        String storedFileName = "employee-" + employeeId + "-" + UUID.randomUUID() + extension;

        try {
            Path targetLocation = storageLocation.resolve(storedFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return storedFileName;
        } catch (IOException ex) {
            throw new FileStorageException("Failed to store file " + originalFileName, ex);
        }
    }

    @Override
    public Resource load(String fileName) {
        try {
            Path filePath = storageLocation.resolve(fileName).normalize();
            if (!filePath.startsWith(storageLocation)) {
                throw new FileStorageException("Cannot access file outside the storage directory: " + fileName);
            }
            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new ResourceNotFoundException("File not found: " + fileName);
            }
            return resource;
        } catch (MalformedURLException ex) {
            throw new FileStorageException("Failed to load file " + fileName, ex);
        }
    }

    @Override
    public void delete(String fileName) {
        if (!StringUtils.hasText(fileName)) {
            return;
        }
        try {
            Path filePath = storageLocation.resolve(fileName).normalize();
            Files.deleteIfExists(filePath);
        } catch (IOException ex) {
            throw new FileStorageException("Failed to delete file " + fileName, ex);
        }
    }
}
