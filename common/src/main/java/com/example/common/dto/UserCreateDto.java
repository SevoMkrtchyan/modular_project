package com.example.common.dto;

import com.example.common.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserCreateDto {

    private String name;
    private String surname;
    private String email;
    private String password;
    private Role role;

}
