package com.skyworld.survey.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "question_file_properties")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class QuestionFileProperties {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false, unique = true)
    private Question question;

    @Column(nullable = false, length = 20)
    private String format = ".pdf";

    @Column(name = "max_file_size", nullable = false)
    private Integer maxFileSize = 1;

    @Column(name = "max_file_size_unit", nullable = false, length = 10)
    private String maxFileSizeUnit = "mb";

    @Column(nullable = false)
    private Boolean multiple = false;
}
