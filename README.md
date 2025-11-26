# Zero-Click Briefing

> 트렌드 코리아 2026 "제로클릭 시대"를 적용한 자동 아침 브리핑 앱

## 프로젝트 개요

**Zero-Click Briefing**은 사용자가 아무런 설정 없이도 자동으로 생활 패턴을 학습하여 매일 아침 필요한 정보를 알림으로 제공하는 스마트 브리핑 앱입니다.

### 핵심 특징

- **완전 자동 학습**: 집 위치, 출발 시간 등 사용자 설정 불필요
- **로컬 LLM 사용**: Ollama 활용으로 API 비용 $0
- **실시간 정보**: 날씨, 교통(자동차/대중교통) 정보 제공
- **크로스 플랫폼**: iOS, Android, Web 지원
- **Apple Watch 연동**: iPhone 알림 자동 미러링

## 기술 스택

### Backend
- Spring Boot 3.5.7
- Java 17
- JPA + H2 Database
- Ollama (qwen2.5:3b)

### Frontend
- Flutter 3.x
- Dart
- flutter_local_notifications
- workmanager (백그라운드 작업)

### AI/ML
- Ollama (Local LLM)
- qwen2.5:3b 모델

## 프로젝트 구조

```
.
├── api_sero_click/          # Spring Boot Backend
│   ├── src/main/java/
│   │   └── kr/co/reo/api_sero_click/
│   │       ├── api/         # REST API Controllers & Services
│   │       ├── confg/       # Spring Configuration
│   │       └── model/       # JPA Entities & DTOs
│   └── build.gradle
│
├── app_zero_click/          # Flutter Frontend
│   ├── lib/
│   │   ├── screens/         # UI Screens
│   │   ├── services/        # API Services
│   │   └── bootstrap.dart   # Background Worker
│   └── pubspec.yaml
│
├── USER_MANUAL.md           # 사용자 매뉴얼
├── DEVELOPER_GUIDE.md       # 개발자 가이드
├── PRESENTATION.md          # 발표 자료 (Marp)
└── SUBMISSION_PACKAGE.md    # 경진대회 제출 가이드
```

## 주요 기능

### 1. 자동 패턴 학습
- 야간 GPS 데이터로 집 위치 자동 감지 (22:00-06:00)
- 평일 아침 이동 패턴으로 출발 시간 학습
- 최소 3회 이상 발생 시 85% 신뢰도로 패턴 확정

### 2. 스마트 브리핑
- 날씨: 현재 온도, 날씨 상태
- 교통: 자동차/대중교통 소요시간, 혼잡도, 추천 경로
- 매일 아침 학습된 출발 시간 30분 전 자동 알림

### 3. Zero-Click 철학
- 앱 설치 후 아무 설정 없이 자동 동작
- 백그라운드에서 패턴 자동 학습
- 필요한 순간에 자동으로 알림 전송

## 빠른 시작

### 사전 요구사항
- Java 17+
- Flutter 3.x
- Ollama (qwen2.5:3b 모델)
- macOS/Linux/Windows

### Backend 실행
```bash
cd api_sero_click
./gradlew bootRun
```

### Frontend 실행
```bash
cd app_zero_click
flutter pub get
flutter run
```

### Ollama 실행
```bash
ollama run qwen2.5:3b
```

## 문서

- [USER_MANUAL.md](USER_MANUAL.md) - 사용자 매뉴얼
- [DEVELOPER_GUIDE.md](DEVELOPER_GUIDE.md) - 개발자 가이드
- [PRESENTATION.md](PRESENTATION.md) - 발표 자료
- [QUICK_TEST_GUIDE.md](QUICK_TEST_GUIDE.md) - 빠른 테스트 가이드

## 경진대회 정보

- **주제**: 트렌드 코리아 2026 적용 아이디어
- **적용 트렌드**: Zero-Click 시대
- **제출 마감**: 2024년 11월 28일

## 라이선스

이 프로젝트는 경진대회 출품작으로 제작되었습니다.

## 개발자

- Chaehun Jeong ([@chaehunJeong](https://github.com/chaehunJeong))
