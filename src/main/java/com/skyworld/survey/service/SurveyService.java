package com.skyworld.survey.service;

import com.skyworld.survey.dto.SurveyDto;
import com.skyworld.survey.dto.SurveyListDto;
import com.skyworld.survey.entity.Survey;
import com.skyworld.survey.exception.ResourceNotFoundException;
import com.skyworld.survey.repository.SurveyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SurveyService {

    private final SurveyRepository surveyRepository;

    @Transactional
    public SurveyDto create(SurveyDto dto) {
        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new IllegalArgumentException("Survey name is required");
        }
        Survey survey = Survey.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .build();
        return toDto(surveyRepository.save(survey));
    }

    @Transactional(readOnly = true)
    public SurveyListDto findAll() {
        List<SurveyDto> dtos = surveyRepository.findAll()
                .stream().map(this::toDto).toList();
        return new SurveyListDto(dtos);
    }

    @Transactional(readOnly = true)
    public SurveyDto findById(Long id) {
        return toDto(getOrThrow(id));
    }

    @Transactional
    public SurveyDto update(Long id, SurveyDto dto) {
        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new IllegalArgumentException("Survey name is required");
        }
        Survey survey = getOrThrow(id);
        survey.setName(dto.getName());
        survey.setDescription(dto.getDescription());
        return toDto(surveyRepository.save(survey));
    }

    @Transactional
    public void delete(Long id) {
        surveyRepository.delete(getOrThrow(id));
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private Survey getOrThrow(Long id) {
        return surveyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Survey not found with id: " + id));
    }

    private SurveyDto toDto(Survey s) {
        return SurveyDto.builder()
                .id(s.getId())
                .name(s.getName())
                .description(s.getDescription())
                .build();
    }
}
