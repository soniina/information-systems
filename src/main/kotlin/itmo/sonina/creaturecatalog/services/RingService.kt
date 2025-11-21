package itmo.sonina.creaturecatalog.services

import itmo.sonina.creaturecatalog.dto.import.RingImport
import itmo.sonina.creaturecatalog.dto.requests.RingRequest
import itmo.sonina.creaturecatalog.exceptions.UniqueConstraintException
import itmo.sonina.creaturecatalog.models.Ring
import itmo.sonina.creaturecatalog.repositories.RingRepository
import org.springframework.stereotype.Service

@Service
class RingService(private val ringRepository: RingRepository) :
    CrudService<Ring, RingRequest, RingImport>(ringRepository) {

    override fun toEntity(request: RingRequest): Ring {
        val name = requireNotNull(request.name) { "Name is required" }
        if (ringRepository.existsByName(name)) throw UniqueConstraintException(
            "name",
            "Ring with name '$name' already exists"
        )
        return Ring(
            name = name,
            power = requireNotNull(request.power) { "Power is required" },
            weight = request.weight
        )
    }

    override fun toEntity(import: RingImport): Ring {
        val name = requireNotNull(import.name) { "Name is required" }
        if (ringRepository.existsByName(name)) throw UniqueConstraintException(
            "name",
            "Ring with name '$name' already exists"
        )
        return Ring(
            name = name,
            power = requireNotNull(import.power) { "Power is required" },
            weight = import.weight
        )
    }

    override fun updateEntity(entity: Ring, request: RingRequest) {
        entity.apply {
            request.name?.let { name = it }
            request.power?.let { power = it }
            weight = request.weight
        }
    }

    override fun toRequest(entity: Ring) =
        RingRequest(
            name = entity.name,
            power = entity.power,
            weight = entity.weight
        )

}
