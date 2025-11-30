package itmo.sonina.creaturecatalog.services

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import itmo.sonina.creaturecatalog.dto.import.BookCreatureImport
import itmo.sonina.creaturecatalog.exceptions.UniqueConstraintException
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import jakarta.validation.ValidationException
import org.springframework.stereotype.Service
import jakarta.validation.Validator
import org.springframework.dao.ConcurrencyFailureException
import org.springframework.dao.DataAccessException
import org.springframework.retry.backoff.FixedBackOffPolicy
import org.springframework.retry.policy.SimpleRetryPolicy
import org.springframework.retry.support.RetryTemplate
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.TransactionDefinition
import org.springframework.transaction.TransactionSystemException
import org.springframework.transaction.support.TransactionTemplate
import org.springframework.web.multipart.MultipartFile
import java.io.ByteArrayInputStream
import java.sql.SQLException
import java.util.*

@Service
class ImportManager(
    private val minioService: MinioService,
    private val bookCreatureService: BookCreatureService,
    private val importService: ImportService,
    private val objectMapper: ObjectMapper,
    private val validator: Validator,
    private val transactionManager: PlatformTransactionManager
) {

    @PersistenceContext
    private lateinit var entityManager: EntityManager

    fun processImportFile(file: MultipartFile) {
        val minioFileName = UUID.randomUUID().toString() + ".json"
        val originalName = file.originalFilename ?: ""

        try {
            val bytes = file.bytes
            minioService.uploadFile(minioFileName, ByteArrayInputStream(bytes), "application/json")

            val creatures = objectMapper.readValue<List<BookCreatureImport>>(bytes)

            val violations = creatures.flatMapIndexed { index, creature ->
                validator.validate(creature).map { "Index $index: ${it.message}" }
            }

            if (violations.isNotEmpty()) {
                throw ValidationException("Validation errors: ${violations.take(3)}")
            }

            val retryTemplate = RetryTemplate()
            val retryPolicy = SimpleRetryPolicy(
                3,
                mapOf(
                    ConcurrencyFailureException::class.java to true,
                    SQLException::class.java to true,
                    TransactionSystemException::class.java to true
                )
            )
            retryTemplate.setRetryPolicy(retryPolicy)
            retryTemplate.setBackOffPolicy(FixedBackOffPolicy().apply { backOffPeriod = 100 })

            retryTemplate.execute<Unit, Exception> {
                val transactionTemplate = TransactionTemplate(transactionManager).apply {
                    isolationLevel = TransactionDefinition.ISOLATION_SERIALIZABLE
                }

                transactionTemplate.execute {

                    if (originalName.contains("db_fail")) {
                        throw org.springframework.dao.DataAccessResourceFailureException("Simulated DB Connection Error (Connection refused)")
                    }

                    bookCreatureService.createAllFromImport(creatures)

                    if (originalName.contains("logic_fail")) {
                        throw RuntimeException("Simulated Business Logic Crash (NullPointerException etc)")
                    }
                    entityManager.flush()
                    importService.registerSuccess(creatures.size, minioFileName)
                }
            }
        } catch (e: Exception) {
            try {
                minioService.deleteFile(minioFileName)
            } catch (e: Exception) {
            }
            importService.registerFailure(
                when (e) {
                    is ValidationException -> e.message ?: "Validation error"
                    is DataAccessException -> "Database error: ${e.message}"
                    is UniqueConstraintException -> "Violation of uniqueness: ${e.message}"
                    else -> "Unexpected error: ${e.message}"
                }
            )
        }
    }

}