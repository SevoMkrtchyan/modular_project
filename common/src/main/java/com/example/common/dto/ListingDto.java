package com.example.common.dto;

import com.example.common.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ListingDto {

    private int id;
    private String title;
    private String description;
    private int price;
    private Category category;
    private UserDto userDto;

}
