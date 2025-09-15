package itmo.sonina.creaturecatalog.repositories

import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext

abstract class CrudRepository<T : Any>(private val entityClass: Class<T>) {

    @PersistenceContext
    protected lateinit var entityManager: EntityManager

    fun findById(id: Int): T? = entityManager.find(entityClass, id)

    fun findPage(page: Int, size: Int): List<T> =
        entityManager.createQuery("SELECT e FROM ${entityClass.simpleName} e", entityClass)
            .setFirstResult(page * size)
            .setMaxResults(size)
            .resultList

    fun save(entity: T) = entityManager.persist(entity)

    fun update(entity: T) = entityManager.merge(entity)

    fun deleteById(id: Int) {
        val entity = findById(id)
        entity?.let {
            entityManager.remove(it)
        }
    }
}
