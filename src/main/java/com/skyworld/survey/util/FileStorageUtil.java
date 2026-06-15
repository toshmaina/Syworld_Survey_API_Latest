package com.skyworld.survey.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Component
public class FileStorageUtil {

    private final Path uploadRoot;

    public FileStorageUtil(@Value("${app.upload.dir:uploads}") String uploadDir) throws IOException {
        this.uploadRoot = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(this.uploadRoot);
    }

    /**
     * Saves a multipart PDF file to disk using a UUID-based filename.
     * Returns the stored filename.
     */
    public String store(MultipartFile file) throws IOException {
        String original = file.getOriginalFilename();
        if (original == null || original.isBlank()) {
            throw new IllegalArgumentException("Uploaded file must have a name");
        }

        String extension = "";
        int dotIndex = original.lastIndexOf('.');
        if (dotIndex >= 0) {
            extension = original.substring(dotIndex).toLowerCase();
        }

        if (!".pdf".equals(extension)) {
            throw new IllegalArgumentException("Only PDF files are accepted. Got: " + extension);
        }

        String storedName = UUID.randomUUID() + extension;
        Path destination = this.uploadRoot.resolve(storedName).normalize();

        // Guard against path traversal attacks
        if (!destination.startsWith(this.uploadRoot)) {
            throw new SecurityException("Cannot store file outside designated upload directory");
        }

        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
        return storedName;
    }

    public Path resolve(String storedFilename) {
        return uploadRoot.resolve(storedFilename).normalize();
    }

    public Path getUploadRoot() {
        return uploadRoot;
    }
}
