package com.skyworld.survey.service;

import com.skyworld.survey.dto.*;
import com.skyworld.survey.entity.*;
import com.skyworld.survey.exception.ResourceNotFoundException;
import com.skyworld.survey.repository.*;
import com.skyworld.survey.util.FileStorageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ResponseService {

    private final ResponseRepository    responseRepository;
    private final SurveyRepository      surveyRepository;
    private final QuestionRepository    questionRepository;
    private final CertificateRepository certificateRepository;
    private final FileStorageUtil       fileStorageUtil;

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Transactional
    public ResponseDto submit(Long surveyId,
                              Map<String, String> fields,
                              Map<String, List<MultipartFile>> files) throws Exception {

        Survey survey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Survey not found with id: " + surveyId));

        List<Question> questions = questionRepository
                .findBySurveyIdOrderBySortOrderAsc(surveyId);

        // Validate required fields
        for (Question q : questions) {
            if (Boolean.TRUE.equals(q.getRequired())) {
                if (q.getType() == QuestionType.file) {
                    List<MultipartFile> uploads = files.getOrDefault(q.getName(), List.of());
                    if (uploads.isEmpty()) {
                        throw new IllegalArgumentException(
                                "Required file field '" + q.getName() + "' is missing");
                    }
                } else {
                    String val = fields.getOrDefault(q.getName(), "").trim();
                    if (val.isEmpty()) {
                        throw new IllegalArgumentException(
                                "Required field '" + q.getName() + "' is missing");
                    }
                }
            }
        }

        String email = fields.getOrDefault("email_address", "");

        Response response = Response.builder()
                .survey(survey)
                .respondentEmail(email)
                .build();

        // Map text answers
        for (Question q : questions) {
            if (q.getType() != QuestionType.file) {
                String val = fields.get(q.getName());
                if (val != null) {
                    response.getAnswers().add(ResponseAnswer.builder()
                            .response(response)
                            .question(q)
                            .answerValue(val)
                            .build());
                }
            }
        }

        Response saved = responseRepository.save(response);

        // Persist uploaded files
        for (Question q : questions) {
            if (q.getType() == QuestionType.file) {
                for (MultipartFile file : files.getOrDefault(q.getName(), List.of())) {
                    if (!file.isEmpty()) {
                        String storedName = fileStorageUtil.store(file);
                        saved.getCertificates().add(Certificate.builder()
                                .response(saved)
                                .question(q)
                                .originalFilename(file.getOriginalFilename())
                                .storedFilename(storedName)
                                .filePath(fileStorageUtil.resolve(storedName).toString())
                                .fileSize(file.getSize())
                                .build());
                    }
                }
            }
        }

        return toDto(responseRepository.save(saved), questions);
    }

    @Transactional(readOnly = true)
    public ResponseListDto findAll(Long surveyId, int page, int pageSize, String email) {
        if (!surveyRepository.existsById(surveyId)) {
            throw new ResourceNotFoundException("Survey not found with id: " + surveyId);
        }

        Pageable pageable = PageRequest.of(
                Math.max(page - 1, 0), pageSize,
                Sort.by("dateResponded").descending());

        Page<Response> pageResult = (email != null && !email.isBlank())
                ? responseRepository.findBySurveyIdAndRespondentEmailContainingIgnoreCase(
                        surveyId, email, pageable)
                : responseRepository.findBySurveyId(surveyId, pageable);

        List<Question> questions = questionRepository
                .findBySurveyIdOrderBySortOrderAsc(surveyId);

        return ResponseListDto.builder()
                .currentPage(page)
                .lastPage(pageResult.getTotalPages())
                .pageSize(pageSize)
                .totalCount(pageResult.getTotalElements())
                .responses(pageResult.getContent().stream()
                        .map(r -> toDto(r, questions)).toList())
                .build();
    }

    // ─── Mapper ───────────────────────────────────────────────────────────────

    private ResponseDto toDto(Response r, List<Question> questions) {
        Map<Long, String> answerMap = new HashMap<>();
        r.getAnswers().forEach(a -> answerMap.put(a.getQuestion().getId(), a.getAnswerValue()));

        List<AnswerFieldDto> fields = new ArrayList<>();
        for (Question q : questions) {
            if (q.getType() != QuestionType.file) {
                fields.add(new AnswerFieldDto(q.getName(),
                        answerMap.getOrDefault(q.getId(), "")));
            }
        }

        List<CertificateDto> certDtos = r.getCertificates().stream()
                .map(c -> CertificateDto.builder()
                        .id(c.getId())
                        .filename(c.getOriginalFilename())
                        .build())
                .toList();

        return ResponseDto.builder()
                .responseId(r.getId())
                .fields(fields)
                .certificates(certDtos.isEmpty() ? null : new CertificatesDto(certDtos))
                .dateResponded(r.getDateResponded() != null
                        ? r.getDateResponded().format(FORMATTER) : null)
                .build();
    }
}
