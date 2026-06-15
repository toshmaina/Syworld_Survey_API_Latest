package com.skyworld.survey.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "question_options",
       uniqueConstraints = @UniqueConstraint(
           name = "uq_option_value_per_question",
           columnNames = {"question_id", "value"}
       ))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class QuestionOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Column(nullable = false, length = 100)
    private String value;

    @Column(nullable = false, length = 255)
    private String label;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;
}
