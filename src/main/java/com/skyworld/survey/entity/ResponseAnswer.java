package com.skyworld.survey.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "response_answers",
       uniqueConstraints = @UniqueConstraint(
           name = "uq_answer_per_question_response",
           columnNames = {"response_id", "question_id"}
       ),
       indexes = {
           @Index(name = "idx_answers_response", columnList = "response_id"),
           @Index(name = "idx_answers_question", columnList = "question_id")
       })
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ResponseAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "response_id", nullable = false)
    private Response response;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Column(name = "answer_value", columnDefinition = "TEXT")
    private String answerValue;
}
