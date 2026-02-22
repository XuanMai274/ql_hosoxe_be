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
        if (identifier == null || identifier.trim().isEmpty()) {
            return LoginResponse.builder()
                    .success(false)
                    .message("Tài khoản hoặc mật khẩu không chính xác")
                    .build();
        }

        // Ưu tiên tìm theo username trước vì username thường là duy nhất và bắt buộc
        UserAccountEntity account = userAccountRepository.findByUsername(identifier).orElse(null);

        // Nếu không tìm thấy theo username, mới tìm theo email
        if (account == null) {
            try {
                account = userAccountRepository.findByEmail(identifier).orElse(null);
            } catch (org.springframework.dao.IncorrectResultSizeDataAccessException e) {
                // Trường hợp có nhiều hơn 1 record cùng email, thông báo bảo mật hoặc lấy cái
                // đầu tiên
                // Ở đây ta báo lỗi chung để bảo mật
                return LoginResponse.builder()
                        .success(false)
                        .message("Tài khoản không hợp lệ (trùng email)")
                        .build();
            }
        }

        if (account == null) {
            return LoginResponse.builder()
                    .success(false)
                    .message("Tài khoản hoặc mật khẩu không chính xác")
                    .build();
        }

        // Check if account is locked
        if (account.getLockUntil() != null && account.getLockUntil().isAfter(LocalDateTime.now())) {
            return LoginResponse.builder()
                    .success(false)
                    .message(
                            "Tài khoản của bạn đã bị khóa tạm thời do nhập sai quá 5 lần. Vui lòng thử lại sau 5 phút.")
                    .build();
        }

        // Check password
        if (passwordEncoder.matches(request.getPassword(), account.getPasswordHash())) {
            // Reset failed attempts on success
            account.setFailedAttempts(0);
            account.setLockUntil(null);
            userAccountRepository.save(account);

            // Generate tokens
            String accessToken = jwtUtils.generateAccessToken(account.getUsername());
            String refreshToken = jwtUtils.generateRefreshToken(account.getUsername());

            // Save refresh token to DB
            account.setRefreshToken(refreshToken);
            userAccountRepository.save(account);

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
            // Handle failed attempt
            int attempts = account.getFailedAttempts() != null ? account.getFailedAttempts() + 1 : 1;
            account.setFailedAttempts(attempts);

            if (attempts >= MAX_FAILED_ATTEMPTS) {
                account.setLockUntil(LocalDateTime.now().plusMinutes(LOCK_DURATION_MINUTES));
                userAccountRepository.save(account);
                return LoginResponse.builder()
                        .success(false)
                        .message("Sai mật khẩu quá 5 lần. Tài khoản của bạn đã bị khóa 5 phút.")
                        .build();
            } else {
                userAccountRepository.save(account);
                return LoginResponse.builder()
                        .success(false)
                        .message("Tài khoản hoặc mật khẩu không chính xác. Bạn còn " + (MAX_FAILED_ATTEMPTS - attempts)
                                + " lần thử.")
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

        String newAccessToken = jwtUtils.generateAccessToken(account.getUsername());

        return LoginResponse.builder()
                .success(true)
                .username(account.getUsername())
                .accessToken(newAccessToken)
                .role(account.getRole() != null ? account.getRole().getCode() : null)
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
