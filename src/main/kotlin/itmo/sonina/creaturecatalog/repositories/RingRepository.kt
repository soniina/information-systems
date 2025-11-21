package itmo.sonina.creaturecatalog.repositories

import itmo.sonina.creaturecatalog.models.Ring
import org.springframework.stereotype.Repository

@Repository
class RingRepository : CrudRepository<Ring>(Ring::class.java) {
    override val allowedColumns = setOf("id", "name", "power", "weight")

    fun existsByName(name: String) =
        entityManager.createQuery(
            "SELECT COUNT(r) FROM Ring r WHERE r.name = :name", Long::class.java
        ).setParameter("name", name).singleResult > 0
}