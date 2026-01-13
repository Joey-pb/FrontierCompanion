package wgu.jbas127.frontiercompanionbackend.dto;

public record ArticleDTO (
    Long id,
    String title,
    String description,
    String url,
    String thumbnailUrl,
    String author,
    String source,
    String publishedDate,
    Double similarityScore
){}
