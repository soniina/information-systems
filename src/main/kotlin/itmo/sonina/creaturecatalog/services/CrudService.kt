package itmo.sonina.creaturecatalog.services

import itmo.sonina.creaturecatalog.dto.import.CrudImport
import itmo.sonina.creaturecatalog.dto.requests.CrudRequest
import itmo.sonina.creaturecatalog.repositories.CrudRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.dao.ConcurrencyFailureException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional
import java.sql.SQLException

@Service
abstract class CrudService<T : Any, REQ : CrudRequest, IMP : CrudImport>(private val crudRepository: CrudRepository<T>) {

    abstract fun toEntity(request: REQ): T
    abstract fun toEntity(import: IMP): T
    abstract fun toRequest(entity: T): REQ
    abstract fun updateEntity(entity: T, request: REQ)

    @Transactional(isolation = Isolation.SERIALIZABLE)
    @Retryable(
        value = [ConcurrencyFailureException::class, SQLException::class],
        maxAttempts = 3,
        backoff = Backoff(delay = 100)
    )
    fun create(request: REQ): T = crudRepository.save(toEntity(request))

    @Transactional
    fun update(id: Int, request: REQ): T {
        val entity = crudRepository.findById(id) ?: throw EntityNotFoundException("Entity with id $id not found")
        updateEntity(entity, request)
        return crudRepository.update(entity)
    }

    @Transactional
    fun delete(id: Int) = crudRepository.deleteById(id)

    fun createFromImport(import: IMP): T = crudRepository.save(toEntity(import))

    fun getById(id: Int) = crudRepository.findById(id)

    fun getObjects(pageable: Pageable, filterColumn: String?, filterValue: String?, sortColumn: String?): Page<T> =
        crudRepository.findPage(
            pageable,
            filterColumn ?: "",
            filterValue ?: "",
            sortColumn?.takeIf { it.isNotBlank() } ?: "id")

    fun getPageForId(id: Int, pageSize: Int, filterColumn: String?, filterValue: String?, sortColumn: String?): Int {
        val allEntities = crudRepository.findAllIds(
            filterColumn ?: "",
            filterValue ?: "",
            sortColumn?.takeIf { it.isNotBlank() } ?: "id")
        val index = allEntities.indexOf(id)
        return if (index == -1) 0 else index / pageSize
    }

    fun getAll() = crudRepository.findAll()

    fun getFree() = crudRepository.findFree()

    fun getAllowedColumns(): Set<String> = crudRepository.allowedColumns

}