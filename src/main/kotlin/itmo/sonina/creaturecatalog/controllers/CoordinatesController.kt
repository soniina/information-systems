package itmo.sonina.creaturecatalog.controllers

import itmo.sonina.creaturecatalog.dto.requests.CoordinatesRequest
import itmo.sonina.creaturecatalog.models.Coordinates
import itmo.sonina.creaturecatalog.services.CoordinatesService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/coordinates")
class CoordinatesController(coordinatesService: CoordinatesService): CrudController<Coordinates, CoordinatesRequest>(coordinatesService)