package itmo.sonina.creaturecatalog.dto.requests

import itmo.sonina.creaturecatalog.models.BookCreatureType
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive

data class BookCreatureRequest(
    @field:NotNull
    @field:NotBlank
    val name: String? = null,

    @field:NotNull
    @field:Positive
    val coordinatesId: Int? = null,

    @field:NotNull
    @field:Positive
    val age: Long? = null,

    @field:NotNull
    @field: NotBlank
    val creatureType: BookCreatureType? = null,

    @field:NotNull
    @field:Positive
    val creatureLocationId: Int? = null,

    @field:NotNull
    @field:Positive
    val attackLevel: Long? = null,

    @field:Positive
    val ringId: Int? = null
): CrudRequest