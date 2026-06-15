package com.skyworld.survey.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.*;

import java.util.List;

@JacksonXmlRootElement(localName = "question_responses")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ResponseListDto {

    @JacksonXmlProperty(isAttribute = true, localName = "current_page")
    private int currentPage;

    @JacksonXmlProperty(isAttribute = true, localName = "last_page")
    private int lastPage;

    @JacksonXmlProperty(isAttribute = true, localName = "page_size")
    private int pageSize;

    @JacksonXmlProperty(isAttribute = true, localName = "total_count")
    private long totalCount;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "question_response")
    private List<ResponseDto> responses;
}
