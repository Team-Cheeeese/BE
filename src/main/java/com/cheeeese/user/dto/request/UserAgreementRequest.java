package com.cheeeese.user.dto.request;

import lombok.Builder;

@Builder
public record UserAgreementRequest(
        boolean isServiceAgreement,
        boolean isUserInfoAgreement,
        boolean isMarketingAgreement,
        boolean isThirdPartyAgreement
) {
}
