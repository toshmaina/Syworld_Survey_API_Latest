package com.skyworld.survey.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RegisterRequest {
    private String username;
    private String email;
    private String password;
    private String role;   // "ADMIN" or "USER" — defaults to USER if blank
}
