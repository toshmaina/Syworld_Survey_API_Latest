package com.skyworld.survey.repository;

import com.skyworld.survey.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    List<Question> findBySurveyIdOrderBySortOrderAsc(Long surveyId);

    Optional<Question> findByIdAndSurveyId(Long id, Long surveyId);

    boolean existsBySurveyIdAndName(Long surveyId, String name);
}
