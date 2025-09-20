package itmo.sonina.creaturecatalog.dto.requests

import jakarta.validation.constraints.*

data class RingRequest(
    @field:NotBlank
    val name: String,

    @field:Positive
    val power: Int,

    @field:Positive
    val weight: Long? = null
): CrudRequest