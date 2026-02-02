package com.cheeeese.cheese4cut.application;

import com.cheeeese.album.domain.Album;
import com.cheeeese.cheese4cut.domain.Cheese4cut;
import com.cheeeese.cheese4cut.domain.Cheese4cutAiSummary;
import com.cheeeese.cheese4cut.dto.response.AiResult;
import com.cheeeese.cheese4cut.infrastructure.ClovaClient;
import com.cheeeese.cheese4cut.infrastructure.persistence.Cheese4cutAiSummaryRepository;
import com.cheeeese.global.util.ImageUtil;
import com.cheeeese.global.util.resolver.CdnUrlResolver;
import com.cheeeese.photo.domain.Photo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class Cheese4cutAiService {

    private final ClovaClient clovaClient;
    private final ImageUtil imageUtil;
    private final Cheese4cutAiSummaryRepository aiSummaryRepository;
    private final CdnUrlResolver cdnUrlResolver;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void generateAiSummary(Cheese4cut cheese4cut, Album album, List<Photo> photos) {
        try {
            // 1. 이미지 분석 (HCX-005) - 사진 4장을 각각 분석하여 텍스트 추출
            String combinedAnalysis = photos.stream()
                    .map(photo -> {
                        // 사용자님의 ImageUtil 활용
                        // TODO: test만 원본 이미지로 실 서비스는 썸네일
                        String absoluteUrl = cdnUrlResolver.resolveOriginal(photo.getImageUrl());

                        // 2. 변환된 절대 URL을 ImageUtil에 전달
                        String base64 = imageUtil.resizeAndEncodeToBase64FromUrl(absoluteUrl);                        return String.format("[사진 분석]\n%s\n", clovaClient.callHcx005(base64));
                    })
                    .collect(Collectors.joining("\n"));

            // 2. 최종 요약 (HCX-DASH-002) - 분석 결과 + 제목/날짜로 앨범 기록 생성
            AiResult result = clovaClient.callHcxDash002(
                    combinedAnalysis,
                    album.getTitle(),
                    album.getEventDate()
            );

            // 3. 결과 저장
            Cheese4cutAiSummary summary = Cheese4cutAiSummary.builder()
                    .cheese4cut(cheese4cut)
                    .aiTitle(result.title())     // 10자 이내 검증된 결과
                    .aiContent(result.content()) // 180~220자 사이의 기록
                    .build();

            aiSummaryRepository.saveAndFlush(summary);

            log.info("AI Summary 성공적으로 생성됨. Album ID: {}", album.getId());

        } catch (Exception e) {
            log.error("AI Summary 생성 중 치명적 오류 발생: ", e);
        }
    }
}
