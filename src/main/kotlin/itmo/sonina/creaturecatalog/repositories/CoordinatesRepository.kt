package itmo.sonina.creaturecatalog.repositories

import itmo.sonina.creaturecatalog.models.Coordinates
import org.springframework.stereotype.Repository

@Repository
class CoordinatesRepository: CrudRepository<Coordinates>(Coordinates::class.java) {
    override val allowedColumns = setOf("id", "x", "y")
}