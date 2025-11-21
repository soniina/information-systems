package itmo.sonina.creaturecatalog.services

import itmo.sonina.creaturecatalog.dto.import.MagicCityImport
import itmo.sonina.creaturecatalog.dto.requests.MagicCityRequest
import itmo.sonina.creaturecatalog.exceptions.UniqueConstraintException
import itmo.sonina.creaturecatalog.models.MagicCity
import itmo.sonina.creaturecatalog.repositories.MagicCityRepository
import org.springframework.stereotype.Service

@Service
class MagicCityService(
    private val magicCityRepository: MagicCityRepository
) : CrudService<MagicCity, MagicCityRequest, MagicCityImport>(magicCityRepository) {

    override fun toEntity(request: MagicCityRequest): MagicCity {
        val name = requireNotNull(request.name) { "Name is required" }
        if (magicCityRepository.existsByName(name)) throw UniqueConstraintException(
            "name",
            "Magic City with name '$name' already exists"
        )
        return MagicCity(
            name = name,
            area = requireNotNull(request.area) { "Area is required" },
            population = requireNotNull(request.population) { "Population is required" },
            establishmentDate = requireNotNull(request.establishmentDate) { "Establishment date is required" },
            governor = request.governor,
            capital = request.capital,
            populationDensity = requireNotNull(request.populationDensity) { "Population is required" }
        )
    }

    override fun toEntity(import: MagicCityImport): MagicCity {
        val name = requireNotNull(import.name) { "Name is required" }
        if (magicCityRepository.existsByName(name)) throw UniqueConstraintException(
            "name",
            "Magic City with name '$name' already exists"
        )
        return MagicCity(
            name = name,
            area = requireNotNull(import.area) { "Area is required" },
            population = requireNotNull(import.population) { "Population is required" },
            establishmentDate = requireNotNull(import.establishmentDate) { "Establishment date is required" },
            governor = import.governor,
            capital = import.capital,
            populationDensity = requireNotNull(import.populationDensity) { "Population is required" }
        )
    }

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

    override fun toRequest(entity: MagicCity) =
        MagicCityRequest(
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
