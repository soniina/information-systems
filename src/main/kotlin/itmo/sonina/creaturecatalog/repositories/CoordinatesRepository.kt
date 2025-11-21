package itmo.sonina.creaturecatalog.repositories

import itmo.sonina.creaturecatalog.models.Coordinates
import org.springframework.stereotype.Repository

@Repository
class CoordinatesRepository : CrudRepository<Coordinates>(Coordinates::class.java) {
    override val allowedColumns = setOf("id", "x", "y")

    fun existsByXAndY(x: Float, y: Long): Boolean =
        entityManager.createQuery(
            "SELECT COUNT(c) FROM Coordinates c WHERE c.x = :x AND c.y = :y", Long::class.java
        ).setParameter("x", x).setParameter("y", y).singleResult > 0

}