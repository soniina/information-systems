package itmo.sonina.creaturecatalog.controllers

import itmo.sonina.creaturecatalog.dto.requests.RingRequest
import itmo.sonina.creaturecatalog.models.Ring
import itmo.sonina.creaturecatalog.services.RingService
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/rings")
class RingController(ringService: RingService): CrudController<Ring, RingRequest>(ringService) {
    override val viewName = "rings"
    override fun createEmptyRequest() = RingRequest()
}
