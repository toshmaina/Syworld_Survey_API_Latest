package com.skyworld.survey.repository;

import com.skyworld.survey.entity.Response;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResponseRepository extends JpaRepository<Response, Long> {

    Page<Response> findBySurveyId(Long surveyId, Pageable pageable);

    Page<Response> findBySurveyIdAndRespondentEmailContainingIgnoreCase(
            Long surveyId, String email, Pageable pageable);

    Optional<Response> findByIdAndSurveyId(Long id, Long surveyId);
}
