package itmo.sonina.creaturecatalog.controllers

import itmo.sonina.creaturecatalog.dto.requests.MagicCityRequest
import itmo.sonina.creaturecatalog.models.MagicCity
import itmo.sonina.creaturecatalog.services.MagicCityService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/cities")
class MagicCityController(magicCityService: MagicCityService): CrudController<MagicCity, MagicCityRequest>(magicCityService)
