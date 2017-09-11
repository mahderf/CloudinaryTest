package com.cloudinary.demo.controller;

import com.cloudinary.demo.DemoApplication;
import com.cloudinary.demo.configuration.CloudinaryConfig;
import com.cloudinary.demo.models.Actor;
import com.cloudinary.demo.repository.ActorRepository;
import com.cloudinary.utils.ObjectUtils;
import com.google.common.collect.Lists;
import it.ozimov.springboot.mail.model.Email;
import it.ozimov.springboot.mail.model.defaultimpl.DefaultEmail;
import it.ozimov.springboot.mail.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.internet.InternetAddress;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

@Controller
public class HomeController {
    @Autowired
    ActorRepository actorRepository;

    @Autowired
    CloudinaryConfig cloudc;



    @Autowired
    public EmailService emailService;
    public void sendEmailWithoutTemplating() throws UnsupportedEncodingException {
        final Email email= DefaultEmail.builder()
                .from(new InternetAddress("mahifentaye@gmail.com", "Marco Tullio Cicero ne"))
                .to(Lists.newArrayList(new InternetAddress("mymahder@gmail.com","pomponiu s Atticus")))
                .subject("Laelius de amicitia")
                .body("Firmamentum autem stabilitatis constantiaeque eius, quam in amicitia q uaermius, fides est.")
                .encoding("UTF-8").build();
//		modelObject.put("recipent", recipent);
        System.out.println("test it");
        emailService.send(email);
    }
    @RequestMapping("/")
    public String listActors(Model model) throws UnsupportedEncodingException {
//        DemoApplication demo=new DemoApplication();
        model.addAttribute("actors", actorRepository.findAll());
        sendEmailWithoutTemplating();
//        demo.sendEmailWithoutTemplating("email");
        return "list";
    }
    @GetMapping("/add")
    public String newActor(Model model){
        model.addAttribute("actor", new Actor());
        return "form";
    }

    @PostMapping("/add")
    public String processActor(@ModelAttribute Actor actor,
                               @RequestParam("file")MultipartFile file) {
        if (file.isEmpty()) {
            return "redirect:/add";
        }


        try {
            Map uploadResult = cloudc.upload(file.getBytes(),
            ObjectUtils.asMap("resourcetype","auto"));
            actor.setHeadshot(uploadResult.get("url").toString());
            actorRepository.save(actor);
        } catch (IOException e) {
            e.printStackTrace();
            return "redirect:/add";
        }
        return "redirect:/";
    }
}
