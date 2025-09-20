package itmo.sonina.creaturecatalog.controllers

import itmo.sonina.creaturecatalog.dto.requests.BookCreatureRequest
import itmo.sonina.creaturecatalog.models.BookCreature
import itmo.sonina.creaturecatalog.services.BookCreatureService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/creatures")
class BookCreatureController(private val bookCreatureService: BookCreatureService): CrudController<BookCreature, BookCreatureRequest>(bookCreatureService) {

}