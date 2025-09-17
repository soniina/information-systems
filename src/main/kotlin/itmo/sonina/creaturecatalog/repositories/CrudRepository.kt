package itmo.sonina.creaturecatalog.repositories

import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import jakarta.persistence.criteria.Path
import jakarta.persistence.criteria.Root
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository

@Repository
abstract class CrudRepository<T : Any>(private val entityClass: Class<T>) {

    @PersistenceContext
    protected lateinit var entityManager: EntityManager

    protected open val allowedColumns: Set<String> = setOf("id")

    fun findById(id: Int): T? = entityManager.find(entityClass, id)

    private fun <T> resolvePath(root: Root<T>, pathString: String): Path<String> {
        val parts = pathString.split(".")
        var path: Path<*> = root
        for (part in parts) {
            path = path.get<Any>(part)
        }
        return path as Path<String>
    }

    fun findPage(pageable: Pageable,
                 filterColumn: String?,
                 filterValue: String?,
                 sortColumn: String?): Page<T> {

        if (filterColumn != null && filterColumn !in allowedColumns)
            throw IllegalArgumentException("Invalid filter column: $filterColumn")
        if (sortColumn != null && sortColumn !in allowedColumns)
            throw IllegalArgumentException("Invalid sort column: $sortColumn")

        val builder = entityManager.criteriaBuilder
        val mainQuery = builder.createQuery(entityClass)
        val root = mainQuery.from(entityClass)

        if (filterColumn != null && filterValue != null) {
            val filterPath = resolvePath(root, filterColumn)
            mainQuery.where(
                builder.like(
                    builder.lower(filterPath),
                    "%${filterValue.lowercase()}%"
                )
            )
        }

        if (sortColumn != null) {
            val sortPath = resolvePath(root, sortColumn)
            mainQuery.orderBy(builder.asc(sortPath))
        }

        val query = entityManager.createQuery(mainQuery).apply {
            firstResult = pageable.offset.toInt()
            maxResults = pageable.pageSize
        }
        val content = query.resultList


        val countQuery = builder.createQuery(Long::class.java)
        val countRoot = countQuery.from(entityClass)

        if (filterColumn != null && filterValue != null) {
            val countPath = resolvePath(countRoot, filterColumn)
            countQuery.where(
                builder.like(
                    builder.lower(countPath),
                    "%${filterValue.lowercase()}%"
                )
            )
        }
        countQuery.select(builder.count(countRoot))
        val total = entityManager.createQuery(countQuery).singleResult

        return PageImpl(content, pageable, total)
    }

    fun save(entity: T) = entityManager.persist(entity)

    fun update(entity: T) = entityManager.merge(entity)

    fun deleteById(id: Int) {
        val entity = findById(id)
        entity?.let {
            entityManager.remove(it)
        }
    }
}
