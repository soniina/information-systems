package itmo.sonina.creaturecatalog.dto.requests

import itmo.sonina.creaturecatalog.models.BookCreatureType
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive

data class BookCreatureRequest(
    @field:NotBlank
    val name: String,

    @field:Positive
    val coordinatesId: Int,

    @field:Positive
    val age: Long,

    @field: NotBlank
    val creatureType: BookCreatureType,

    @field:Positive
    val creatureLocationId: Int,

    @field:Positive
    val attackLevel: Long,

    @field:Positive
    val ringId: Int? = null
): CrudRequest