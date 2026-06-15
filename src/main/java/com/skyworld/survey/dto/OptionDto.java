package com.skyworld.survey.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.*;

@JacksonXmlRootElement(localName = "option")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class OptionDto {

    @JacksonXmlProperty(isAttribute = true)
    private String value;

    @JacksonXmlProperty(localName = "label")
    private String label;
}
