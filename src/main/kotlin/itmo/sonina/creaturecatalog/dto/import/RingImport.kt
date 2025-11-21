package itmo.sonina.creaturecatalog.dto.import

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive

data class RingImport(
    @field:NotNull
    @field:NotBlank
    val name: String? = null,

    @field:NotNull
    @field:Positive
    val power: Int? = null,

    @field:Positive
    val weight: Long? = null
) : CrudImport