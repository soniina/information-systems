package itmo.sonina.creaturecatalog.models

import jakarta.persistence.*
import jakarta.validation.constraints.*

@Entity
@Table(name = "magic_cities")
data class MagicCity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    val id: Int,

    @field:NotBlank
    @Column(nullable = false)
    var name: String,

    @field:Min(1)
    @Column(nullable = false)
    var area: Long,

    @field:Min(1)
    @Column(nullable = false)
    var population: Int,

    @Column(nullable = false)
    var establishmentDate: java.util.Date,

    @Enumerated(EnumType.STRING)
    var governor: BookCreatureType? = null,

    @Column(nullable = false)
    var capital: Boolean,

    @field:Min(1)
    @Column(nullable = false)
    var populationDensity: Float
)
