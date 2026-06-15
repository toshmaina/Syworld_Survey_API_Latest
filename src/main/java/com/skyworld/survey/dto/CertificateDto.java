package com.skyworld.survey.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.*;

@JacksonXmlRootElement(localName = "certificate")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CertificateDto {

    @JacksonXmlProperty(isAttribute = true)
    private Long id;

    private String filename;
}
