//package projekt.zespolowy.zero_waste.controller;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//import projekt.zespolowy.zero_waste.entity.User;
//import projekt.zespolowy.zero_waste.services.UserService;
//
//@RestController
//@RequestMapping("/api/auth")
//public class LoginController {
//
//    private final UserService userService;
//
//    @Autowired
//    public LoginController(UserService userService) {
//        this.userService = userService;
//    }
//
//    @PostMapping("/register")
//    public User registerUser(@RequestBody User user) {
//        return userService.registerUser(user);
//    }
//
//    @PostMapping("/login")
//    public User loginUser(@RequestBody User user) {
//        return userService.loginUser(user.getUsername(), user.getPassword());
//    }
//
//    @GetMapping("login")
//    public String login() {
//        return "login";
//    }
//}
