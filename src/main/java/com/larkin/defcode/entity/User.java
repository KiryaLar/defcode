package com.larkin.defcode.entity;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "password")
public class User {

    private Integer id;
    private String username;
    private String password;
    private Role role;
}
