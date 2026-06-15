package com.skyworld.survey.controller;

import com.skyworld.survey.dto.SurveyDto;
import com.skyworld.survey.dto.SurveyListDto;
import com.skyworld.survey.service.SurveyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/surveys", produces = MediaType.APPLICATION_XML_VALUE)
@RequiredArgsConstructor
public class SurveyController {

    private final SurveyService surveyService;

    @PostMapping(consumes = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<SurveyDto> create(@RequestBody SurveyDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(surveyService.create(dto));
    }

    @GetMapping
    public ResponseEntity<SurveyListDto> findAll() {
        return ResponseEntity.ok(surveyService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SurveyDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(surveyService.findById(id));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<SurveyDto> update(@PathVariable Long id,
                                             @RequestBody SurveyDto dto) {
        return ResponseEntity.ok(surveyService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        surveyService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
