package itmo.sonina.creaturecatalog.services

import itmo.sonina.creaturecatalog.dto.requests.CrudRequest
import itmo.sonina.creaturecatalog.repositories.CrudRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
abstract class CrudService<T : Any, in REQ: CrudRequest> (private val crudRepository: CrudRepository<T>) {

    abstract fun toEntity(request: REQ): T
    abstract fun updateEntity(entity: T, request: REQ)

    @Transactional
    fun create(request: REQ): T = crudRepository.save(toEntity(request))

    @Transactional
    fun update(id: Int, request: REQ): T {
        val entity = crudRepository.findById(id) ?: throw EntityNotFoundException("Entity with id $id not found")
        updateEntity(entity, request)
        return crudRepository.update(entity)
    }

    @Transactional
    fun delete(id: Int) = crudRepository.deleteById(id)

    fun getById(id: Int) = crudRepository.findById(id)

    fun getObjects(pageable: Pageable, filterColumn: String?, filterValue: String?, sortColumn: String?): Page<T> =
         crudRepository.findPage(pageable, filterColumn.toString(), filterValue.toString(), sortColumn.toString())

}