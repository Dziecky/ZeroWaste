package projekt.zespolowy.zero_waste.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import projekt.zespolowy.zero_waste.entity.User;
import projekt.zespolowy.zero_waste.entity.forum.Comment;
import projekt.zespolowy.zero_waste.entity.forum.ForumThread;
import projekt.zespolowy.zero_waste.services.CommentService;
import projekt.zespolowy.zero_waste.services.ForumThreadService;
import projekt.zespolowy.zero_waste.services.UserService;

import java.util.List;

@Controller
@RequestMapping("/forum")
public class ForumThreadController {
    private ForumThreadService threadService;
    private UserService userService;
    private final CommentService commentService;


    @Autowired
    public ForumThreadController(ForumThreadService forumThreadService, UserService userService, CommentService commentService) {
        this.threadService = forumThreadService;
        this.userService = userService;
        this.commentService = commentService;
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
        model.addAttribute("newComment", new Comment());

        return "forum/thread";
    }
    @PostMapping("/thread/{id}/comment")
    public String addComment(@PathVariable Long id,
                             @ModelAttribute("newComment") Comment newComment,
                             Model model) {

        User currentUser = userService.getUser();
        ForumThread thread = threadService.findById(id);
        commentService.saveComment(newComment, thread, currentUser);


        // Redirect back to the thread page
        return "redirect:/forum/thread/" + id;
    }
    @PostMapping("/thread/{threadId}/editComment/{commentId}")
    public String editComment(@PathVariable Long threadId,
                              @PathVariable Long commentId,
                              @RequestParam("content") String content) {
        Comment comment = commentService.findById(commentId);
        commentService.editComment(comment, content);
        return "redirect:/forum/thread/" + threadId;
    }
}
