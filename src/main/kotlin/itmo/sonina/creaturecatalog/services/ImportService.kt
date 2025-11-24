package itmo.sonina.creaturecatalog.services

import itmo.sonina.creaturecatalog.models.ImportOperation
import itmo.sonina.creaturecatalog.models.ImportStatus
import itmo.sonina.creaturecatalog.repositories.ImportHistoryRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Service
class ImportService(private val importHistoryRepository: ImportHistoryRepository) {

    fun getObjects(pageable: Pageable): Page<ImportOperation> =
        importHistoryRepository.findPage(pageable)
    
    fun registerSuccess(objectsCount: Int, minioFileName: String): ImportOperation {
        return importHistoryRepository.save(
            ImportOperation(
                status = ImportStatus.SUCCESS,
                objectsAdded = objectsCount,
                errorMessage = null,
                minioFileName = minioFileName
            )
        )
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun registerFailure(errorMessage: String): ImportOperation {
        return importHistoryRepository.save(
            ImportOperation(
                status = ImportStatus.FAILED,
                objectsAdded = 0,
                errorMessage = errorMessage,
                minioFileName = null
            )
        )
    }
}