package com.cheeeese.cheese4cut.application;

import com.cheeeese.album.application.logger.AlbumLogger;
import com.cheeeese.album.application.validator.AlbumValidator;
import com.cheeeese.album.domain.Album;
import com.cheeeese.album.domain.UserAlbum;
import com.cheeeese.album.domain.type.Role;
import com.cheeeese.album.exception.AlbumException;
import com.cheeeese.album.exception.code.AlbumErrorCode;
import com.cheeeese.album.infrastructure.persistence.AlbumRepository;
import com.cheeeese.album.infrastructure.persistence.UserAlbumRepository;
import com.cheeeese.cheese4cut.application.validator.Cheese4cutValidator;
import com.cheeeese.cheese4cut.domain.Cheese4cut;
import com.cheeeese.cheese4cut.dto.request.Cheese4cutFixedRequest;
import com.cheeeese.cheese4cut.dto.response.Cheese4cutFinalResponse;
import com.cheeeese.cheese4cut.dto.response.Cheese4cutPreviewResponse;
import com.cheeeese.cheese4cut.dto.response.Cheese4cutResponse;
import com.cheeeese.cheese4cut.exception.Cheese4cutException;
import com.cheeeese.cheese4cut.exception.code.Cheese4cutErrorCode;
import com.cheeeese.cheese4cut.infrastructure.mapper.Cheese4cutMapper;
import com.cheeeese.cheese4cut.infrastructure.persistence.Cheese4cutRepository;
import com.cheeeese.global.security.CustomUserDetails;
import com.cheeeese.global.util.resolver.CdnUrlResolver;
import com.cheeeese.photo.domain.Photo;
import com.cheeeese.photo.domain.PhotoStatus;
import com.cheeeese.photo.infrastructure.persistence.PhotoLikesRepository;
import com.cheeeese.photo.infrastructure.persistence.PhotoRepository;
import com.cheeeese.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class Cheese4cutService {

    private final Cheese4cutRepository cheese4cutRepository;
    private final AlbumRepository albumRepository;
    private final PhotoRepository photoRepository;
    private final PhotoLikesRepository photoLikesRepository;
    private final UserAlbumRepository userAlbumRepository;
    private final AlbumValidator albumValidator;
    private final Cheese4cutValidator cheese4cutValidator;
    private final CdnUrlResolver cdnUrlResolver;
    private final ApplicationEventPublisher eventPublisher;
    private final AlbumLogger albumLogger;

    @Transactional(readOnly = true)
    public Cheese4cutResponse getCheese4cutByAlbumCode(Authentication authentication, String code) {
        Album album = albumRepository.findByCode(code)
                .orElseThrow(() -> new AlbumException(AlbumErrorCode.ALBUM_NOT_FOUND));

        Optional<Cheese4cut> cheese4cutOptional = cheese4cutRepository.findByAlbumId(album.getId());

        if (cheese4cutOptional.isPresent()) {
            Cheese4cut cheese4cut = cheese4cutOptional.get();

            List<Cheese4cutFinalResponse.FinalPhotoInfo> photos = cheese4cut.getPhotos().stream()
                    .map(p -> Cheese4cutMapper.toFinalPhotoInfo(
                            p.getPhotoId(),
                            cdnUrlResolver.resolveThumbnail(p.getThumbnailImageUrl()),
                            p.getPhotoRank()
                    ))
                    .toList();

            return Cheese4cutMapper.toFinalResponse(photos);
        }

        User currentUser = extractUser(authentication);

        Role myRole = null;
        Long currentUserId = currentUser != null ? currentUser.getId() : null;

        if (currentUserId != null) {
            Optional<UserAlbum> myUserAlbumOptional = userAlbumRepository.findByUserIdAndAlbumId(currentUserId, album.getId());

            if (myUserAlbumOptional.isPresent()) {
                myRole = myUserAlbumOptional.get().getRole();
            }
        }

        return getPreviewResponse(album.getId(), album.getParticipant(), myRole);
    }

    private Cheese4cutResponse getPreviewResponse(Long albumId, int participant, Role myRole) {
        List<Long> topPhotoIds = photoRepository.findTop4CompletedPhotoIdsByLikes(
                albumId,
                PhotoStatus.COMPLETED,
                PageRequest.of(0, 4)
        );

        if (topPhotoIds.size() < 4) {
            throw new Cheese4cutException(Cheese4cutErrorCode.INSUFFICIENT_COUNT_FOR_CHEESE4CUT);
        }

        List<Photo> orderedPhotos = getOrderedPhotos(topPhotoIds);

        long uniqueLikesCount = photoLikesRepository.countDistinctUserIdsByPhotoIds(topPhotoIds);

        List<Cheese4cutPreviewResponse.PreviewPhotoInfo> resolvedPhotoInfos =
                IntStream.range(0, orderedPhotos.size())
                        .mapToObj(index -> {
                            Photo p = orderedPhotos.get(index);
                            return Cheese4cutMapper.toPreviewPhotoInfo(
                                    p.getId(), cdnUrlResolver.resolveThumbnail(p.getThumbnailUrl()), index+1
                            );
                        })
                        .toList();

        return Cheese4cutMapper.toPreviewResponse(resolvedPhotoInfos, uniqueLikesCount, participant, myRole);
    }

    @Transactional
    public void finalizeCheese4cut(User user, String code, Cheese4cutFixedRequest request) {
        Album album = albumValidator.validateAlbumCode(code);

        albumValidator.validateAlbumEntry(album, user);

        cheese4cutValidator.validateUserIsMaker(album, user);

        if (cheese4cutRepository.findByAlbumId(album.getId()).isPresent()) {
            throw new Cheese4cutException(Cheese4cutErrorCode.CHEESE4CUT_ALREADY_FINALIZED);
        }

        cheese4cutValidator.validateFinalizePhotos(album, request.photoIds());

        List<Photo> orderedPhotos =
                photoRepository.findAllByIdInOrderByLikesDescCreatedDesc(request.photoIds());

        if (orderedPhotos.size() != request.photoIds().size())
            throw new Cheese4cutException(Cheese4cutErrorCode.INSUFFICIENT_COUNT_FOR_CHEESE4CUT);

        cheese4cutRepository.save(Cheese4cutMapper.toEntity(album, orderedPhotos));

        albumLogger.logCheese4CutFinalized(user.getId(), request.photoIds(), album.getCode());
    }

    private List<Photo> getOrderedPhotos(List<Long> photoIds) {
        List<Photo> photos = photoRepository.findAllByIdIn(photoIds);

        Map<Long, Photo> photoMap = photos.stream()
                .collect(Collectors.toMap(Photo::getId, Function.identity()));

        List<Photo> orderedPhotos = photoIds.stream()
                .map(photoId -> {
                    Photo photo = photoMap.get(photoId);
                    if (photo == null) {
                        throw new Cheese4cutException(Cheese4cutErrorCode.INSUFFICIENT_COUNT_FOR_CHEESE4CUT);
                    }
                    return photo;
                })
                .toList();

        if (orderedPhotos.size() != photoIds.size()) {
            throw new Cheese4cutException(Cheese4cutErrorCode.INSUFFICIENT_COUNT_FOR_CHEESE4CUT);
        }
        return orderedPhotos;
    }

    private User extractUser(Authentication authentication) {
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return null;
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof CustomUserDetails customUserDetails) {
            return customUserDetails.getUser();
        }
        return null;
    }

    @Transactional
    public void finalizeCheese4cutWithAi(User user, String code, Cheese4cutFixedRequest request) {
        // 1. 기존 검증 로직 (기존 코드와 동일하게 유지)
        Album album = albumValidator.validateAlbumCode(code);

        // 앨범 만료 확인 & 사용자 블랙리스트 확인
        albumValidator.validateAlbumEntry(album, user);

        cheese4cutValidator.validateUserIsMaker(album, user);

        // 이미 확정된 치즈네컷이 있는지 확인
        if (cheese4cutRepository.findByAlbumId(album.getId()).isPresent()) {
            throw new Cheese4cutException(Cheese4cutErrorCode.CHEESE4CUT_ALREADY_FINALIZED);
        }

        // 사진 개수 및 앨범 소속 여부 검증
        cheese4cutValidator.validateFinalizePhotos(album, request.photoIds());

        // 2. 사진 데이터 조회 및 정렬
        List<Photo> orderedPhotos =
                photoRepository.findAllByIdInOrderByLikesDescCreatedDesc(request.photoIds());

        if (orderedPhotos.size() != request.photoIds().size()) {
            throw new Cheese4cutException(Cheese4cutErrorCode.INSUFFICIENT_COUNT_FOR_CHEESE4CUT);
        }

        // 3. 치즈네컷 엔티티 저장 (AI 요약 엔티티의 외래키가 됨)
        Cheese4cut cheese4cut = cheese4cutRepository.save(Cheese4cutMapper.toEntity(album, orderedPhotos));

        // 4. [추가] 비동기 AI 파이프라인 시작
        eventPublisher.publishEvent(
                new Cheese4cutFinalizedEvent(cheese4cut, album, orderedPhotos)
        );

        // 5. 로깅
        albumLogger.logCheese4CutFinalized(user.getId(), request.photoIds(), album.getCode());
    }
}
