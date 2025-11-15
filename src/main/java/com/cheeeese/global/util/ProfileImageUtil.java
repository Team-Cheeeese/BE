package com.cheeeese.global.util;

import com.cheeeese.global.util.resolver.CdnUrlResolver;
import com.cheeeese.user.domain.User;
import com.cheeeese.user.domain.type.ProfileImageType;

public class ProfileImageUtil {

    public static String resolveProfileImage(User user, CdnUrlResolver resolver) {
        ProfileImageType type = ProfileImageType.fromName(user.getProfileImage());
        return resolver.resolveProfile(type.getPath());
    }
}
