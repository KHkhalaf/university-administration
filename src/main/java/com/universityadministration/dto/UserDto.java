package com.universityadministration.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserDto {
    private int id;
    private String username;
    private String password;
    private String email;
    private String type;
    private int companyId;

}
