package wgu.jbas127.frontiercompanionbackend.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ArticleDTO {
    private Long id;
    private String title;
    private String description;
    private String url;
    private String thumbnailUrl;
    private String author;
    private String source;
    private String publishedDate;
}
