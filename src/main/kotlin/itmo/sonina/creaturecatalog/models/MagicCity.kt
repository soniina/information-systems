package itmo.sonina.creaturecatalog.models

import jakarta.persistence.*

@Entity
@Table(name = "magic_cities")
data class MagicCity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    val id: Int = 0,

    @Column(nullable = false)
    var name: String,

    @Column(nullable = false)
    var area: Long,

    @Column(nullable = false)
    var population: Int,

    @Column(nullable = false)
    var establishmentDate: java.util.Date,

    @Enumerated(EnumType.STRING)
    var governor: BookCreatureType? = null,

    @Column(nullable = false)
    var capital: Boolean,

    @Column(nullable = false)
    var populationDensity: Float
)
