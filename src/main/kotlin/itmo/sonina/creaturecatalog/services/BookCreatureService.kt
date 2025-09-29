package itmo.sonina.creaturecatalog.services

import itmo.sonina.creaturecatalog.dto.requests.BookCreatureRequest
import itmo.sonina.creaturecatalog.models.BookCreature
import itmo.sonina.creaturecatalog.repositories.BookCreatureRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service


@Service
class BookCreatureService(
    private val bookCreatureRepository: BookCreatureRepository,
    private val coordinatesService: CoordinatesService,
    private val magicCityService: MagicCityService,
    private val ringService: RingService
): CrudService<BookCreature, BookCreatureRequest>(bookCreatureRepository) {

    private fun getCoordinatesById(id: Int) = coordinatesService.getById(id) ?: throw EntityNotFoundException("Coordinates not found with id: $id")

    private fun getMagicCityById(id: Int) = magicCityService.getById(id) ?: throw EntityNotFoundException("MagicCity not found with id: $id")

    private fun getRingById(id: Int?) =
        id?.let { ringId ->
            ringService.getById(ringId) ?: throw EntityNotFoundException("Ring not found with id: $ringId")
        }

    override fun toEntity(request: BookCreatureRequest) =
        BookCreature(
            name = requireNotNull(request.name) { "Name is required" },
            coordinates = getCoordinatesById(requireNotNull(request.coordinatesId) { "Coordinates are required" }),
            age = requireNotNull(request.age) { "Age is required" },
            creatureType = requireNotNull(request.creatureType) { "CreatureType is required" },
            creatureLocation = getMagicCityById(requireNotNull(request.creatureLocationId) { "Location is required" }),
            attackLevel = requireNotNull(request.attackLevel) { "AttackLevel is required" },
            ring = request.ringId?.let { getRingById(it) } // optional
        )

    override fun updateEntity(entity: BookCreature, request: BookCreatureRequest) {
        entity.apply {
            request.name?.let { name = it }
            request.coordinatesId?.let { coordinates = getCoordinatesById(it) }
            request.age?.let { age = it }
            request.creatureType?.let { creatureType = it }
            request.creatureLocationId?.let { creatureLocation = getMagicCityById(it) }
            request.attackLevel?.let { attackLevel = it }
            ring = request.ringId?.let { getRingById(it) }
        }
    }

    override fun toRequest(entity: BookCreature)=
        BookCreatureRequest (
            name = entity.name,
            coordinatesId = entity.coordinates.id,
            age = entity.age,
            creatureType = entity.creatureType,
            creatureLocationId = entity.creatureLocation.id,
            attackLevel = entity.attackLevel,
            ringId = entity.ring?.id,
    )

    fun getFreeCoordinates() = coordinatesService.getFree()
    fun getAllCities() = magicCityService.getAll()
    fun getFreeRings() = ringService.getFree()

    fun getWithMinAge() = bookCreatureRepository.findWithMinAge()
    fun countByCreationDate() = bookCreatureRepository.countByCreationDate()
    fun countByRingPowerLessThan(value: Int) = bookCreatureRepository.countByRingPowerLessThan(value)
    fun removeAllRingsFromHobbits() = bookCreatureRepository.removeAllRingsFromHobbits()
    fun moveHobbitsWithRingsToMordor(): Boolean {
        val city = magicCityService.getByName("Mordor") ?: return false
        bookCreatureRepository.moveHobbitsWithRingsToMordor(city)
        return true
    }

}
