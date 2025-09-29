package itmo.sonina.creaturecatalog.controllers

import itmo.sonina.creaturecatalog.dto.requests.CoordinatesRequest
import itmo.sonina.creaturecatalog.models.Coordinates
import itmo.sonina.creaturecatalog.services.CoordinatesService
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/coordinates")
class CoordinatesController(coordinatesService: CoordinatesService): CrudController<Coordinates, CoordinatesRequest>(coordinatesService) {
    override val viewName = "coordinates"
    override fun createEmptyRequest() = CoordinatesRequest()
}