package itmo.sonina.creaturecatalog.controllers

import itmo.sonina.creaturecatalog.dto.requests.CrudRequest
import itmo.sonina.creaturecatalog.services.CrudService
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

abstract class CrudController<T:Any, in REQ: CrudRequest>(private val crudService: CrudService<T, REQ>) {

    @GetMapping
    fun listObjects(@RequestParam(defaultValue = "0") page: Int,
                      @RequestParam(defaultValue = "10") pageSize: Int,
                      @RequestParam(required = false) filterColumn: String?,
                      @RequestParam(required = false) filterValue: String?,
                      @RequestParam(required = false) sortColumn: String?): Page<T> {
        val objectsPage = crudService.getObjects(PageRequest.of(page, pageSize), filterColumn, filterValue, sortColumn)
        return objectsPage
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody @Valid request: REQ): T {
        return crudService.create(request)
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun update(@PathVariable id: Int, @RequestBody @Valid request: REQ): T {
        return crudService.update(id, request)
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: Int) {
        crudService.delete(id)
    }

}