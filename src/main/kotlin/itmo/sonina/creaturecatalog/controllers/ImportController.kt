package itmo.sonina.creaturecatalog.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import itmo.sonina.creaturecatalog.dto.import.BookCreatureImport
import itmo.sonina.creaturecatalog.exceptions.UniqueConstraintException
import itmo.sonina.creaturecatalog.services.BookCreatureService
import itmo.sonina.creaturecatalog.services.ImportService
import jakarta.validation.Validator
import org.springframework.dao.DataAccessException
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.mvc.support.RedirectAttributes

@Controller
@RequestMapping("/imports")
class ImportController(
    private val importService: ImportService,
    private val bookCreatureService: BookCreatureService,
    private val objectMapper: ObjectMapper,
    private val validator: Validator
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
        val creatures = try {
            objectMapper.readValue<List<BookCreatureImport>>(file.bytes)
        } catch (e: Exception) {
            redirectAttributes.addFlashAttribute("error", "Couldn't read the file: ${e.message}")
            return "redirect:/imports"
        }

        val violations = creatures.flatMapIndexed { index, creature ->
            validator.validate(creature).map { violation ->
                "Creature at index $index (${creature.name}): ${violation.propertyPath} ${violation.message}"
            }
        }

        if (violations.isNotEmpty()) {
            importService.create(errorMessage = "Validation errors: ${violations.take(1).joinToString()}")
            return "redirect:/imports"
        }

        try {
            bookCreatureService.createAllFromImport(creatures)
            importService.create(objectAdded = creatures.size)
        } catch (e: DataAccessException) {
            importService.create(errorMessage = "Data access error: ${e.message}")
        } catch (e: UniqueConstraintException) {
            importService.create(errorMessage = "Violation of uniqueness: ${e.message}")
        } catch (e: Exception) {
            importService.create(errorMessage = "Unexpected error: ${e.message}")
        }
        return "redirect:/imports"
    }
}