package com.bidv.asset.vehicle.ServiceImplement;

import com.bidv.asset.vehicle.DTO.LoginRequest;
import com.bidv.asset.vehicle.DTO.LoginResponse;
import com.bidv.asset.vehicle.Repository.UserAccountRepository;
import com.bidv.asset.vehicle.Service.AuthService;
import com.bidv.asset.vehicle.Utill.JwtUtils;
import com.bidv.asset.vehicle.entity.UserAccountEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthServiceImplement implements AuthService {

    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final int LOCK_DURATION_MINUTES = 5;

    @Override
    public LoginResponse login(LoginRequest request) {
        String identifier = request.getUsername();
        String password = request.getPassword();

        // 1. Kiểm tra đầu vào
        if (identifier == null || identifier.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            return LoginResponse.builder()
                    .success(false)
                    .message("Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu")
                    .build();
        }

        // 2. Tìm tài khoản (ưu tiên username, sau đó đến email)
        UserAccountEntity account = userAccountRepository.findByUsername(identifier).orElse(null);
        if (account == null) {
            try {
                account = userAccountRepository.findByEmail(identifier).orElse(null);
            } catch (Exception e) {
                return LoginResponse.builder()
                        .success(false)
                        .message("Tài khoản không hợp lệ (trùng email hệ thống)")
                        .build();
            }
        }

        // 3. Nếu không tìm thấy tài khoản
        if (account == null) {
            return LoginResponse.builder()
                    .success(false)
                    .message("Tài khoản không tồn tại")
                    .build();
        }

        // 4. Kiểm tra trạng thái tài khoản
        if (!"ACTIVE".equalsIgnoreCase(account.getStatus())) {
            return LoginResponse.builder()
                    .success(false)
                    .message("Tài khoản đã bị khóa hoặc chưa được kích hoạt")
                    .build();
        }

        // 5. Kiểm tra thời gian khóa (Brute force protection)
        if (account.getLockUntil() != null && account.getLockUntil().isAfter(LocalDateTime.now())) {
            return LoginResponse.builder()
                    .success(false)
                    .message("Tài khoản đang bị khóa tạm thời. Vui lòng thử lại sau.")
                    .build();
        }

        // 6. Kiểm tra mật khẩu
        if (passwordEncoder.matches(password, account.getPasswordHash())) {
            // ĐĂNG NHẬP THÀNH CÔNG

            // Reset số lần nhập sai
            account.setFailedAttempts(0);
            account.setLockUntil(null);

            // Tạo tokens
            String roleCode = account.getRole() != null ? account.getRole().getCode() : "USER";
            String accessToken = jwtUtils.generateAccessToken(account.getUsername(), roleCode);
            String refreshToken = jwtUtils.generateRefreshToken(account.getUsername());

            // Lưu refresh token vào DB
            account.setRefreshToken(refreshToken);
            userAccountRepository.save(account);

            // Lấy thông tin hiển thị (ưu tiên tên từ bảng Nhân viên/Khách hàng)
            String fullName = account.getUsername();
            if (account.getEmployee() != null) {
                fullName = account.getEmployee().getFullName();
            } else if (account.getCustomer() != null) {
                fullName = account.getCustomer().getCustomerName();
            }

            return LoginResponse.builder()
                    .success(true)
                    .id(account.getId())
                    .username(account.getUsername())
                    .fullName(fullName)
                    .email(account.getEmail())
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .role(account.getRole() != null ? account.getRole().getCode() : null)
                    .message("Đăng nhập thành công")
                    .build();
        } else {
            // SAI MẬT KHẨU

            int attempts = (account.getFailedAttempts() != null ? account.getFailedAttempts() : 0) + 1;
            account.setFailedAttempts(attempts);

            if (attempts >= MAX_FAILED_ATTEMPTS) {
                account.setLockUntil(LocalDateTime.now().plusMinutes(LOCK_DURATION_MINUTES));
                userAccountRepository.save(account);
                return LoginResponse.builder()
                        .success(false)
                        .message("Sai mật khẩu quá " + MAX_FAILED_ATTEMPTS + " lần. Tài khoản bị khóa "
                                + LOCK_DURATION_MINUTES + " phút.")
                        .build();
            } else {
                userAccountRepository.save(account);
                return LoginResponse.builder()
                        .success(false)
                        .message("Mật khẩu không chính xác. Bạn còn " + (MAX_FAILED_ATTEMPTS - attempts) + " lần thử.")
                        .build();
            }
        }
    }

    @Override
    public LoginResponse refreshToken(String refreshToken) {
        if (refreshToken == null || jwtUtils.isTokenExpired(refreshToken)) {
            return LoginResponse.builder().success(false).message("Refresh token hết hạn hoặc không hợp lệ").build();
        }

        UserAccountEntity account = userAccountRepository.findByRefreshToken(refreshToken)
                .orElse(null);

        if (account == null) {
            return LoginResponse.builder().success(false).message("Refresh token không tồn tại trong hệ thống").build();
        }

        // Tạo tokens mới (Rotation)
        String roleCode = account.getRole() != null ? account.getRole().getCode() : "USER";
        String newAccessToken = jwtUtils.generateAccessToken(account.getUsername(), roleCode);
        String newRefreshToken = jwtUtils.generateRefreshToken(account.getUsername());

        // Cập nhật refresh token mới vào DB
        account.setRefreshToken(newRefreshToken);
        userAccountRepository.save(account);

        // Lấy thông tin hiển thị
        String fullName = account.getUsername();
        if (account.getEmployee() != null) {
            fullName = account.getEmployee().getFullName();
        } else if (account.getCustomer() != null) {
            fullName = account.getCustomer().getCustomerName();
        }

        return LoginResponse.builder()
                .success(true)
                .id(account.getId())
                .username(account.getUsername())
                .fullName(fullName)
                .email(account.getEmail())
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .role(roleCode)
                .message("Làm mới token thành công")
                .build();
    }

    @Override
    public void logout(String refreshToken) {
        if (refreshToken != null) {
            userAccountRepository.findByRefreshToken(refreshToken).ifPresent(account -> {
                account.setRefreshToken(null);
                userAccountRepository.save(account);
            });
        }
    }
}
