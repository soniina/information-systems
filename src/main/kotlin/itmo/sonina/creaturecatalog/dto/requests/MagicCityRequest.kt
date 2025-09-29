package itmo.sonina.creaturecatalog.dto.requests

import itmo.sonina.creaturecatalog.models.BookCreatureType
import jakarta.validation.constraints.*
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDate

data class MagicCityRequest(
    @field:NotNull
    @field:NotBlank
    val name: String? = null,

    @field:NotNull
    @field:Positive
    val area: Long? = null,

    @field:NotNull
    @field:Positive
    val population: Int? = null,

    @field:NotNull
    @field:DateTimeFormat(pattern = "yyyy-MM-dd")
    val establishmentDate: LocalDate? = null,

    val governor: BookCreatureType? = null,

    val capital: Boolean = false,

    @field:NotNull
    @field:Positive
    val populationDensity: Float? = null
): CrudRequest