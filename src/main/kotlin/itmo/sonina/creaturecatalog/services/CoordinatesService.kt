package itmo.sonina.creaturecatalog.services

import itmo.sonina.creaturecatalog.dto.requests.CoordinatesRequest
import itmo.sonina.creaturecatalog.models.Coordinates
import itmo.sonina.creaturecatalog.repositories.CoordinatesRepository
import org.springframework.stereotype.Service

@Service
class CoordinatesService(coordinatesRepository: CoordinatesRepository): CrudService<Coordinates, CoordinatesRequest>(coordinatesRepository) {

    override fun toEntity(request: CoordinatesRequest) =
        Coordinates(
            x = request.x,
            y = request.y
        )

    override fun updateEntity(entity: Coordinates, request: CoordinatesRequest) {
        entity.x = request.x
        entity.y = request.y
    }

}
