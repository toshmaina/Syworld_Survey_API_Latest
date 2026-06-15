package com.skyworld.survey.controller;

import com.skyworld.survey.dto.QuestionDto;
import com.skyworld.survey.dto.QuestionListDto;
import com.skyworld.survey.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/surveys/{surveyId}/questions",
                produces = MediaType.APPLICATION_XML_VALUE)
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    @PostMapping(consumes = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<QuestionDto> create(@PathVariable Long surveyId,
                                               @RequestBody QuestionDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(questionService.create(surveyId, dto));
    }

    @GetMapping
    public ResponseEntity<QuestionListDto> findAll(@PathVariable Long surveyId) {
        return ResponseEntity.ok(questionService.findBySurvey(surveyId));
    }

    @PutMapping(value = "/{questionId}", consumes = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<QuestionDto> update(@PathVariable Long surveyId,
                                               @PathVariable Long questionId,
                                               @RequestBody QuestionDto dto) {
        return ResponseEntity.ok(questionService.update(surveyId, questionId, dto));
    }

    @DeleteMapping("/{questionId}")
    public ResponseEntity<Void> delete(@PathVariable Long surveyId,
                                        @PathVariable Long questionId) {
        questionService.delete(surveyId, questionId);
        return ResponseEntity.noContent().build();
    }
}
