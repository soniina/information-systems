package itmo.sonina.creaturecatalog.services

import itmo.sonina.creaturecatalog.dto.requests.RingRequest
import itmo.sonina.creaturecatalog.models.Ring
import itmo.sonina.creaturecatalog.repositories.RingRepository
import org.springframework.stereotype.Service

@Service
class RingService(ringRepository: RingRepository): CrudService<Ring, RingRequest>(ringRepository) {

    override fun toEntity(request: RingRequest) =
        Ring(
            name = requireNotNull(request.name) { "Name is required" },
            power = requireNotNull(request.power) { "Power is required" },
            weight = request.weight
        )

    override fun updateEntity(entity: Ring, request: RingRequest) {
        entity.apply {
            request.name?.let { name = it }
            request.power?.let { power = it }
            weight = request.weight
        }
    }

    override fun toRequest(entity: Ring) =
        RingRequest (
            name = entity.name,
            power = entity.power,
            weight = entity.weight
        )

}
