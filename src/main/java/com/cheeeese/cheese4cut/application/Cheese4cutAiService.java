package com.cheeeese.cheese4cut.application;

import com.cheeeese.album.domain.Album;
import com.cheeeese.album.exception.AlbumException;
import com.cheeeese.album.exception.code.AlbumErrorCode;
import com.cheeeese.album.infrastructure.persistence.AlbumRepository;
import com.cheeeese.cheese4cut.domain.AiSummaryStatus;
import com.cheeeese.cheese4cut.domain.Cheese4cut;
import com.cheeeese.cheese4cut.domain.Cheese4cutAiSummary;
import com.cheeeese.cheese4cut.dto.response.AiResult;
import com.cheeeese.cheese4cut.dto.response.Cheese4cutAiResponse;
import com.cheeeese.cheese4cut.exception.Cheese4cutException;
import com.cheeeese.cheese4cut.exception.code.Cheese4cutErrorCode;
import com.cheeeese.cheese4cut.infrastructure.ClovaClient;
import com.cheeeese.cheese4cut.infrastructure.mapper.Cheese4cutMapper;
import com.cheeeese.cheese4cut.infrastructure.persistence.Cheese4cutAiSummaryRepository;
import com.cheeeese.cheese4cut.infrastructure.persistence.Cheese4cutRepository;
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
    private final Cheese4cutRepository cheese4cutRepository;
    private final AlbumRepository albumRepository;
    private final CdnUrlResolver cdnUrlResolver;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void generateAiSummary(Cheese4cut cheese4cut, Album album, List<Photo> photos) {
        Cheese4cutAiSummary summary = aiSummaryRepository.findByCheese4cutId(cheese4cut.getId())
                .orElseGet(() -> aiSummaryRepository.saveAndFlush(
                        Cheese4cutMapper.toAiSummaryProcessing(cheese4cut)
                ));

        try {
            // 1. 이미지 분석 (HCX-005) - 사진 4장을 각각 분석하여 텍스트 추출
            String combinedAnalysis = photos.stream()
                    .map(photo -> {
                        String absoluteUrl = cdnUrlResolver.resolveOriginal(photo.getImageUrl());

                        // 2. 변환된 절대 URL을 ImageUtil에 전달
                        String base64 = imageUtil.resizeAndEncodeToBase64FromUrl(absoluteUrl);
                        return String.format("[사진 분석]\n%s\n", clovaClient.callHcx005(base64));
                    })
                    .collect(Collectors.joining("\n"));

            // 2. 최종 요약 (HCX-DASH-002) - 분석 결과 + 제목/날짜로 앨범 기록 생성
            AiResult result = clovaClient.callHcxDash002(
                    combinedAnalysis,
                    album.getTitle(),
                    album.getEventDate()
            );

            // 3. 결과 저장
            summary.updateStatus(AiSummaryStatus.COMPLETED, result.title(), result.content());

            log.info("AI Summary 성공적으로 생성됨. Album ID: {}", album.getId());

        } catch (Exception e) {
            log.error("AI Summary 생성 중 치명적 오류 발생: ", e);
            // 실패 상태 저장하여 프론트엔드가 인지할 수 있도록 함
            summary.updateStatus(AiSummaryStatus.FAILED, "요약 실패", "AI 분석 중 오류가 발생했습니다.");
        }

        aiSummaryRepository.saveAndFlush(summary);
    }

    @Transactional(readOnly = true)
    public Cheese4cutAiResponse getAiSummary(String code) {
        // 1. 앨범 코드로 앨범 존재 확인
        Album album = albumRepository.findByCode(code)
                .orElseThrow(() -> new AlbumException(AlbumErrorCode.ALBUM_NOT_FOUND));

        // 2. 해당 앨범의 치즈네컷 확인
        Cheese4cut cheese4cut = cheese4cutRepository.findByAlbumId(album.getId())
                .orElseThrow(() -> new Cheese4cutException(Cheese4cutErrorCode.CHEESE4CUT_NOT_FOUND));

        // 3. AI 요약 결과 조회
        return aiSummaryRepository.findByCheese4cutId(cheese4cut.getId())
                .map(Cheese4cutMapper::toAiResponse)
                .orElseGet(Cheese4cutAiResponse::processing);
    }
}
