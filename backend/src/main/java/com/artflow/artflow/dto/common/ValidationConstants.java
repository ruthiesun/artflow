package com.artflow.artflow.dto.common;

public class ValidationConstants {
    public static final String USERNAME_REGEX = "^(?=.*[a-zA-Z])[a-zA-Z0-9_-]{3,20}$";
    public static final String USERNAME_MESSAGE = "Username must be between 3 and 20 characters, contain at least one letter, and can only contain alphanumeric characters, hyphens, and underscores.";
    public static final int USERNAME_LENGTH_MIN = 3;
    public static final int USERNAME_LENGTH_MAX = 20;
    
    public static final String PASSWORD_REGEX = "(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[@$!%*?&])[A-Za-z0-9@$!%*?&]{8,100}$";
    public static final String PASSWORD_MESSAGE = "Password must be between 8 and 100 characters and contain an uppercase letter, lowercase letter, number, and a special character (@$!%*?&).";
    public static final int PASSWORD_LENGTH_MIN = 8;
    public static final int PASSWORD_LENGTH_MAX = 100;
    
    public static final String PROJECT_NAME_REGEX = "^(?=.{1,100}$)[A-Za-z0-9]+(?: [A-Za-z0-9]+)*$";
    public static final String PROJECT_NAME_MESSAGE = "Project name must be between 1 and 100 characters and can only contain alphanumeric characters and single spaces.";
    public static final int PROJECT_NAME_LENGTH_MIN = 1;
    public static final int PROJECT_NAME_LENGTH_MAX = 100;
    public static final int PROJECT_DESC_LENGTH_MAX = 250;
    public static final int PROJECT_IMAGE_CAPTION_LENGTH_MAX = 250;
    
    public static final String TAG_REGEX = "^(?=.{1,20}$)[a-zA-Z0-9]+(?: [a-zA-Z0-9]+)*$";
    public static final String TAG_MESSAGE = "Tags must be between 1 and 20 characters and can only contain alphanumeric characters and single spaces.";
    public static final int TAG_LENGTH_MIN = 1;
    public static final int TAG_LENGTH_MAX = 20;
}
