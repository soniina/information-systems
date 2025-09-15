package itmo.sonina.creaturecatalog

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CreatureCatalogApplication

fun main(args: Array<String>) {
    runApplication<CreatureCatalogApplication>(*args)
}
