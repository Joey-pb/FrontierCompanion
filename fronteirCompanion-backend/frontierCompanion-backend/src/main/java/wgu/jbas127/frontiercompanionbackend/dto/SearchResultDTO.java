package wgu.jbas127.frontiercompanionbackend.dto;

import java.util.List;

public record SearchResultDTO (
    List<ArticleDTO> articles,
    Integer totalResults
) {}
