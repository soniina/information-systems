package itmo.sonina.creaturecatalog.dto.import

import jakarta.validation.constraints.NotNull

data class CoordinatesImport(
    @field:NotNull
    val x: Float? = null,

    @field:NotNull
    val y: Long? = null
) : CrudImport