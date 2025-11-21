package itmo.sonina.creaturecatalog.services

import itmo.sonina.creaturecatalog.dto.import.BookCreatureImport
import itmo.sonina.creaturecatalog.dto.requests.BookCreatureRequest
import itmo.sonina.creaturecatalog.exceptions.UniqueConstraintException
import itmo.sonina.creaturecatalog.models.BookCreature
import itmo.sonina.creaturecatalog.repositories.BookCreatureRepository
import jakarta.persistence.EntityNotFoundException
import org.springframework.dao.ConcurrencyFailureException
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional
import java.sql.SQLException


@Service
class BookCreatureService(
    private val bookCreatureRepository: BookCreatureRepository,
    private val coordinatesService: CoordinatesService,
    private val magicCityService: MagicCityService,
    private val ringService: RingService
) : CrudService<BookCreature, BookCreatureRequest, BookCreatureImport>(bookCreatureRepository) {

    private fun getCoordinatesById(id: Int) =
        coordinatesService.getById(id)

    private fun getMagicCityById(id: Int) =
        magicCityService.getById(id)

    private fun getMagicCityByName(name: String) =
        magicCityService.getByName(name)

    private fun getRingById(id: Int?) =
        id?.let { ringId ->
            ringService.getById(ringId)
        }

    override fun toEntity(request: BookCreatureRequest): BookCreature {
        val name = requireNotNull(request.name) { "Name is required" }
        val coordinatesId = requireNotNull(request.coordinatesId) { "Coordinates are required" }
        val age = requireNotNull(request.age) { "Age is required" }
        val creatureType = requireNotNull(request.creatureType) { "CreatureType is required" }
        val locationId = requireNotNull(request.creatureLocationId) { "Location id is required" }
        val location =
            getMagicCityById(locationId) ?: throw EntityNotFoundException("MagicCity not found with id: $locationId")
        val attackLevel = requireNotNull(request.attackLevel) { "AttackLevel is required" }

        if (bookCreatureRepository.existsByNameAndCreatureTypeAndAgeAndCreatureLocationIdAndAttackLevel(
                name,
                creatureType,
                age,
                locationId,
                attackLevel
            )
        )
            throw UniqueConstraintException(
                "name",
                "${
                    creatureType.name.lowercase().replaceFirstChar { it.uppercase() }
                } with name '$name', age=$age and attack level=$attackLevel in magic city '${location.name}' already exists"
            )

        return BookCreature(
            name = name,
            coordinates = getCoordinatesById(coordinatesId)
                ?: throw EntityNotFoundException("Coordinates not found with id: $coordinatesId"),
            age = age,
            creatureType = creatureType,
            creatureLocation = location,
            attackLevel = attackLevel,
            ring = request.ringId?.let {
                getRingById(it) ?: throw EntityNotFoundException("Ring not found with id: $it")
            },
        )
    }

    override fun toEntity(import: BookCreatureImport): BookCreature {
        val name = requireNotNull(import.name) { "Name is required" }
        val coordinates =
            coordinatesService.createFromImport(requireNotNull(import.coordinates) { "Coordinates are required" })
        val age = requireNotNull(import.age) { "Age is required" }
        val creatureType = requireNotNull(import.creatureType) { "CreatureType is required" }
        val locationImport = requireNotNull(import.creatureLocation) { "Location is required" }
        val location = getMagicCityByName(requireNotNull(locationImport.name) { "Location name is required" })
            ?: magicCityService.createFromImport(locationImport)
        val attackLevel = requireNotNull(import.attackLevel) { "AttackLevel is required" }

        if (bookCreatureRepository.existsByNameAndCreatureTypeAndAgeAndCreatureLocationIdAndAttackLevel(
                name,
                creatureType,
                age,
                location.id,
                attackLevel
            )
        )
            throw UniqueConstraintException(
                "name",
                "${
                    creatureType.name.lowercase().replaceFirstChar { it.uppercase() }
                } with name '$name', age=$age, attack level=$attackLevel in magic city '${location.name}' already exists"
            )

        return BookCreature(
            name = name,
            coordinates = coordinates,
            age = age,
            creatureType = creatureType,
            creatureLocation = location,
            attackLevel = attackLevel,
            ring = import.ring?.let { ringService.createFromImport(it) }
        )
    }

    override fun updateEntity(entity: BookCreature, request: BookCreatureRequest) {
        entity.apply {
            request.name?.let { name = it }
            request.coordinatesId?.let {
                coordinates =
                    getCoordinatesById(it) ?: throw EntityNotFoundException("Coordinates not found with id: $it")
            }
            request.age?.let { age = it }
            request.creatureType?.let { creatureType = it }
            request.creatureLocationId?.let {
                creatureLocation =
                    getMagicCityById(it) ?: throw EntityNotFoundException("MagicCity not found with id: $it")
            }
            request.attackLevel?.let { attackLevel = it }
            ring = request.ringId?.let { getRingById(it) }
        }
    }

    override fun toRequest(entity: BookCreature) =
        BookCreatureRequest(
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

    @Transactional
    fun removeAllRingsFromHobbits() = bookCreatureRepository.removeAllRingsFromHobbits()

    @Transactional
    fun moveHobbitsWithRingsToMordor(): Boolean {
        val city = magicCityService.getByName("Mordor") ?: return false
        bookCreatureRepository.moveHobbitsWithRingsToMordor(city)
        return true
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    @Retryable(
        value = [ConcurrencyFailureException::class, SQLException::class],
        maxAttempts = 3,
        backoff = Backoff(delay = 100)
    )
    fun createAllFromImport(creatures: List<BookCreatureImport>) {
        creatures.forEach {
            createFromImport(it)
        }
    }
}
