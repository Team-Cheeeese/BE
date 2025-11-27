# 🧀 치이이즈 : 딱 7일만 열리는 특별한 공유 앨범 서비스
> 🔗 서비스 링크: [https://say-cheese.me](https://say-cheese.me)

![웹 썸네일](https://github.com/user-attachments/assets/f5a6c97a-21b9-4dff-a7b7-8c12fe6e27db)

## 🧑‍🤝‍🧑 Backend Members

|**우다현** | **주정빈** |
| :--------: |:----------:|
| <a href="https://github.com/dahyun24"><img src="https://avatars.githubusercontent.com/u/123882512?v=4" width="150"> | <a href="https://github.com/zyovn"><img src="https://avatars.githubusercontent.com/u/166782961?v=4" width="150"> |
| `backend` | `backend` |

## 📜 API 명세서
[[Swagger] 🧀 치이이즈 API 명세서](https://dev.say-cheese.me/swagger-ui/index.html#/)

## 🗃️ ERD
<img width="1730" height="912" alt="Cheeeese (1)" src="https://github.com/user-attachments/assets/f612d679-78ac-4a92-8b2b-6a81bba19e55" />

## 🏛️ System Architecture
<img width="3812" height="2232" alt="image" src="https://github.com/user-attachments/assets/2f90fde1-3a95-4ed9-8720-009bd289c829" />

## 🛠️ 기술 스택
- Language & Framework
  - Spring Boot 3.5.6
  - Java 21
  - JPA

- Database & Cache
  - MySQL
  - Redis
 
- CI/CD & Deployment
  - GitHub Actions + Docker
    - 코드 푸시 → 자동 빌드 → 테스트 → Docker 이미지 생성 → 배포까지 자동화
    - 컨테이너 기반으로 환경 일관성 확보 및 무중단 배포 가능
    - 운영 환경에 맞춘 Blue-Green 방식 구현

- Monitoring & Logging
  - Promtail : 서버 로그를 수집하여 중앙 집중형 로깅 구성
  - Loki : 비용 최적화된 로그 저장소로 대량 로그 처리에 적합
  - Grafana : 대시보드를 통해 서버 상태·로그·지표 실시간 시각화
  - Prometheus : 서버/애플리케이션 메트릭 수집 및 Alert 기반 모니터링 구현

## 📂 폴더 구조
```csharp
src
└── main
    └── java
        └── com.cheeeese
            ├── domain                          # 🧩 도메인 전체 (Bounded Context 모음)
            │   ├── album                       # 📸 앨범 도메인
            │   ├── photo                       # 🖼️ 사진 도메인
            │   ├── cheese4cut                  # 🎞️ 치즈네컷 도메인
            │   ├── user                        # 👤 사용자 도메인
            │   ├── auth                        # 🔐 인증 / 인가
            │   └── oauth2                      # 🔑 소셜 로그인(OAuth2)
            │       ├── application             # 서비스 / 유스케이스
            │       ├── domain                  # 엔티티 / 비즈니스 규칙
            │       ├── dto                     # 요청·응답 DTO
            │       ├── exception               # 도메인 전용 예외
            │       ├── infrastructure          # JPA / 외부 연동
            │       └── presentation            # 컨트롤러 / API
            │
            ├── global                           # 🌍 전역 설정 / 유틸 / AOP / 공통 예외
            └── CheeeeseApplication.java         # 🚀 Spring Boot 메인 실행 파일
```


## 💬 Commit Convention
#이슈 번호 태그: 커밋 메시지 형태로 작성
> e.g. #1 feat: 카카오 로그인 구현

| Type       | 내용                                 |
| ---------- | ---------------------------------- |
| `feat`     | 새로운 기능 구현                          |
| `chore`    | 부수적인 코드 수정 및 기타 변경사항               |
| `docs`     | 문서 추가 및 수정, 삭제                     |
| `fix`      | 버그 수정                              |
| `hotfix`   | 서비스 장애 등 긴급 이슈 수정                  |
| `test`     | 테스트 코드 추가 및 수정, 삭제                 |
| `refactor` | 코드 리팩토링                            |
| `style`    | 코드 포맷팅, 세미콜론 누락 등 기능 변경 없는 스타일 수정  |
| `deploy`   | 배포 관련 작업 (CI/CD, 서버 설정, 배포 스크립트 등) |
