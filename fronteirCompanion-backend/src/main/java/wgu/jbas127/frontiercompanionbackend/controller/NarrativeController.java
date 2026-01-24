package wgu.jbas127.frontiercompanionbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wgu.jbas127.frontiercompanionbackend.dto.NarrativeUploadForm;
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
     * @param form Form containing narrative file, exhibit ID, and optional section name.
     * @return A {@link ResponseEntity} containing the list of created {@link Narrative} entities,
     *         or a bad request response if processing fails.
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload Narrative document",
            description = "Upload a text file containing narrative content for an exhibit")
    public ResponseEntity<List<Narrative>> uploadNarrative(@ModelAttribute NarrativeUploadForm form) {

        try {
            List<Narrative> narratives =
                    narrativeService.processNarrativeDocument(form.getFile(), form.getExhibitId(), form.getSectionName());
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
