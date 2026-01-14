package wgu.jbas127.frontiercompanionbackend.dto;

import lombok.Data;

@Data
public class NarrativeDTO {
    private Long id;
    private Long exhibitId;
    private String title;
    private String content;
    private String sectionName;
    private Double similarityScore;
}
