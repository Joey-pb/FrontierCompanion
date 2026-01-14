package wgu.jbas127.frontiercompanionbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import wgu.jbas127.frontiercompanionbackend.entitiy.Narrative;
import wgu.jbas127.frontiercompanionbackend.service.NarrativeService;

import java.util.List;

@RestController
@RequestMapping("api/narratives")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class NarrativeController {

    private final NarrativeService narrativeService;

    @PostMapping("/upload")
    @Operation(summary = "Upload Narrative document",
            description = "Upload a text file containing narrative content for an exhibit")
    public ResponseEntity<List<Narrative>> uploadNarrative(
            @Parameter(description = "Text file with narrative content")
            @RequestParam("file") MultipartFile file,
            @Parameter(description = "Exhibit ID associated with narrative")
            @RequestParam("exhibitId") Long exhibitId,
            @Parameter(description = "Optional section name (e.g. 'Daily Life', 'Migration'")
            @RequestParam(value = "sectionName", required = false) String sectionName){

        try {
            List<Narrative> narratives = narrativeService.processNarrativeDocument(file, exhibitId, sectionName);
            return ResponseEntity.ok(narratives);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/exhibit/{exhibitId}")
    @Operation(summary = "Get narratives by id")
    public ResponseEntity<List<Narrative>> getNarrativeByExhibit(@PathVariable Long exhibitId) {
        return ResponseEntity.ok(narrativeService.getNarrativesByExhibit(exhibitId));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete narrative")
    public ResponseEntity<Void> deleteNarrative(@PathVariable Long id){
        narrativeService.deleteNarrative(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/exhibit/{id}")
    @Operation(summary = "Delete all narratives for an exhibit")
    public ResponseEntity<Void> deleteAllNarratives(@PathVariable Long id){
        narrativeService.deleteNarrativesByExhibit(id);
        return ResponseEntity.noContent().build();
    }
}
