package com.example.ShopApp_BE.Utils;

public class MessageKeys {
    //register
    public static final String PHONENUMBER_EXISTED = "phonenumber.existed";
    public static final String PASSWORD_NOT_MATCH = "password.not.match";
    public static final String EMAIL_EXISTED = "email.existed";
    public static final String REGISTER_SUCCESS = "register.successfully";

    //role
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_USER = "USER";

    //login
    public static final String LOGIN_SUCCESS = "login.successfully";
    public static final String LOGIN_FAILED = "login.failed";
    public static final String ACCOUNT_LOCK = "account.locked";

    //user
    public static final String UPDATE_SUCCESS = "update.successfully";
    public static final String USER_ID_NOT_FOUND = "user.id.not.found";
    public static final String UNAUTHORIZED = "unauthorized";

    //password
    public static final String CONFIRM_PASSWORD_NOT_MATCH = "confirm.password.mismatch";
    public static final String RESET_PASSWORD = "RESET PASSWORD";
    public static final String SEND_EMAIL_RESET_PASSWORD_SUCCESS = "send.email.reset.password.successfully";
    public static final String RESET_PASSWORD_SUCCESS = "reset.password.successfully";

    //token
    public static final String DELETE_TOKEN_SUCCESS = "delete.token.successfully";
    public static final String REFRESH_TOKEN_INVALID = "refresh.token.invalid";
    public static final String ACCESS_TOKEN_INVALID = "access.token.invalid";
    public static final String RESET_TOKEN_INVALID = "reset.token.invalid";

    //header
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String REFRESH_TOKEN_HEADER = "refresh_token";

    //image
    public static final String IMAGE_NOT_VALID = "image.not.valid";
    public static final String IMAGE_NOT_FOUND = "image.not.found";

    //product
    public static final String CREATE_PRODUCT_SUCCESS = "product.create.successfully";
    public static final String PRODUCT_NOT_FOUND = "product.not.found";
    public static final String DELETE_PRODUCT_SUCCESS = "delete.product.successfully";

    //category
    public static final String CATEGORY_NOT_FOUND = "category.not.found";
    public static final String CREATE_CATEGORY_SUCCESS = "category.create.successfully";
    public static final String DELETE_CATEGORY_SUCCESS = "category.delete.successfully";

    //fake data
    public static final String FAKE_DATA_SUCCESS = "fake.data.successfully";

    //order
    public static final String CREATE_ORDER_SUCCESS = "order.create.successfully";
    public static final String DELETE_ORDER_SUCCESS = "order.delete.successfully";
    public static final String ORDER_NOT_FOUND = "order.not.found";
    public static final String STATUS_IN_VALID = "status.in.valid";
    public static final String CANNOT_CANCEL_ORDER = "cannot.cancel.order";

    //comment
    public static final String CREATE_COMMENT_SUCCESS = "comment.create.successfully";
    public static final String DELETE_COMMENT_SUCCESS = "comment.delete.successfully";
    public static final String COMMENT_NOT_FOUND = "comment.not.found";



}
