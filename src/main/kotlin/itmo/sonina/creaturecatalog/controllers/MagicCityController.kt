package itmo.sonina.creaturecatalog.controllers

import itmo.sonina.creaturecatalog.dto.import.MagicCityImport
import itmo.sonina.creaturecatalog.dto.requests.MagicCityRequest
import itmo.sonina.creaturecatalog.models.MagicCity
import itmo.sonina.creaturecatalog.services.MagicCityService
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/cities")
class MagicCityController(magicCityService: MagicCityService) :
    CrudController<MagicCity, MagicCityRequest, MagicCityImport>(magicCityService) {
    override val viewName = "cities"
    override fun createEmptyRequest() = MagicCityRequest()
}
