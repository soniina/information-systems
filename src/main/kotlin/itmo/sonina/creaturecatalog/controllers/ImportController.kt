package itmo.sonina.creaturecatalog.controllers

import itmo.sonina.creaturecatalog.services.ImportManager
import itmo.sonina.creaturecatalog.services.ImportService
import itmo.sonina.creaturecatalog.services.MinioService
import org.springframework.core.io.InputStreamResource
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.mvc.support.RedirectAttributes

@Controller
@RequestMapping("/imports")
class ImportController(
    private val importManager: ImportManager,
    private val importService: ImportService,
    private val minioService: MinioService
) {

    private val viewName = "imports"

    @GetMapping
    fun listObjects(
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "6") pageSize: Int,
        model: Model
    ): String {

        val objectsPage = importService.getObjects(PageRequest.of(page - 1, pageSize))

        model.addAttribute(viewName, objectsPage.content)
        model.addAttribute("currentPage", page)
        model.addAttribute("totalPages", objectsPage.totalPages)

        return "$viewName/table"
    }

    @PostMapping("/upload")
    fun uploadFile(@RequestParam("file") file: MultipartFile, redirectAttributes: RedirectAttributes): String {
        if (file.isEmpty) {
            redirectAttributes.addFlashAttribute("error", "File is empty.")
            return "redirect:/imports"
        }
        importManager.processImportFile(file)
        return "redirect:/imports"
    }


    @GetMapping("/download/{filename}")
    fun downloadFile(@PathVariable filename: String): ResponseEntity<InputStreamResource> {
        val fileStream = minioService.downloadFile(filename)
        val resource = InputStreamResource(fileStream)

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"$filename\"")
            .contentType(MediaType.APPLICATION_JSON)
            .body(resource)
    }
}