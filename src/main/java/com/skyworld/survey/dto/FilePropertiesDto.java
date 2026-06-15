package com.skyworld.survey.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.*;

@JacksonXmlRootElement(localName = "file_properties")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class FilePropertiesDto {

    @JacksonXmlProperty(isAttribute = true)
    private String format;

    @JacksonXmlProperty(isAttribute = true, localName = "max_file_size")
    private Integer maxFileSize;

    @JacksonXmlProperty(isAttribute = true, localName = "max_file_size_unit")
    private String maxFileSizeUnit;

    @JacksonXmlProperty(isAttribute = true)
    private String multiple;
}
