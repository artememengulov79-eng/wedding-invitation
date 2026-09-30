package com.wedding.invitation;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class InvitationController {

    private final GuestRepository guestRepository;
    private final EmailService emailService;

    // Добавили EmailService сюда
    public InvitationController(GuestRepository guestRepository, EmailService emailService) {
        this.guestRepository = guestRepository;
        this.emailService = emailService;
    }

    @GetMapping("/")
    public String invitation(Model model) {
        model.addAttribute("groom", "Артём");
        model.addAttribute("bride", "Виктория");
        model.addAttribute("date", "12 ноября 2026");
        model.addAttribute("time", "13:00");
        model.addAttribute("address", "г. Санкт-Петербург, проспект Славы, 31");
        model.addAttribute("mapUrl", "https://yandex.ru/maps/-/CXUBz8Lt");
        model.addAttribute("guest", new Guest());
        return "invitation";
    }

    @PostMapping("/rsvp")
    public String submitRsvp(@ModelAttribute("guest") Guest guest,
                             RedirectAttributes redirectAttributes) {
        try {
            // 1. Сохраняем в базу
            guestRepository.save(guest);
            // 2. Отправляем тебе письмо!
            //emailService.sendGuestNotification(guest);

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
}