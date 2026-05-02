package org.bloggers.ts_users.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class RequestUtils {

    public static final String VALID_PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z\\d]).{8,}$";
    public static final String VALID_USERNAME_PATTERN    = "^[a-zA-Z0-9._]+$";

}
