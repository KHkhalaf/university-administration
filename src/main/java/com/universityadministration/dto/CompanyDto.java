package com.universityadministration.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CompanyDto {
    public int id;
    public String name;

    public CompanyDto(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
