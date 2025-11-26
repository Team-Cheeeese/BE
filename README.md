# BE

## API 명세서
[[Swagger] 🧀 치이이즈 API 명세서](https://dev.say-cheese.me/swagger-ui/index.html#/)

## ERD
<img width="1680" height="862" alt="Cheeeese (2)" src="https://github.com/user-attachments/assets/a523f9d5-a40e-44bf-8d5a-64b072469454" />

## 시스템 아키텍처
<img width="3812" height="2232" alt="image" src="https://github.com/user-attachments/assets/2f90fde1-3a95-4ed9-8720-009bd289c829" />

## 기술 스택
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
