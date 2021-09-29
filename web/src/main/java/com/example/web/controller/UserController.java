package com.example.web.controller;

import com.example.common.dto.UserCreateDto;
import com.example.common.dto.UserDto;
import com.example.common.entity.User;
import com.example.common.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final ModelMapper modelMapper;

    @GetMapping("/users")
    public String getAllUsers(ModelMap modelMap) {
        List<User> users = userService.findAll();
        log.info("Requested to find all users , sending response with users list");
        List<UserDto> userDtos = users.stream().map(e ->
                modelMapper.map(e, UserDto.class)).collect(Collectors.toList());
        modelMap.addAttribute("users", userDtos);
        return "users";
    }

    @GetMapping("/getUserById/{id}")
    public String getUserById(@PathVariable(name = "id") int id, ModelMap modelMap) {
        User user = userService.findUserById(id);
        if (user == null) {
            log.info("Requested to find user by id {} which isn't exist", id);
            return "redirect:/users";
        }
        log.info("Sending response with pared user with id {} to userDto ", id);
        modelMap.addAttribute("user", modelMapper.map(user, UserDto.class));
        return "singleUser";
    }

    @GetMapping("/deleteUserById/{id}")
    public String deleteUserById(@PathVariable(name = "id") int id) {
        if (!userService.deleteUserById(id)) {
            log.info("Attempt to delete user with id {} which isn't exist", id);
            return "redirect:/users";
        }
        log.info("User with {} id was deleted from Database", id);
        return "redirect:/users";
    }

    @PostMapping()
    public String saveUser(@RequestBody @Valid UserCreateDto user) {
        if (userService.saveUser(modelMapper.map(user, User.class))) {
            log.info("User with {} email was saved", user.getEmail());
            return "redirect:/users";
        }
        log.info("Attempt to save user failed");
        return "redirect:/users";
    }

    @PutMapping
    public String updateUser(@RequestBody UserCreateDto user
            , @RequestParam(name = "id") int id) {
        User userFromDto = modelMapper.map(user, User.class);
        userFromDto.setId(id);
        if (userService.updateUser(userFromDto)) {
            log.info("User with was updated ");
            return "redirect:/getUserById/" + id;
        }
        log.info("Requested to update user with id {} failed", id);
        return "redirect:/users";
    }
}
