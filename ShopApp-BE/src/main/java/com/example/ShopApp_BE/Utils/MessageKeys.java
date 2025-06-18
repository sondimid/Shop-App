package com.example.ShopApp_BE.Utils;

public class MessageKeys {
    // Register
    public static final String PHONENUMBER_EXISTED = "Số điện thoại đã tồn tại.";
    public static final String PASSWORD_NOT_MATCH = "Mật khẩu không khớp.";
    public static final String EMAIL_EXISTED = "Email đã tồn tại.";
    public static final String REGISTER_SUCCESS = "Đăng ký tài khoản thành công.";

    // Role
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_USER = "USER";

    // Login
    public static final String LOGIN_SUCCESS = "Đăng nhập thành công.";
    public static final String LOGIN_FAILED = "Đăng nhập thất bại.";
    public static final String ACCOUNT_LOCK = "Tài khoản đã bị khóa.";
    public static final String ACCOUNT_LOGOUT = "Tài khoản đã đăng xuất";

    // User
    public static final String UPDATE_SUCCESS = "Cập nhật thông tin thành công.";
    public static final String USER_ID_NOT_FOUND = "ID người dùng không chính xác.";
    public static final String UNAUTHORIZED = "Bạn không có quyền thực hiện hành động này.";

    // Password
    public static final String CONFIRM_PASSWORD_NOT_MATCH = "Xác nhận mật khẩu không trùng khớp.";
    public static final String RESET_PASSWORD = "Đặt lại mật khẩu.";
    public static final String SEND_EMAIL_RESET_PASSWORD_SUCCESS = "Gửi email đặt lại mật khẩu thành công.";
    public static final String RESET_PASSWORD_SUCCESS = "Đặt lại mật khẩu thành công.";

    // Token
    public static final String DELETE_TOKEN_SUCCESS = "Xóa token thành công.";
    public static final String REFRESH_TOKEN_INVALID = "Refresh token không hợp lệ.";
    public static final String ACCESS_TOKEN_INVALID = "Access token không hợp lệ.";
    public static final String RESET_TOKEN_INVALID = "Reset token không hợp lệ.";

    // Header
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String REFRESH_TOKEN_HEADER = "refresh_token";

    // Image
    public static final String IMAGE_NOT_VALID = "Hình ảnh không hợp lệ.";
    public static final String IMAGE_NOT_FOUND = "Không tìm thấy hình ảnh.";

    // Product
    public static final String CREATE_PRODUCT_SUCCESS = "Tạo sản phẩm thành công.";
    public static final String PRODUCT_NOT_FOUND = "Không tìm thấy sản phẩm.";
    public static final String DELETE_PRODUCT_SUCCESS = "Xóa sản phẩm thành công.";

    // Category
    public static final String CATEGORY_NOT_FOUND = "Không tìm thấy danh mục.";
    public static final String CREATE_CATEGORY_SUCCESS = "Tạo danh mục thành công.";
    public static final String DELETE_CATEGORY_SUCCESS = "Xóa danh mục thành công.";

    // Fake Data
    public static final String FAKE_DATA_SUCCESS = "Dữ liệu giả đã được tạo thành công.";

    // Order
    public static final String CREATE_ORDER_SUCCESS = "Tạo đơn hàng thành công.";
    public static final String DELETE_ORDER_SUCCESS = "Xóa đơn hàng thành công.";
    public static final String ORDER_NOT_FOUND = "Không tìm thấy đơn hàng.";
    public static final String STATUS_IN_VALID = "Trạng thái không hợp lệ.";
    public static final String CANNOT_CANCEL_ORDER = "Không thể hủy đơn hàng.";
    public static final String PAYMENT_IN_VALID = "Phương thức thanh toán không hợp lệ";
    public static final String ORDER_CODE = "order_code";

    // Comment
    public static final String CREATE_COMMENT_SUCCESS = "Thêm bình luận thành công.";
    public static final String DELETE_COMMENT_SUCCESS = "Xóa bình luận thành công.";
    public static final String COMMENT_NOT_FOUND = "Không tìm thấy bình luận.";

    // Email
    public static final String EMAIL_SEND_SUCCESS = "Gửi email thành công.";
    public static final String OTP_NOT_MATCH = "Mã OTP không khớp.";
    public static final String OTP_EXPIRED = "Mã OTP đã hết hạn.";
    public static final String EMAIL_NOT_MATCH = "Email không khớp.";
    public static final String TO0_MANY_REQUEST = "Quá nhiều requests vui lòng đợi";

    //key redis hash
    public static final String BLACKLIST_HASH = "blacklist";
    public static final String PRODUCT_HASH = "product";
    public static final String NEW_HASH = "new";
    public static final String DISCOUNT_HASH = "discount";
    public static final String CART_HASH = "cart";
    public static final String OTP_HASH = "otp";
}
