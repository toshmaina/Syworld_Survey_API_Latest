package com.skyworld.survey.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.*;

import java.util.List;

@JacksonXmlRootElement(localName = "options")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class OptionsDto {

    @JacksonXmlProperty(isAttribute = true)
    private String multiple;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "option")
    private List<OptionDto> option;
}
