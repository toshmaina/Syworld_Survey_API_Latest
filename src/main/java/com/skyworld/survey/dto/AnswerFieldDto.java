package com.skyworld.survey.dto;

import lombok.*;

/**
 * Represents a single dynamic answer field.
 * The fieldName becomes the XML element name at runtime.
 * e.g. fieldName="full_name", value="Jane Doe"
 * serialises to: <full_name>Jane Doe</full_name>
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AnswerFieldDto {
    private String fieldName;
    private String value;
}
