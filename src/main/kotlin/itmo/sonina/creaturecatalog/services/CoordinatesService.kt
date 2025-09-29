package itmo.sonina.creaturecatalog.services

import itmo.sonina.creaturecatalog.dto.requests.CoordinatesRequest
import itmo.sonina.creaturecatalog.models.Coordinates
import itmo.sonina.creaturecatalog.repositories.CoordinatesRepository
import org.springframework.stereotype.Service

@Service
class CoordinatesService(coordinatesRepository: CoordinatesRepository): CrudService<Coordinates, CoordinatesRequest>(coordinatesRepository) {

    override fun toEntity(request: CoordinatesRequest) =
        Coordinates(
            x = requireNotNull(request.x) { "X is required" },
            y = requireNotNull(request.y) { "Y is required" }
        )

    override fun updateEntity(entity: Coordinates, request: CoordinatesRequest) {
        entity.apply {
            x = request.x ?: x
            y = request.y ?: y
        }
    }

    override fun toRequest(entity: Coordinates) =
        CoordinatesRequest(
            x = entity.x,
            y = entity.y
        )

}
