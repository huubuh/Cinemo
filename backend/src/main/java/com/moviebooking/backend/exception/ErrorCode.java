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
    INVALID_REFRESH_TOKEN(1103, "Phiên đăng nhập đã hết hạn, vui lòng đăng nhập lại", HttpStatus.UNAUTHORIZED),
    INVALID_OTP(1104, "OTP không hợp lệ hoặc đã hết hạn", HttpStatus.BAD_REQUEST),
    INVALID_CURRENT_PASSWORD(1105, "Mật khẩu hiện tại không đúng", HttpStatus.BAD_REQUEST),
    INVALID_NEW_PASSWORD(1106, "Mật khẩu mới phải khác mật khẩu hiện tại", HttpStatus.BAD_REQUEST),
    EMAIL_NOT_VERIFIED(1107, "Email chưa được xác minh, vui lòng nhập mã OTP đã gửi về email", HttpStatus.FORBIDDEN),
    OTP_SEND_TOO_SOON(1108, "Vui lòng đợi 60 giây trước khi yêu cầu mã mới", HttpStatus.TOO_MANY_REQUESTS),
    PASSWORD_TOO_LONG(1109, "Mật khẩu quá dài, vui lòng chọn mật khẩu ngắn hơn", HttpStatus.BAD_REQUEST),
    INVALID_GOOGLE_TOKEN(1110, "Đăng nhập Google thất bại, vui lòng thử lại", HttpStatus.UNAUTHORIZED),
    GOOGLE_UNAVAILABLE(1111, "Đăng nhập Google tạm thời chưa khả dụng", HttpStatus.SERVICE_UNAVAILABLE),
    GOOGLE_LINK_CONFLICT(1113, "Email này đã liên kết với một tài khoản Google khác", HttpStatus.CONFLICT);

    private final int code;
    private final String message;
    private final HttpStatus status;
}
