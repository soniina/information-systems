package itmo.sonina.creaturecatalog.controllers

import itmo.sonina.creaturecatalog.services.BookCreatureService
import itmo.sonina.creaturecatalog.services.MagicCityService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/creatures/special")
class SpecialOperationsController(private val bookCreatureService: BookCreatureService, private val magicCityService: MagicCityService) {

    @GetMapping
    fun showSpecialPage(): String {
        return "creatures/special"
    }

    @GetMapping("/min-age")
    fun findWithMinAge(model: Model): String {
        model.addAttribute("minAgeResult", bookCreatureService.getWithMinAge())
        return "creatures/special"
    }

    @GetMapping("/group-by-creation-date")
    fun groupByCreationDate(model: Model): String {
        model.addAttribute("groupByDateResult", bookCreatureService.countByCreationDate())
        return "creatures/special"
    }

    @GetMapping("/count-by-ring")
    fun countByRingPowerLessThan(@RequestParam ringPower: Int, model: Model): String {
        model.addAttribute("countByRingResult", bookCreatureService.countByRingPowerLessThan(ringPower))
        return "creatures/special"
    }

    @GetMapping("/remove-rings")
    fun removeAllRingsFromHobbits(model: Model): String {
        bookCreatureService.removeAllRingsFromHobbits()
        model.addAttribute("removeRingsResult", "All rings have been successfully removed from hobbits!")
        return "creatures/special"
    }

    @GetMapping("/move-to-mordor")
    fun moveHobbitsWithRingsToMordor(model: Model): String {
        val result = bookCreatureService.moveHobbitsWithRingsToMordor()
        if (result) {
            model.addAttribute("moveToMordorResult", "Hobbits with rings have been moved to Mordor!")
            model.addAttribute("moveToMordorResultType", "success")
        } else {
            model.addAttribute("moveToMordorResult", "Error: Mordor city not found!")
            model.addAttribute("moveToMordorResultType", "error")
        }
        return "creatures/special"
    }
}