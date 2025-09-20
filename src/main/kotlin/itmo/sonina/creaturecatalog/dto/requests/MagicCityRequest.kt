package itmo.sonina.creaturecatalog.dto.requests

import itmo.sonina.creaturecatalog.models.BookCreatureType
import jakarta.validation.constraints.*

data class MagicCityRequest(
    @field:NotBlank
    val name: String,

    @field:Positive
    val area: Long,

    @field:Positive
    val population: Int,

    val establishmentDate: java.util.Date,

    val governor: BookCreatureType? = null,

    val capital: Boolean,

    @field:Positive
    val populationDensity: Float
): CrudRequest