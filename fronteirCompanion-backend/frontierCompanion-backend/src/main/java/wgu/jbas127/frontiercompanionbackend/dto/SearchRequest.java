package wgu.jbas127.frontiercompanionbackend.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SearchRequest {
    private String query;
    private Integer limit = 20;
    private Double threshold = 0.5;

}
