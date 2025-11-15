package com.cheeeese.user.domain.type;

import lombok.Getter;

@Getter
public enum ProfileImageType {

    P1("profile/signup_profile_1.jpg"),
    P2("profile/signup_profile_2.jpg"),
    P3("profile/signup_profile_3.jpg"),
    P4("profile/signup_profile_4.jpg"),
    P5("profile/signup_profile_5.jpg"),
    P6("profile/signup_profile_6.jpg"),
    P7("profile/signup_profile_7.jpg"),
    P8("profile/signup_profile_8.jpg"),
    P9("profile/signup_profile_9.jpg"),
    P10("profile/signup_profile_10.jpg");

    private final String path;

    ProfileImageType(String path) {
        this.path = path;
    }

    public static ProfileImageType fromName(String name) {
        return ProfileImageType.valueOf(name);
    }
}
