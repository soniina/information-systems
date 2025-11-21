package itmo.sonina.creaturecatalog.services

import itmo.sonina.creaturecatalog.dto.import.CoordinatesImport
import itmo.sonina.creaturecatalog.dto.requests.CoordinatesRequest
import itmo.sonina.creaturecatalog.exceptions.UniqueConstraintException
import itmo.sonina.creaturecatalog.models.Coordinates
import itmo.sonina.creaturecatalog.repositories.CoordinatesRepository
import org.springframework.stereotype.Service

@Service
class CoordinatesService(private val coordinatesRepository: CoordinatesRepository) :
    CrudService<Coordinates, CoordinatesRequest, CoordinatesImport>(coordinatesRepository) {

    override fun toEntity(request: CoordinatesRequest): Coordinates {
        val x = requireNotNull(request.x) { "X is required" }
        val y = requireNotNull(request.y) { "Y is required" }
        if (coordinatesRepository.existsByXAndY(x, y)) throw UniqueConstraintException(
            "x",
            "Coordinates with x=$x and y=$y already exists"
        )
        return Coordinates(
            x = x,
            y = y
        )
    }

    override fun toEntity(import: CoordinatesImport): Coordinates {
        val x = requireNotNull(import.x) { "X is required" }
        val y = requireNotNull(import.y) { "Y is required" }
        if (coordinatesRepository.existsByXAndY(x, y)) throw UniqueConstraintException(
            "x",
            "Coordinates with x=$x and y=$y already exists"
        )
        return Coordinates(
            x = x,
            y = y
        )
    }

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
