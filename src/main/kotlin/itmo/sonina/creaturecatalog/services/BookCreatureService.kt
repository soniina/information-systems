package itmo.sonina.creaturecatalog.services

import itmo.sonina.creaturecatalog.dto.requests.BookCreatureRequest
import itmo.sonina.creaturecatalog.models.BookCreature
import itmo.sonina.creaturecatalog.repositories.BookCreatureRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service


@Service
class BookCreatureService(bookCreatureRepository: BookCreatureRepository, private val coordinatesService: CoordinatesService, private val magicCityService: MagicCityService, private val ringService: RingService): CrudService<BookCreature, BookCreatureRequest>(bookCreatureRepository) {

    private fun getCoordinatesById(id: Int) = coordinatesService.getById(id) ?: throw EntityNotFoundException("Coordinates not found with id: $id")

    private fun getMagicCityById(id: Int) = magicCityService.getById(id) ?: throw EntityNotFoundException("MagicCity not found with id: $id")

    private fun getRingById(id: Int?) =
        id?.let { ringId ->
            ringService.getById(ringId) ?: throw EntityNotFoundException("Ring not found with id: $ringId")
        }

    override fun toEntity(request: BookCreatureRequest) =
         BookCreature(
            name = request.name,
            coordinates = getCoordinatesById(request.coordinatesId),
            age = request.age,
            creatureType = request.creatureType,
            creatureLocation = getMagicCityById(request.creatureLocationId),
            attackLevel = request.attackLevel,
            ring = getRingById(request.ringId),
        )

    override fun updateEntity(entity: BookCreature, request: BookCreatureRequest) {
        entity.name = request.name
        entity.coordinates = getCoordinatesById(request.coordinatesId)
        entity.age = request.age
        entity.creatureType = request.creatureType
        entity.creatureLocation = getMagicCityById(request.creatureLocationId)
        entity.attackLevel = request.attackLevel
        entity.ring = getRingById(request.ringId)
    }

}
