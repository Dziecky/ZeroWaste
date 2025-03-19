package projekt.zespolowy.zero_waste.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import projekt.zespolowy.zero_waste.entity.User;
import projekt.zespolowy.zero_waste.entity.forum.ForumThread;
import projekt.zespolowy.zero_waste.services.ForumThreadService;
import projekt.zespolowy.zero_waste.services.UserService;

import java.util.List;

@Controller
@RequestMapping("/forum")
public class ForumThreadController {
    private ForumThreadService threadService;
    private UserService userService;

    @Autowired
    public ForumThreadController(ForumThreadService forumThreadService, UserService userService) {
        this.threadService = forumThreadService;
        this.userService = userService;
    }
    @GetMapping
    public String listThreads(Model model) {
        //User currentUser = userService.getUser();

        List<ForumThread> threads = threadService.findAll();
        model.addAttribute("threads", threads);
        return "forum/forum";
    }
    @GetMapping("/create")
    public String createThread(Model model) {
        model.addAttribute("thread", new ForumThread());
        return "forum/createThread";
    }
    @PostMapping("/create")
    public String saveThread(@ModelAttribute("thread") ForumThread thread) {
        User currentUser = userService.getUser();
        threadService.saveThread(thread, currentUser);
        return "redirect:/forum";
    }
    @GetMapping("/thread/{id}")
    public String viewThread(@PathVariable Long id, Model model) {
        ForumThread thread = threadService.findById(id);
        model.addAttribute("thread", thread);
        return "forum/thread";
    }
}
