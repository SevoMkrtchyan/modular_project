package com.example.rest.endpoint;

import com.example.common.dto.UserCreateDto;
import com.example.common.dto.UserDto;
import com.example.common.entity.User;
import com.example.common.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserEndpoint {

    private final UserService userService;
    private final ModelMapper modelMapper;

    @GetMapping()
    public List<UserDto> getAllUsers() {
        List<User> users = userService.findAll();
        log.info("Requested to find all users , sending response with users list");
        return users.stream().map(e ->
                modelMapper.map(e, UserDto.class)).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable(name = "id") int id) {
        User user = userService.findUserById(id);
        if (user == null) {
            log.info("Requested to find user by id {} which isn't exist", id);
            return ResponseEntity.noContent().build();
        }
        log.info("Sending response with pared user with id {} to userDto ", id);
        return ResponseEntity.ok(modelMapper.map(user, UserDto.class));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteUserById(@PathVariable(name = "id") int id) {
        if (!userService.deleteUserById(id)) {
            log.info("Attempt to delete user with id {} which isn't exist", id);
            return ResponseEntity.notFound().build();
        }
        log.info("User with {} id was deleted from Database", id);
        return ResponseEntity.ok().build();
    }

    @PostMapping()
    public ResponseEntity saveUser(@RequestBody @Valid UserCreateDto user) {
        if (userService.saveUser(modelMapper.map(user, User.class))) {
            log.info("User with {} email was saved", user.getEmail());
            return ResponseEntity.ok().build();
        }
        log.info("Attempt to save user failed");
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    @PutMapping
    public ResponseEntity<UserDto> updateUser(@RequestBody UserCreateDto user
            , @RequestParam(name = "id") int id) {
        User userFromDto = modelMapper.map(user, User.class);
        userFromDto.setId(id);
        if (userService.updateUser(userFromDto)) {
            log.info("User with was updated ");
            return ResponseEntity.ok().build();
        }
        log.info("Requested to update user with id {} failed", id);
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

}