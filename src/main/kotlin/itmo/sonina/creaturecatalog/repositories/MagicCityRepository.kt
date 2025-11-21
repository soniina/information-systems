package itmo.sonina.creaturecatalog.repositories

import itmo.sonina.creaturecatalog.models.MagicCity
import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.Lock
import org.springframework.stereotype.Repository

@Repository
class MagicCityRepository : CrudRepository<MagicCity>(MagicCity::class.java) {

    override val allowedColumns = setOf(
        "id", "name", "area", "population", "establishmentDate", "governor",
        "capital", "populationDensity"
    )

    fun findByName(name: String) =
        entityManager.createQuery(
            "SELECT m FROM MagicCity m WHERE m.name = :name", MagicCity::class.java
        ).setParameter("name", name)
            .resultList
            .firstOrNull()

    fun existsByName(name: String) =
        entityManager.createQuery(
            "SELECT COUNT(m) FROM MagicCity m WHERE m.name = :name", Long::class.java
        ).setParameter("name", name).singleResult > 0
    
}