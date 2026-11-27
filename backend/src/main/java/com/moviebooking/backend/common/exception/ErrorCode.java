package com.moviebooking.backend.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    UNCATEGORIZED(9999, "Lỗi hệ thống, vui lòng thử lại sau", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_REQUEST(1001, "Dữ liệu không hợp lệ", HttpStatus.BAD_REQUEST),
    CONCURRENT_UPDATE(1004, "Dữ liệu đã được người khác cập nhật, vui lòng tải lại", HttpStatus.CONFLICT),
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
    GOOGLE_LINK_CONFLICT(1113, "Email này đã liên kết với một tài khoản Google khác", HttpStatus.CONFLICT),
    MOVIE_NOT_FOUND(1201, "Không tìm thấy phim", HttpStatus.NOT_FOUND),
    CATEGORY_NOT_FOUND(1202, "Không tìm thấy thể loại", HttpStatus.NOT_FOUND),
    CATEGORY_EXISTED(1203, "Tên thể loại đã tồn tại", HttpStatus.CONFLICT),
    CATEGORY_IN_USE(1204, "Thể loại đang được phim sử dụng", HttpStatus.CONFLICT),
    INVALID_MOVIE_DATES(1205, "Ngày kết thúc phải từ ngày khởi chiếu trở đi", HttpStatus.BAD_REQUEST),

    CINEMA_NOT_FOUND(1301, "Không tìm thấy rạp", HttpStatus.NOT_FOUND),
    ROOM_NOT_FOUND(1302, "Không tìm thấy phòng chiếu", HttpStatus.NOT_FOUND),
    ROOM_EXISTED(1303, "Tên phòng đã tồn tại trong rạp", HttpStatus.CONFLICT),
    INVALID_SEAT_LAYOUT(1304, "Sơ đồ ghế không hợp lệ: ghế nằm ngoài phòng hoặc chồng lên nhau", HttpStatus.BAD_REQUEST),
    SEAT_LAYOUT_IN_USE(1305, "Không thể bỏ ghế đã được dùng cho suất chiếu, hãy chuyển ghế sang trạng thái bảo trì/khóa", HttpStatus.CONFLICT),

    SHOWTIME_NOT_FOUND(1401, "Không tìm thấy suất chiếu", HttpStatus.NOT_FOUND),
    SHOWTIME_OVERLAP(1402, "Phòng đã có suất chiếu trùng giờ (tính cả thời gian dọn phòng)", HttpStatus.CONFLICT),
    MOVIE_NOT_SHOWABLE(1403, "Phim đang ẩn hoặc ngày chiếu nằm ngoài thời gian phát hành", HttpStatus.BAD_REQUEST),
    ROOM_NOT_AVAILABLE(1404, "Phòng chưa có sơ đồ ghế, đang bảo trì/đóng cửa hoặc rạp ngừng hoạt động", HttpStatus.BAD_REQUEST),
    SHOWTIME_STARTED(1405, "Suất chiếu đã bắt đầu", HttpStatus.CONFLICT),
    TICKET_PRICE_NOT_FOUND(1406, "Không tìm thấy bảng giá vé", HttpStatus.NOT_FOUND),
    TICKET_PRICE_OVERLAP(1407, "Đã có bảng giá cùng loại ghế, loại màn hình và thứ trong khoảng ngày này", HttpStatus.CONFLICT),
    INVALID_TICKET_PRICE_DATES(1408, "Ngày kết thúc phải từ ngày bắt đầu áp dụng trở đi", HttpStatus.BAD_REQUEST);

    private final int code;
    private final String message;
    private final HttpStatus status;
}
