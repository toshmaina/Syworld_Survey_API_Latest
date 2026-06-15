package com.skyworld.survey.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "responses",
       indexes = {
           @Index(name = "idx_responses_email",  columnList = "respondent_email"),
           @Index(name = "idx_responses_survey", columnList = "survey_id")
       })
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Response {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_id", nullable = false)
    private Survey survey;

    @Column(name = "respondent_email", nullable = false, length = 255)
    private String respondentEmail;

    @CreationTimestamp
    @Column(name = "date_responded", updatable = false)
    private LocalDateTime dateResponded;

    @OneToMany(mappedBy = "response", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ResponseAnswer> answers = new ArrayList<>();

    @OneToMany(mappedBy = "response", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Certificate> certificates = new ArrayList<>();
}
