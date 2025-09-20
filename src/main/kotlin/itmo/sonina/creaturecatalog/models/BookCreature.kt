package itmo.sonina.creaturecatalog.models

import jakarta.persistence.*
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import java.time.ZonedDateTime

@Entity
@Table(name = "book_creatures")
data class BookCreature (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    val id: Int = 0,

    @Column(nullable = false)
    var name: String,

    @OneToOne(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    @JoinColumn(name = "coordinates_id", nullable = false)
    var coordinates: Coordinates,

    @Column(nullable = false)
    val creationDate: ZonedDateTime = ZonedDateTime.now(),

    @Column(nullable = false)
    var age: Long,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var creatureType: BookCreatureType,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creature_location_id", nullable = false)
    var creatureLocation: MagicCity,

    @Column(nullable = false)
    var attackLevel: Long,

    @OneToOne(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
    @JoinColumn(name = "ring_id")
    var ring: Ring? = null
)
