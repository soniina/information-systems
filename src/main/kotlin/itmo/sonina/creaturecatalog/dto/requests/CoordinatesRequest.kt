package itmo.sonina.creaturecatalog.dto.requests

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotNull

data class CoordinatesRequest (
    @field:NotNull
    val x: Float? = null,

    @field:NotNull
    val y: Long? = null
): CrudRequest