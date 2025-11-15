package com.cheeeese.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
@Schema(description = "사용자 이용 약관 API")
public record UserAgreementRequest(
        @NotNull
        @Schema(
                description = "서비스 이용 약관 동의",
                example = "true"
        )
        boolean isServiceAgreement,

        @NotNull
        @Schema(
                description = "사용자 정보 수집 동의",
                example = "true"
        )
        boolean isUserInfoAgreement,

        @Schema(
                description = "마케팅 수신 동의",
                example = "false"
        )
        boolean isMarketingAgreement,

        @NotNull
        @Schema(
                description = "제3자 제공 동의",
                example = "false"
        )
        boolean isThirdPartyAgreement
) {
}
