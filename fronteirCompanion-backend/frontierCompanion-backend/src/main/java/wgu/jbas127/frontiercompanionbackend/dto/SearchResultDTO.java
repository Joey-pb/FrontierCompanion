package wgu.jbas127.frontiercompanionbackend.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class SearchResultDTO {
    private List<ArticleDTO> articles;
    private Integer totalResults;

}
