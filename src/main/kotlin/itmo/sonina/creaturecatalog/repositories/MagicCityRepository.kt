package itmo.sonina.creaturecatalog.repositories

import itmo.sonina.creaturecatalog.models.MagicCity
import org.springframework.stereotype.Repository

@Repository
class MagicCityRepository: CrudRepository<MagicCity>(MagicCity::class.java) {

    override val allowedColumns = setOf("id", "name", "area", "population", "establishmentDate", "governor",
        "capital", "populationDensity")

    fun findByName(name: String): MagicCity? =
        entityManager.createQuery(
            "SELECT m FROM MagicCity m WHERE m.name = :name", MagicCity::class.java
        ).setParameter("name", name)
            .resultList
            .firstOrNull()

}