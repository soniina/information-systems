package itmo.sonina.bookcreatures.models

import jakarta.persistence.*
import jakarta.validation.constraints.*

@Entity
@Table(name = "rings")
data class Ring (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    val id: Int,

    @field:NotBlank
    @Column(nullable = false)
    var name: String,

    @field:Min(1)
    @Column(nullable = false)
    var power: Int,

    @field:Min(1)
    var weight: Long? = null
)
