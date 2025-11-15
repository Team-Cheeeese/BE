package com.cheeeese.user.domain.type;

import lombok.Getter;

@Getter
public enum ProfileImageType {

    P1("signup_profile_1.jpg"),
    P2("signup_profile_2.jpg"),
    P3("signup_profile_3.jpg"),
    P4("signup_profile_4.jpg"),
    P5("signup_profile_5.jpg"),
    P6("signup_profile_6.jpg"),
    P7("signup_profile_7.jpg"),
    P8("signup_profile_8.jpg"),
    P9("signup_profile_9.jpg"),
    P10("signup_profile_10.jpg");

    private final String path;

    ProfileImageType(String path) {
        this.path = path;
    }

    public static ProfileImageType fromName(String name) {
        return ProfileImageType.valueOf(name);
    }
}
