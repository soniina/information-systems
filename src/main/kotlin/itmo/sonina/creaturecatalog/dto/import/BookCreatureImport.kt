package itmo.sonina.creaturecatalog.dto.import

import itmo.sonina.creaturecatalog.models.BookCreatureType
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive

data class BookCreatureImport(
    @field:NotBlank
    val name: String? = null,

    @field:NotNull
    @field:Valid
    val coordinates: CoordinatesImport? = null,

    @field:NotNull
    @field:Positive
    val age: Long? = null,

    @field:NotNull
    val creatureType: BookCreatureType? = null,

    @field:NotNull
    @field:Valid
    val creatureLocation: MagicCityImport? = null,

    @field:NotNull
    @field:Positive
    val attackLevel: Long? = null,

    @field:NotNull
    @field:Valid
    val ring: RingImport? = null
) : CrudImport