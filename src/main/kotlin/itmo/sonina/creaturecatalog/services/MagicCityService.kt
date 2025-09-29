package itmo.sonina.creaturecatalog.services

import itmo.sonina.creaturecatalog.dto.requests.MagicCityRequest
import itmo.sonina.creaturecatalog.models.MagicCity
import itmo.sonina.creaturecatalog.repositories.MagicCityRepository
import org.springframework.stereotype.Service

@Service
class MagicCityService(
    private val magicCityRepository: MagicCityRepository,
    cityRepository: MagicCityRepository
): CrudService<MagicCity, MagicCityRequest>(magicCityRepository) {

    override fun toEntity(request: MagicCityRequest) =
        MagicCity(
            name = requireNotNull(request.name) { "Name is required" },
            area = requireNotNull(request.area) { "Area is required" },
            population = requireNotNull(request.population) { "Population is required" },
            establishmentDate = requireNotNull(request.establishmentDate) { "Establishment date is required" },
            governor = request.governor,
            capital = request.capital,
            populationDensity = requireNotNull(request.populationDensity) { "Population is required" }
        )

    override fun updateEntity(entity: MagicCity, request: MagicCityRequest) {
        entity.apply {
            request.name?.let { name = it }
            request.area?.let { area = it }
            request.population?.let { population = it }
            request.establishmentDate?.let { establishmentDate = it }
            governor = request.governor
            capital = request.capital
            request.populationDensity?.let { populationDensity = it }
        }
    }

    override fun toRequest(entity: MagicCity)=
        MagicCityRequest (
            name = entity.name,
            area = entity.area,
            population = entity.population,
            establishmentDate = entity.establishmentDate,
            governor = entity.governor,
            capital = entity.capital,
            populationDensity = entity.populationDensity
        )

    fun getByName(name: String): MagicCity? = magicCityRepository.findByName(name)
}
