package itmo.sonina.creaturecatalog.services

import itmo.sonina.creaturecatalog.models.ImportOperation
import itmo.sonina.creaturecatalog.models.ImportStatus
import itmo.sonina.creaturecatalog.repositories.ImportHistoryRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ImportService(private val importHistoryRepository: ImportHistoryRepository) {

    fun getObjects(pageable: Pageable): Page<ImportOperation> =
        importHistoryRepository.findPage(pageable)

    @Transactional
    fun create(objectAdded: Int = 0, errorMessage: String? = null): ImportOperation =
        importHistoryRepository.save(
            ImportOperation(
                status = if (errorMessage != null) ImportStatus.FAILED else ImportStatus.SUCCESS,
                objectsAdded = objectAdded,
                errorMessage = errorMessage
            )
        )
}