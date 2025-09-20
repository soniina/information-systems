package itmo.sonina.creaturecatalog.services

import itmo.sonina.creaturecatalog.dto.requests.MagicCityRequest
import itmo.sonina.creaturecatalog.models.MagicCity
import itmo.sonina.creaturecatalog.repositories.MagicCityRepository
import org.springframework.stereotype.Service

@Service
class MagicCityService(magicCityRepository: MagicCityRepository): CrudService<MagicCity, MagicCityRequest>(magicCityRepository) {

    override fun toEntity(request: MagicCityRequest) =
        MagicCity(
            name = request.name,
            area = request.area,
            population = request.population,
            establishmentDate = request.establishmentDate,
            governor = request.governor,
            capital = request.capital,
            populationDensity = request.populationDensity,
        )

    override fun updateEntity(entity: MagicCity, request: MagicCityRequest) {
        entity.name = request.name
        entity.area = request.area
        entity.population = request.population
        entity.establishmentDate = request.establishmentDate
        entity.governor = request.governor
        entity.capital = request.capital
        entity.populationDensity = request.populationDensity
    }

}
