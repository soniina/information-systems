package itmo.sonina.creaturecatalog.dto.requests

import jakarta.validation.constraints.*

data class RingRequest(
    @field:NotNull
    @field:NotBlank
    val name: String? = null,

    @field:NotNull
    @field:Positive
    val power: Int? = null,

    @field:Positive
    val weight: Long? = null
): CrudRequest