package itmo.sonina.creaturecatalog.repositories

import itmo.sonina.creaturecatalog.models.BookCreature
import itmo.sonina.creaturecatalog.models.BookCreatureType
import itmo.sonina.creaturecatalog.models.Coordinates
import itmo.sonina.creaturecatalog.models.MagicCity
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.ZonedDateTime

@Repository
class BookCreatureRepository : CrudRepository<BookCreature>(BookCreature::class.java) {

    override val allowedColumns = setOf(
        "id", "name", "coordinates.x", "coordinates.y", "age",
        "creatureType", "creatureLocation.name", "creatureLocation.capital", "attackLevel", "ring.name", "ring.power"
    )

    fun findWithMinAge(): BookCreature? =
        entityManager.createQuery(
            "SELECT c FROM BookCreature c WHERE c.age = (SELECT MIN(c2.age) FROM BookCreature c2)",
            BookCreature::class.java
        ).resultList.firstOrNull()

    fun countByCreationDate(): Map<ZonedDateTime, Long> =
        entityManager.createQuery(
            "SELECT c.creationDate, COUNT(c) FROM BookCreature c GROUP BY c.creationDate",
            Array<Any>::class.java
        ).resultList.associate { (date, count) -> date as ZonedDateTime to count as Long }

    fun countByRingPowerLessThan(value: Int): Int =
        entityManager.createQuery(
            "SELECT COUNT(c) FROM BookCreature c WHERE c.ring IS NOT NULL AND c.ring.power < :value",
            java.lang.Long::class.java
        ).setParameter("value", value).singleResult.toInt()

    fun removeAllRingsFromHobbits() {
        entityManager.createQuery("UPDATE BookCreature c SET c.ring = NULL WHERE c.creatureType = :hobbitType")
            .setParameter("hobbitType", BookCreatureType.HOBBIT)
            .executeUpdate()
    }

    fun moveHobbitsWithRingsToMordor(mordor: MagicCity) {
        entityManager.createQuery("UPDATE BookCreature c SET c.creatureLocation = :mordor WHERE c.creatureType = :hobbitType AND c.ring IS NOT NULL")
            .setParameter("mordor", mordor)
            .setParameter("hobbitType", BookCreatureType.HOBBIT)
            .executeUpdate()
    }

    fun existsByNameAndCreatureTypeAndAgeAndCreatureLocationIdAndAttackLevel(
        name: String,
        type: BookCreatureType,
        age: Long,
        locationId: Int,
        attackLevel: Long
    ): Boolean =
        entityManager.createQuery(
            "SELECT COUNT(b) FROM BookCreature b WHERE b.name = :name AND b.creatureType = :type AND b.age = :age AND b.creatureLocation.id = :locationId AND b.attackLevel = :attackLevel",
            Long::class.java
        ).setParameter("name", name).setParameter("type", type).setParameter("age", age)
            .setParameter("locationId", locationId).setParameter("attackLevel", attackLevel).singleResult > 0
}