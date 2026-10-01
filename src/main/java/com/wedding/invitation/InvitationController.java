package com.wedding.invitation;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.StringJoiner;

@Controller
public class InvitationController {

    private final GuestRepository guestRepository;

    public InvitationController(GuestRepository guestRepository) {
        this.guestRepository = guestRepository;
    }

    @GetMapping("/")
    public String invitation(Model model) {
        model.addAttribute("groom", "Артём");
        model.addAttribute("bride", "Виктория");
        model.addAttribute("date", "12 ноября 2026");
        model.addAttribute("time", "12:40");
        model.addAttribute("address", "г. Санкт-Петербург, проспект Славы, 31");
        model.addAttribute("mapUrl", "https://yandex.ru/maps/-/CXUBz8Lt");
        model.addAttribute("guest", new Guest());
        return "invitation";
    }

    @PostMapping("/rsvp")
    public String submitRsvp(@ModelAttribute("guest") Guest guest,
                             @RequestParam(value = "alcoholPreference", required = false) List<String> alcoholList,
                             RedirectAttributes redirectAttributes) {
        try {
            if (alcoholList != null && !alcoholList.isEmpty()) {
                StringJoiner joiner = new StringJoiner(",");
                for (String alcohol : alcoholList) {
                    joiner.add(alcohol);
                }
                guest.setAlcoholPreference(joiner.toString());
            } else {
                guest.setAlcoholPreference("none");
            }

            guestRepository.save(guest);
            redirectAttributes.addFlashAttribute("success", true);
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Произошла ошибка: " + e.getMessage());
        }
        return "redirect:/";
    }

    @GetMapping("/admin")
    public String admin(Model model) {
        List<Guest> guests = guestRepository.findAllByOrderByCreatedAtDesc();
        model.addAttribute("guests", guests);
        return "admin";
    }

    @PostMapping("/admin/clear")
    public String clearGuests(RedirectAttributes redirectAttributes) {
        guestRepository.deleteAll();
        redirectAttributes.addFlashAttribute("success", "Список гостей очищен");
        return "redirect:/admin";
    }

    @PostMapping("/admin/delete/{id}")
    public String deleteGuest(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        guestRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Гость удалён");
        return "redirect:/admin";
    }
}