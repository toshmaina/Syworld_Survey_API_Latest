package com.skyworld.survey.service;

import com.skyworld.survey.dto.*;
import com.skyworld.survey.entity.*;
import com.skyworld.survey.exception.ResourceNotFoundException;
import com.skyworld.survey.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final SurveyRepository   surveyRepository;

    @Transactional
    public QuestionDto create(Long surveyId, QuestionDto dto) {
        Survey survey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Survey not found with id: " + surveyId));

        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new IllegalArgumentException("Question name is required");
        }
        if (questionRepository.existsBySurveyIdAndName(surveyId, dto.getName())) {
            throw new IllegalArgumentException(
                    "A question with name '" + dto.getName() + "' already exists in this survey");
        }

        Question question = Question.builder()
                .survey(survey)
                .name(dto.getName())
                .type(QuestionType.valueOf(dto.getType()))
                .text(dto.getText())
                .description(dto.getDescription())
                .required("yes".equalsIgnoreCase(dto.getRequired()))
                .sortOrder(questionRepository.findBySurveyIdOrderBySortOrderAsc(surveyId).size())
                .build();

        applyOptions(question, dto);
        applyFileProperties(question, dto);

        return toDto(questionRepository.save(question));
    }

    @Transactional(readOnly = true)
    public QuestionListDto findBySurvey(Long surveyId) {
        if (!surveyRepository.existsById(surveyId)) {
            throw new ResourceNotFoundException("Survey not found with id: " + surveyId);
        }
        List<QuestionDto> dtos = questionRepository
                .findBySurveyIdOrderBySortOrderAsc(surveyId)
                .stream().map(this::toDto).toList();
        return new QuestionListDto(dtos);
    }

    @Transactional
    public QuestionDto update(Long surveyId, Long questionId, QuestionDto dto) {
        Question question = questionRepository.findByIdAndSurveyId(questionId, surveyId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Question " + questionId + " not found in survey " + surveyId));

        question.setName(dto.getName());
        question.setType(QuestionType.valueOf(dto.getType()));
        question.setText(dto.getText());
        question.setDescription(dto.getDescription());
        question.setRequired("yes".equalsIgnoreCase(dto.getRequired()));

        question.getOptions().clear();
        applyOptions(question, dto);

        question.setFileProperties(null);
        applyFileProperties(question, dto);

        return toDto(questionRepository.save(question));
    }

    @Transactional
    public void delete(Long surveyId, Long questionId) {
        Question question = questionRepository.findByIdAndSurveyId(questionId, surveyId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Question " + questionId + " not found in survey " + surveyId));
        questionRepository.delete(question);
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private void applyOptions(Question question, QuestionDto dto) {
        if (dto.getOptions() != null && dto.getOptions().getOption() != null) {
            AtomicInteger order = new AtomicInteger(0);
            dto.getOptions().getOption().forEach(opt ->
                    question.getOptions().add(QuestionOption.builder()
                            .question(question)
                            .value(opt.getValue())
                            .label(opt.getLabel())
                            .sortOrder(order.getAndIncrement())
                            .build()));
        }
    }

    private void applyFileProperties(Question question, QuestionDto dto) {
        if (dto.getFileProperties() != null) {
            FilePropertiesDto fp = dto.getFileProperties();
            question.setFileProperties(QuestionFileProperties.builder()
                    .question(question)
                    .format(fp.getFormat() != null ? fp.getFormat() : ".pdf")
                    .maxFileSize(fp.getMaxFileSize() != null ? fp.getMaxFileSize() : 1)
                    .maxFileSizeUnit(fp.getMaxFileSizeUnit() != null ? fp.getMaxFileSizeUnit() : "mb")
                    .multiple("yes".equalsIgnoreCase(fp.getMultiple()))
                    .build());
        }
    }

    public QuestionDto toDto(Question q) {
        OptionsDto optionsDto = null;
        if (q.getOptions() != null && !q.getOptions().isEmpty()) {
            boolean multi = q.getType() == QuestionType.multiple_choice;
            List<OptionDto> opts = q.getOptions().stream()
                    .map(o -> OptionDto.builder()
                            .value(o.getValue())
                            .label(o.getLabel())
                            .build())
                    .toList();
            optionsDto = OptionsDto.builder()
                    .multiple(multi ? "yes" : "no")
                    .option(opts)
                    .build();
        }

        FilePropertiesDto fpDto = null;
        if (q.getFileProperties() != null) {
            QuestionFileProperties fp = q.getFileProperties();
            fpDto = FilePropertiesDto.builder()
                    .format(fp.getFormat())
                    .maxFileSize(fp.getMaxFileSize())
                    .maxFileSizeUnit(fp.getMaxFileSizeUnit())
                    .multiple(Boolean.TRUE.equals(fp.getMultiple()) ? "yes" : "no")
                    .build();
        }

        return QuestionDto.builder()
                .id(q.getId())
                .name(q.getName())
                .type(q.getType().name())
                .required(Boolean.TRUE.equals(q.getRequired()) ? "yes" : "no")
                .text(q.getText())
                .description(q.getDescription())
                .options(optionsDto)
                .fileProperties(fpDto)
                .build();
    }
}
