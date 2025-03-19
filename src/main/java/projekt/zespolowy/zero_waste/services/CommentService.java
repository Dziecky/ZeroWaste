package projekt.zespolowy.zero_waste.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import projekt.zespolowy.zero_waste.repository.CommentRepository;

@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserService userService;
    @Autowired
    public CommentService(CommentRepository commentRepository, UserService userService) {
        this.commentRepository = commentRepository;
        this.userService = userService;
    }

}
