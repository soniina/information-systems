package itmo.sonina.creaturecatalog.controllers

import itmo.sonina.creaturecatalog.dto.import.BookCreatureImport
import itmo.sonina.creaturecatalog.dto.requests.BookCreatureRequest
import itmo.sonina.creaturecatalog.models.BookCreature
import itmo.sonina.creaturecatalog.models.BookCreatureType
import itmo.sonina.creaturecatalog.services.BookCreatureService
import jakarta.servlet.http.HttpSession
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import org.springframework.web.server.ResponseStatusException

@Controller
@RequestMapping("/creatures")
class BookCreatureController(private val bookCreatureService: BookCreatureService) :
    CrudController<BookCreature, BookCreatureRequest, BookCreatureImport>(bookCreatureService) {
    override val viewName = "creatures"
    override fun createEmptyRequest() = BookCreatureRequest()

    @PostMapping("/saveDraft")
    fun saveDraft(
        @RequestParam name: String?,
        @RequestParam age: Long?,
        @RequestParam attackLevel: Long?,
        @RequestParam creatureType: BookCreatureType?,
        @RequestParam coordinatesId: Int?,
        @RequestParam creatureLocationId: Int?,
        @RequestParam ringId: Int?,
        @RequestParam redirectUrl: String,
        session: HttpSession
    ): String {
        val draft = BookCreatureRequest(
            name = name,
            coordinatesId = coordinatesId,
            age = age,
            creatureType = creatureType,
            creatureLocationId = creatureLocationId,
            attackLevel = attackLevel,
            ringId = ringId
        )

        session.setAttribute("creatureDraft", draft)

        return "redirect:$redirectUrl"
    }

    @GetMapping("/create")
    override fun showCreateForm(model: Model): String {
        val request = (RequestContextHolder.getRequestAttributes() as ServletRequestAttributes).request
        val coordinatesId = request.getParameter("coordinatesId")?.toIntOrNull()
        val citiesId = request.getParameter("citiesId")?.toIntOrNull()
        val ringsId = request.getParameter("ringsId")?.toIntOrNull()

        val session = request.session
        val draft = session.getAttribute("creatureDraft") as? BookCreatureRequest

        val creatureRequest = draft?.copy(
            coordinatesId = coordinatesId ?: draft.coordinatesId,
            creatureLocationId = citiesId ?: draft.creatureLocationId,
            ringId = ringsId ?: draft.ringId
        ) ?: BookCreatureRequest(
            coordinatesId = coordinatesId ?: 0,
            creatureLocationId = citiesId ?: 0,
            ringId = ringsId
        )

        session.removeAttribute("creatureDraft")

        model.addAttribute("entity", creatureRequest)
        model.addAttribute("coordinatesList", bookCreatureService.getFreeCoordinates())
        model.addAttribute("magicCities", bookCreatureService.getAllCities())
        model.addAttribute("rings", bookCreatureService.getFreeRings())

        return "$viewName/create"
    }

    @GetMapping("/edit/{id}")
    override fun showEditForm(@PathVariable id: Int, model: Model): String {
        val entity =
            bookCreatureService.getById(id) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Entity not found")

        val request = (RequestContextHolder.getRequestAttributes() as ServletRequestAttributes).request
        val coordinatesId = request.getParameter("coordinatesId")?.toIntOrNull()
        val citiesId = request.getParameter("citiesId")?.toIntOrNull()
        val ringsId = request.getParameter("ringsId")?.toIntOrNull()

        val session = request.session
        val draft = session.getAttribute("creatureDraft") as? BookCreatureRequest

        val baseRequest = bookCreatureService.toRequest(entity)

        val creatureRequest = draft?.copy(
            coordinatesId = coordinatesId ?: draft.coordinatesId,
            creatureLocationId = citiesId ?: draft.creatureLocationId,
            ringId = ringsId ?: draft.ringId
        ) ?: baseRequest.copy(
            coordinatesId = coordinatesId ?: baseRequest.coordinatesId,
            creatureLocationId = citiesId ?: baseRequest.creatureLocationId,
            ringId = ringsId ?: baseRequest.ringId
        )

        session.removeAttribute("creatureDraft")

        model.addAttribute("id", id)
        model.addAttribute("entity", creatureRequest)
        model.addAttribute(
            "coordinatesList",
            (bookCreatureService.getFreeCoordinates() + listOfNotNull(entity.coordinates))
        )
        model.addAttribute("magicCities", bookCreatureService.getAllCities())
        model.addAttribute(
            "rings",
            (bookCreatureService.getFreeRings() + listOfNotNull(entity.ring))
        )

        return "$viewName/edit"
    }

}