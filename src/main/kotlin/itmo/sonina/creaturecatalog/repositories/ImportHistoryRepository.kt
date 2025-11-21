package itmo.sonina.creaturecatalog.repositories

import itmo.sonina.creaturecatalog.models.ImportOperation
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository

@Repository
class ImportHistoryRepository {

    @PersistenceContext
    protected lateinit var entityManager: EntityManager

    fun save(entity: ImportOperation): ImportOperation {
        entityManager.persist(entity)
        return entity
    }

    fun findPage(pageable: Pageable): Page<ImportOperation> {
        val builder = entityManager.criteriaBuilder
        val mainQuery = builder.createQuery(ImportOperation::class.java)

        val query = entityManager.createQuery(mainQuery).apply {
            firstResult = pageable.offset.toInt()
            maxResults = pageable.pageSize
        }
        val content = query.resultList

        val countQuery = builder.createQuery(Long::class.java)
        val countRoot = countQuery.from(ImportOperation::class.java)
        countQuery.select(builder.count(countRoot))
        val total = entityManager.createQuery(countQuery).singleResult

        return PageImpl(content, pageable, total)
    }
}
