package ru.greyson.prices_analyzer.controllers;

import jakarta.servlet.http.HttpSession;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.greyson.prices_analyzer.dto.AuthenticationDTO;
import ru.greyson.prices_analyzer.dto.UserDTO;
import ru.greyson.prices_analyzer.models.User;
import ru.greyson.prices_analyzer.security.JWTUtil;
import ru.greyson.prices_analyzer.services.UserService;

@Controller
@RequestMapping("/auth")
public class UserController {
    private final UserService userService;
    private final ModelMapper modelMapper;
    private final JWTUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public UserController(UserService userService, ModelMapper modelMapper, JWTUtil jwtUtil, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.modelMapper = modelMapper;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
    }

    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("userDTO", new UserDTO());
        return "authorization/registration";
    }

    @PostMapping("/register")
    public String createUser(@ModelAttribute("userDTO") UserDTO userDTO) {
        User user = convertToUser(userDTO);
        user.setRole("USER");
        if (userService.create(user)) {
            return "redirect:/auth/login";
        }
        return "authorization/registration";
    }

    @GetMapping("/login")
    public String showLoginPage(Model model) {
        model.addAttribute("authenticationDTO", new AuthenticationDTO());
        return "authorization/login";
    }

    @PostMapping("/login")
    public String performLogin(@ModelAttribute("authenticationDTO") AuthenticationDTO authenticationDTO, HttpSession session, Model model) {
        UsernamePasswordAuthenticationToken authInputToken =
                new UsernamePasswordAuthenticationToken(authenticationDTO.getLogin(),
                        authenticationDTO.getPassword());

        try {
            Authentication auth = authenticationManager.authenticate(authInputToken);
            SecurityContextHolder.getContext().setAuthentication(auth);

            String token = jwtUtil.generateToken(authenticationDTO.getLogin());
            session.setAttribute("token", token);

            return "redirect:/index";
        } catch (BadCredentialsException e) {
            model.addAttribute("error", "Incorrect credentials!");
            return "authorization/login";
        }
    }

    @GetMapping("/logout")
    public String performLogout(HttpSession session) {
        session.removeAttribute("token");
        return "redirect:/login";
    }

    public User convertToUser(UserDTO userDTO){
        return modelMapper.map(userDTO, User.class);
    }
}