package itmo.sonina.creaturecatalog.services

import itmo.sonina.creaturecatalog.dto.requests.RingRequest
import itmo.sonina.creaturecatalog.models.Ring
import itmo.sonina.creaturecatalog.repositories.RingRepository
import org.springframework.stereotype.Service

@Service
class RingService(ringRepository: RingRepository): CrudService<Ring, RingRequest>(ringRepository) {

    override fun toEntity(request: RingRequest) =
        Ring(
            name = request.name,
            power = request.power,
            weight = request.weight
        )

    override fun updateEntity(entity: Ring, request: RingRequest) {
        entity.name = request.name
        entity.power = request.power
        entity.weight = request.weight
    }

}
