package itmo.sonina.creaturecatalog.models

import jakarta.persistence.*

@Entity
@Table(name = "coordinates")
data class Coordinates(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    val id: Int,

    @Column(nullable = false)
    var x: Float,

    @Column(nullable = false)
    var y: Long
)
