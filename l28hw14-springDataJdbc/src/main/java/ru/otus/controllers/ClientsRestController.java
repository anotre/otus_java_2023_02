package ru.otus.controllers;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.model.Address;
import ru.otus.model.Client;
import ru.otus.model.Phone;
import ru.otus.services.DBService;

import java.util.HashSet;
import java.util.Set;

@RestController
@AllArgsConstructor
public class ClientsRestController {
    private final DBService dbService;

    @PostMapping("/api/clients")
    @ResponseBody
    public Client createClient(
            @RequestParam("name") String nameParam,
            @RequestParam("address") String addressParam,
            @RequestParam("phone") String phoneParam) {
        var address = new Address(addressParam);
        var phone = new Phone(phoneParam);
        Set<Phone> phones = new HashSet<>();
        phones.add(phone);
        var client = new Client(nameParam, address, phones);

        return dbService.save(client);
    }
}
