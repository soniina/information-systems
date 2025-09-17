package itmo.sonina.creaturecatalog.repositories

import itmo.sonina.creaturecatalog.models.Ring
import org.springframework.stereotype.Repository

@Repository
class RingRepository: CrudRepository<Ring>(Ring::class.java) {
    override val allowedColumns = setOf("id", "name", "power", "weight")
}