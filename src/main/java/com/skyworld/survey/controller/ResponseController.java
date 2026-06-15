package com.skyworld.survey.controller;

import com.skyworld.survey.dto.ResponseListDto;
import com.skyworld.survey.service.ResponseService;
import com.skyworld.survey.util.XmlResponseSerializer;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/api/surveys/{surveyId}/responses")
@RequiredArgsConstructor
public class ResponseController {

    private final ResponseService responseService;

    /**
     * POST /api/surveys/{surveyId}/responses
     * Accepts multipart/form-data — text fields + PDF files.
     */
    @PostMapping(
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_XML_VALUE
    )
    public ResponseEntity<String> submit(
            @PathVariable Long surveyId,
            HttpServletRequest request) throws Exception {

        log.debug(">>> Submit response for survey {} | Content-Type: {}",
                surveyId, request.getContentType());

        // Parse multipart manually to handle React Native boundary quirks
        MultipartHttpServletRequest multipartRequest;
        try {
            StandardServletMultipartResolver resolver = new StandardServletMultipartResolver();
            multipartRequest = resolver.resolveMultipart(request);
        } catch (Exception e) {
            log.error(">>> Failed to parse multipart request: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.APPLICATION_XML)
                    .body("""
                          <?xml version="1.0" encoding="UTF-8"?>
                          <error>
                            <code>400</code>
                            <message>Invalid multipart request: %s</message>
                          </error>
                          """.formatted(e.getMessage()));
        }

        // Collect text fields — join multi-value params with comma
        Map<String, String> fields = new LinkedHashMap<>();
        multipartRequest.getParameterMap().forEach((key, values) -> {
            if (values != null && values.length > 0) {
                fields.put(key, String.join(",", values));
            }
        });

        // Collect file fields
        Map<String, List<MultipartFile>> files = new LinkedHashMap<>();
        multipartRequest.getMultiFileMap().forEach((key, fileList) ->
                files.put(key, fileList.stream()
                        .filter(f -> !f.isEmpty()).toList()));

        log.debug(">>> Fields: {} | File fields: {}", fields.keySet(), files.keySet());
        files.forEach((key, fileList) ->
                fileList.forEach(f -> log.debug(">>> File: {} | size: {} bytes | type: {}",
                        f.getOriginalFilename(), f.getSize(), f.getContentType())));

        var dto = responseService.submit(surveyId, fields, files);
        String xml = XmlResponseSerializer.serializeResponse(dto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_XML)
                .body(xml);
    }

    /**
     * GET /api/surveys/{surveyId}/responses?page=1&pageSize=10&email=...
     */
    @GetMapping(produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> findAll(
            @PathVariable Long surveyId,
            @RequestParam(defaultValue = "1")  int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false)    String email) throws Exception {

        ResponseListDto listDto = responseService.findAll(surveyId, page, pageSize, email);

        String xml = XmlResponseSerializer.serializeResponseList(
                listDto.getResponses(),
                listDto.getCurrentPage(),
                listDto.getLastPage(),
                listDto.getPageSize(),
                listDto.getTotalCount());

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_XML)
                .body(xml);
    }
}