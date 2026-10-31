package com.moviebooking.backend.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    UNCATEGORIZED(9999, "Lỗi hệ thống, vui lòng thử lại sau", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_REQUEST(1001, "Dữ liệu không hợp lệ", HttpStatus.BAD_REQUEST),
    EMAIL_EXISTED(1002, "Email đã được sử dụng", HttpStatus.CONFLICT),
    USER_NOT_FOUND(1003, "Không tìm thấy người dùng", HttpStatus.NOT_FOUND),
    INVALID_CREDENTIALS(1101, "Email hoặc mật khẩu không đúng", HttpStatus.UNAUTHORIZED),
    ACCOUNT_LOCKED(1102, "Tài khoản đã bị khóa", HttpStatus.FORBIDDEN),
    INVALID_REFRESH_TOKEN(1103, "Phiên đăng nhập không hợp lệ hoặc đã hết hạn", HttpStatus.UNAUTHORIZED),
    INVALID_OTP(1104, "OTP không hợp lệ hoặc đã hết hạn", HttpStatus.BAD_REQUEST),
    INVALID_CURRENT_PASSWORD(1105, "Mật khẩu hiện tại không đúng", HttpStatus.BAD_REQUEST),
    INVALID_NEW_PASSWORD(1106, "Mật khẩu mới phải có ít nhất 8 ký tự, tối đa 72 byte UTF-8 và khác mật khẩu hiện tại", HttpStatus.BAD_REQUEST);

    private final int code;
    private final String message;
    private final HttpStatus status;
}
