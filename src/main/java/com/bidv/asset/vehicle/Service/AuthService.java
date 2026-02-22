package com.bidv.asset.vehicle.Service;

import com.bidv.asset.vehicle.DTO.LoginRequest;
import com.bidv.asset.vehicle.DTO.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);

    LoginResponse refreshToken(String refreshToken);

    void logout(String refreshToken);
}
