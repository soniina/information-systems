package itmo.sonina.creaturecatalog.controllers

import itmo.sonina.creaturecatalog.dto.import.CrudImport
import itmo.sonina.creaturecatalog.dto.requests.CrudRequest
import itmo.sonina.creaturecatalog.exceptions.UniqueConstraintException
import itmo.sonina.creaturecatalog.models.EntityWithId
import itmo.sonina.creaturecatalog.services.CrudService
import jakarta.validation.Valid
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.servlet.mvc.support.RedirectAttributes

abstract class CrudController<T : EntityWithId, REQ : CrudRequest, IMP : CrudImport>(private val crudService: CrudService<T, REQ, IMP>) {

    protected abstract val viewName: String
    protected abstract fun createEmptyRequest(): REQ

    @GetMapping
    fun listObjects(
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "6") pageSize: Int,
        @RequestParam(required = false) highlight: Int?,
        @RequestParam(required = false) filterColumn: String?,
        @RequestParam(required = false) filterValue: String?,
        @RequestParam(required = false) sortColumn: String?,
        model: Model
    ): String {

        val actualPage = highlight?.let {
            crudService.getPageForId(it, pageSize, filterColumn, filterValue, sortColumn) + 1
        } ?: page

        val objectsPage =
            crudService.getObjects(PageRequest.of(actualPage - 1, pageSize), filterColumn, filterValue, sortColumn)

        model.addAttribute(viewName, objectsPage.content)
        model.addAttribute("currentPage", actualPage)
        model.addAttribute("totalPages", objectsPage.totalPages)
        model.addAttribute("highlight", highlight)
        model.addAttribute("filterColumn", filterColumn)
        model.addAttribute("filterValue", filterValue)
        model.addAttribute("sortColumn", sortColumn)
        model.addAttribute("allowedColumns", crudService.getAllowedColumns())

        return "$viewName/table"
    }

    @GetMapping("/create")
    open fun showCreateForm(model: Model): String {
        model.addAttribute("entity", createEmptyRequest())
        return "$viewName/create"
    }

    @GetMapping("/edit/{id}")
    open fun showEditForm(@PathVariable id: Int, model: Model): String {
        val entity = crudService.getById(id) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Entity not found")
        model.addAttribute("id", id)
        model.addAttribute("entity", crudService.toRequest(entity))
        return "$viewName/edit"
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(
        @ModelAttribute("entity") @Valid request: REQ, bindingResult: BindingResult,
        @RequestParam(required = false) returnTo: String?,
        redirectAttributes: RedirectAttributes
    ): String {
        val created = try {
            crudService.create(request)
        } catch (e: UniqueConstraintException) {
            bindingResult.rejectValue(e.field, "error.${e.field}", e.message)
            return "$viewName/create"
        }
        if (returnTo.isNullOrBlank()) return "redirect:/$viewName"
        redirectAttributes.addAttribute("${viewName}Id", created.id)
        return "redirect:$returnTo"
    }

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Int,
        @ModelAttribute("entity") @Valid request: REQ,
        bindingResult: BindingResult
    ): String {
        if (bindingResult.hasErrors()) {
            println(bindingResult.allErrors)
            return "$viewName/edit"
        }
        crudService.update(id, request)
        return "redirect:/$viewName"
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Int, model: Model): String {
        crudService.delete(id)
        return "redirect:/$viewName"
    }

}