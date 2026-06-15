package com.skyworld.survey.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.*;

import java.util.List;

@JacksonXmlRootElement(localName = "question_response")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ResponseDto {

    @JacksonXmlProperty(localName = "response_id")
    private Long responseId;

    /**
     * Dynamic answer fields — each question's machine name becomes an XML element.
     * Serialised manually via XmlResponseSerializer (not via Jackson annotations)
     * because element names are runtime database values.
     */
    private List<AnswerFieldDto> fields;

    @JacksonXmlProperty(localName = "certificates")
    private CertificatesDto certificates;

    @JacksonXmlProperty(localName = "date_responded")
    private String dateResponded;
}
