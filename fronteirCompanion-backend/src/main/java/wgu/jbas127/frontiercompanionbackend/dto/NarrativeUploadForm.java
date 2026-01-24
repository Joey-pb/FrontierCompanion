package wgu.jbas127.frontiercompanionbackend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.multipart.MultipartFile;

public class NarrativeUploadForm {

    @Schema(description = "Text file with narrative content", type = "string", format = "binary")
    private MultipartFile file;

    @Schema(description = "Exhibit ID associated with narrative")
    private Long exhibitId;

    @Schema(description = "Optional section name (e.g. 'Daily Life', 'Migration')", nullable = true)
    private String sectionName;

    public MultipartFile getFile() { return file; }
    public void setFile(MultipartFile file) { this.file = file; }

    public Long getExhibitId() { return exhibitId; }
    public void setExhibitId(Long exhibitId) { this.exhibitId = exhibitId; }

    public String getSectionName() { return sectionName; }
    public void setSectionName(String sectionName) { this.sectionName = sectionName; }
}