package com.bidv.asset.vehicle.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginResponse {
    private Long id;
    private String username;
    private String fullName;
    private String email;
    private String accessToken;
    private String refreshToken;
    private String role;
    private String message;
    private boolean success;
}
