package ru.otus.controllers;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.otus.model.Client;
import ru.otus.services.DBService;

import java.util.List;

@Controller
@AllArgsConstructor
public class ClientsController {
    private final DBService DBService;

    @GetMapping("/clients")
    public String clientListView(Model model) {
        List<Client> clients = DBService.findAll();
        model.addAttribute("clients", clients);
        return "clients";
    }
}
