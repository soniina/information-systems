package itmo.sonina.creaturecatalog.repositories

import itmo.sonina.creaturecatalog.models.BookCreatureType
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.Path
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Root
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository

@Repository
abstract class CrudRepository<T : Any>(private val entityClass: Class<T>) {

    @PersistenceContext
    protected lateinit var entityManager: EntityManager

    val allowedColumns: Set<String> = setOf("id")

    fun findById(id: Int): T? = entityManager.find(entityClass, id)

    private fun <T> resolvePath(root: Root<T>, pathString: String): Path<*> {
        var path: Path<*> = root
        for (part in pathString.split(".")) {
            path = path.get<Any>(part)
        }
        return path
    }

    private fun <T> buildFilterPredicate(builder: CriteriaBuilder, root: Root<T>, filterColumn: String, filterValue: String): Predicate {
        val filterPath = resolvePath(root, filterColumn)
        val javaType = filterPath.javaType

        return when {
            javaType.isEnum -> {
                val enumValue = runCatching {
                    BookCreatureType.valueOf(filterValue.trim().uppercase())
                }.getOrNull()
                if (enumValue != null) {
                    builder.equal(filterPath, enumValue)
                } else {
                    builder.disjunction()
                }
            }

            javaType == String::class.java -> {
                builder.like(
                    builder.lower(filterPath.`as`(String::class.java)),
                    "%${filterValue.trim().lowercase()}%"
                )
            }

            else -> {
                builder.equal(filterPath, filterValue)
            }
        }
    }


    fun findPage(pageable: Pageable,
                 filterColumn: String,
                 filterValue: String,
                 sortColumn: String): Page<T> {

        if (filterColumn.isNotBlank() && filterColumn !in allowedColumns)
            throw IllegalArgumentException("Invalid filter column: $filterColumn")

        val builder = entityManager.criteriaBuilder
        val mainQuery = builder.createQuery(entityClass)
        val root = mainQuery.from(entityClass)

        if (filterColumn.isNotBlank() && filterValue.isNotBlank())
            mainQuery.where(buildFilterPredicate(builder, root, filterColumn, filterValue))

        mainQuery.orderBy(builder.asc(resolvePath(root, sortColumn)))

        val query = entityManager.createQuery(mainQuery).apply {
            firstResult = pageable.offset.toInt()
            maxResults = pageable.pageSize
        }
        val content = query.resultList


        val countQuery = builder.createQuery(Long::class.java)
        val countRoot = countQuery.from(entityClass)

        if (filterColumn.isNotBlank() && filterValue.isNotBlank())
            countQuery.where(buildFilterPredicate(builder, countRoot, filterColumn, filterValue))

        countQuery.select(builder.count(countRoot))
        val total = entityManager.createQuery(countQuery).singleResult

        return PageImpl(content, pageable, total)
    }

    fun findAllIds(filterColumn: String,
                   filterValue: String,
                   sortColumn: String): List<Int> {
        if (filterColumn.isNotBlank() && filterColumn !in allowedColumns)
            throw IllegalArgumentException("Invalid filter column: $filterColumn")

        val builder = entityManager.criteriaBuilder
        val query = builder.createQuery(Int::class.java)
        val root = query.from(entityClass)

        if (filterColumn.isNotBlank() && filterValue.isNotBlank())
            query.where(buildFilterPredicate(builder, root, filterColumn, filterValue))

        query.orderBy(builder.asc(resolvePath(root, sortColumn)))
        query.select(root.get("id"))

        return entityManager.createQuery(query).resultList
    }

    fun save(entity: T): T {
        entityManager.persist(entity)
        return entity
    }

    fun update(entity: T): T {
        entityManager.merge(entity)
        return entity
    }

    fun deleteById(id: Int) {
        val entity = findById(id)
        entity?.let {
            entityManager.remove(it)
        }
    }

    fun findAll(): List<T> =
        entityManager.createQuery("SELECT e FROM ${entityClass.simpleName} e", entityClass)
            .resultList

    fun findFree(): List<T> {
        val fieldName = entityClass.simpleName.replaceFirstChar { it.lowercase() }
        val query = entityManager.createQuery(
            "SELECT e FROM ${entityClass.simpleName} e WHERE e.id NOT IN (SELECT b.$fieldName.id FROM BookCreature b WHERE b.$fieldName IS NOT NULL)",
            entityClass
        )
        return query.resultList
    }
}
