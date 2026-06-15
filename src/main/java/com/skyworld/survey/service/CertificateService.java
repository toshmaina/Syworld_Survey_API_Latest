package com.skyworld.survey.service;

import com.skyworld.survey.entity.Certificate;
import com.skyworld.survey.exception.ResourceNotFoundException;
import com.skyworld.survey.repository.CertificateRepository;
import com.skyworld.survey.util.FileStorageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.nio.file.Path;

@Service
@RequiredArgsConstructor
public class CertificateService {

    private final CertificateRepository certificateRepository;
    private final FileStorageUtil       fileStorageUtil;

    public record CertificateFile(Resource resource, String originalFilename) {}

    public CertificateFile download(Long id) throws MalformedURLException {
        Certificate cert = certificateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Certificate not found with id: " + id));

        Path filePath = fileStorageUtil.resolve(cert.getStoredFilename());
        Resource resource = new UrlResource(filePath.toUri());

        if (!resource.exists() || !resource.isReadable()) {
            throw new ResourceNotFoundException(
                    "File for certificate " + id + " could not be read from disk");
        }

        return new CertificateFile(resource, cert.getOriginalFilename());
    }
}
