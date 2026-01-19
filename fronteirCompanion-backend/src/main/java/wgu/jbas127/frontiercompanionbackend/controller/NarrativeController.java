package wgu.jbas127.frontiercompanionbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import wgu.jbas127.frontiercompanionbackend.entitiy.Narrative;
import wgu.jbas127.frontiercompanionbackend.service.NarrativeService;

import java.util.List;

/**
 * REST controller for managing narratives.
 * Provides endpoints for uploading narrative documents, retrieving narratives by exhibit,
 * and performing soft delete and restore operations.
 */
@RestController
@RequestMapping("api/narratives")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Tag(name = "Narratives", description = "")
public class NarrativeController {

    private final NarrativeService narrativeService;

    /**
     * Uploads a narrative document for a specific exhibit.
     * The file is processed, chunked, and stored as narrative entities.
     *
     * @param file        The text file containing the narrative content.
     * @param exhibitId   The ID of the exhibit associated with the narrative.
     * @param sectionName Optional name for the narrative section.
     * @return A {@link ResponseEntity} containing the list of created {@link Narrative} entities,
     *         or a bad request response if processing fails.
     */
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
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Retrieves all narratives associated with a specific exhibit.
     *
     * @param exhibitId The ID of the exhibit.
     * @return A {@link ResponseEntity} containing the list of {@link Narrative} entities.
     */
    @GetMapping("/exhibit/{exhibitId}")
    @Operation(summary = "Get narratives by id")
    public ResponseEntity<List<Narrative>> getNarrativeByExhibit(@PathVariable Long exhibitId) {
        return ResponseEntity.ok(narrativeService.getNarrativesByExhibit(exhibitId));
    }

    /**
     * Performs a soft delete on all narratives associated with a specific exhibit.
     * This is typically used for safe updates when re-uploading narratives.
     *
     * @param exhibitId The ID of the exhibit whose narratives should be soft deleted.
     * @return A {@link ResponseEntity} with a success message, or not found if no narratives exist.
     */
    @DeleteMapping("exhibit/{exhibitId}")
    @Operation(summary = "Soft delete narrative for an exhibit (for safe updates)")
    public ResponseEntity<String> softDeleteNarrativeByExhibit(@PathVariable Long exhibitId) {

        try {
            String result = narrativeService.softDeleteByExhibitId(exhibitId);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {

            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Restores soft-deleted narratives for a specific exhibit.
     *
     * @param exhibitId The ID of the exhibit whose narratives should be restored.
     * @return A {@link ResponseEntity} with a success message, or not found if no deleted narratives exist.
     */
    @PatchMapping("/exhibit/{exhibitId}/restore")
    @Operation(summary = "Restore soft deleted narrative for an exhibit")
    public ResponseEntity<String> restoreNarrativeByExhibit(@PathVariable Long exhibitId) {
        try {
            String result = narrativeService.restoreByExhibitId(exhibitId);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
