# 📱 Zero-Click Briefing 프로토타입 가이드

> ⚠️ **주의**: 이 문서는 **경진대회 프로토타입 버전**입니다.
> 실제 배포 버전이 아니며, 로컬 환경에서만 작동합니다.

**GitHub 저장소**: https://github.com/chaehunJeong/zero-click-briefing

---

## 🌅 Zero-Click Briefing이란?

**클릭 없이, 설정 없이, 자동으로 작동하는 AI 아침 브리핑 프로토타입**

- ⏰ 매일 아침 출근 전, 자동으로 알림
- 🌤️ 오늘의 날씨 + 교통 정보
- 🤖 AI가 당신의 패턴을 학습해서 최적의 시간에 알림
- 💰 외부 API 비용 $0 (로컬 LLM 사용)

**현재 상태**:
- ✅ 핵심 기능 구현 완료
- ⚠️ 로컬 서버 필수 (클라우드 배포 전)
- 🔬 프로토타입 / MVP 단계

---

## 🛠️ 설치 및 실행 (개발/테스트 환경)

### 0단계: 저장소 클론

```bash
git clone https://github.com/chaehunJeong/zero-click-briefing.git
cd zero-click-briefing
```

### 사전 요구사항

**필수**:
- macOS / Linux (권장)
- Java 21+
- Flutter SDK 3.x+
- Ollama (로컬 LLM)

**선택**:
- Xcode 15+ (iOS 빌드)
- Android Studio (Android 빌드)

---

### 1단계: Ollama 설치 및 실행

```bash
# Ollama 설치
curl -fsSL https://ollama.ai/install.sh | sh

# 모델 다운로드
ollama pull qwen2.5:3b

# 서버 실행 (계속 켜두기)
ollama serve
```

---

### 2단계: Spring Boot 백엔드 실행

```bash
# 저장소 클론
git clone https://github.com/chaehunJeong/zero-click-briefing.git
cd zero-click-briefing/api_sero_click

# 서버 실행
./gradlew bootRun

# 확인
curl http://localhost:8080/actuator/health
# {"status":"UP"} 응답 확인
```

---

### 3단계: Flutter 앱 실행

```bash
cd ../app_zero_click

# 의존성 설치
flutter pub get

# iOS 시뮬레이터 실행
flutter run -d "iPhone 15 Pro"

# 또는 Android 에뮬레이터
flutter run -d emulator-5554

# 또는 Chrome 웹
flutter run -d chrome
```

---

## ⚠️ 중요: 네트워크 설정

### 로컬 서버 주소

**현재 설정** (`lib/api_service_handlers.dart:6`):
```dart
static const String baseUrl = 'http://localhost:8080';
```

### 실제 기기 테스트 시

**iOS/Android 실제 기기**에서 테스트하려면:

1. **맥북 IP 주소 확인**:
   ```bash
   ifconfig | grep "inet " | grep -v 127.0.0.1
   # 예: 192.168.0.10
   ```

2. **Flutter 코드 수정**:
   ```dart
   static const String baseUrl = 'http://192.168.0.10:8080';
   ```

3. **재실행**:
   ```bash
   flutter run
   ```

**주의**: 맥북과 폰이 **같은 Wi-Fi**에 연결되어야 합니다!

---

## 🚀 첫 시작 가이드

### 1단계: 앱 실행

앱을 처음 실행하면 다음 권한을 요청합니다:

- ✅ **위치 권한** (필수): 집 위치 및 출발 시간 학습
- ✅ **알림 권한** (필수): 브리핑 알림 수신
- ✅ **백그라운드 실행** (권장): 자동 위치 수집

**모든 권한을 허용해주세요!**

---

### 2단계: 아무것도 안하기 😎

**진짜입니다!** 설정할 게 없어요.

- ❌ 집 주소 입력 불필요
- ❌ 출근 시간 설정 불필요
- ❌ 알림 시간 설정 불필요

AI가 자동으로 학습합니다!

---

### 3단계: 3일간 평소처럼 생활

앱은 백그라운드에서 조용히 데이터를 수집합니다:

- 🌙 **밤 시간** (22:00~06:00): 집 위치 파악
- 🌅 **아침 시간** (06:00~10:00): 출발 시간 감지

**👉 평일 3일만 출근하면 학습 완료!**

---

### 4단계: 알림 받기 🎉

학습이 완료되면:

- 📱 **평일 아침, 출발 30분 전** 자동 알림
- 🌤️ 오늘의 날씨
- 🚗 예상 교통 상황
- 📍 현재 위치 (예: "서울 강서구")

---

## 🎯 주요 기능

### 1️⃣ 자동 패턴 학습

**집 위치 파악**
```
밤 10시 ~ 새벽 6시 사이 가장 자주 있는 곳 = 집
최소 3일 데이터 수집 후 확정
```

**출발 시간 감지**
```
평일 오전 6시 ~ 10시 사이
집에서 500m 이상 떨어진 시점 = 출발
최소 3번 반복 패턴 확인
```

**알림 시간 결정**
```
예측된 출발 시간 30분 전 자동 알림
예: 평소 8시 출발 → 7시 30분 알림
```

---

### 2️⃣ 스마트 브리핑

**자연스러운 한국어**
```
"안녕하세요! 오늘 서울 강서구 기온 12°C,
구름 약간 있어요. 출근길 약 35분 소요 예상됩니다.
좋은 하루 되세요!"
```

**포함 정보**
- 🌡️ 현재 기온 + 체감 온도
- ☁️ 날씨 상태 (맑음/흐림/비 등)
- 🚗 예상 교통 시간
- 📍 현재 위치 (주소)

---

### 3️⃣ Apple Watch 알림 (iOS 전용)

iPhone 알림이 **자동으로 Apple Watch에 전달**됩니다 (iOS 기본 기능)

**알림 내용**:
- 브리핑 제목과 요약 표시
- 손목만 들어도 확인 가능
- Watch 앱 설치 불필요

**참고**: 독립적인 Watch 앱은 미구현 상태입니다.

---

## ⚙️ 설정 (선택사항)

앱 하단 **"설정"** 탭:

### 알림 설정
- ✅ 알림 켜기/끄기
- 🔔 알림 소리 변경
- 📳 진동 패턴 변경

### 학습 진행률 확인
- 📊 현재 수집된 데이터 개수
- 🏠 밤 시간 데이터: X개
- 🌅 아침 출발 데이터: X개
- 📈 학습 진행률: XX%

### 내 패턴 확인
- 📅 날짜별 위치 기록
- 🕐 시간대별 통계
- 🗺️ 지도에서 위치 확인

---

## 🐛 문제 해결 (트러블슈팅)

### 1. 서버 연결 실패

**증상**: "Connection refused" 또는 앱에서 데이터 로딩 안됨

**해결**:
```bash
# 1. Spring Boot 서버 실행 확인
ps aux | grep java

# 2. 서버 재시작
cd api_sero_click
./gradlew bootRun

# 3. 서버 상태 확인
curl http://localhost:8080/actuator/health
```

---

### 2. Ollama LLM 응답 없음

**증상**: 브리핑이 생성되지 않음

**해결**:
```bash
# 1. Ollama 실행 확인
ps aux | grep ollama

# 2. Ollama 재시작
killall ollama
ollama serve

# 3. 모델 확인
ollama list
# qwen2.5:3b가 있어야 함

# 4. 테스트
ollama run qwen2.5:3b "안녕하세요"
```

---

### 3. Flutter 빌드 실패

**증상**: `flutter run` 시 에러

**해결**:
```bash
# 1. 의존성 재설치
flutter clean
flutter pub get

# 2. iOS (macOS만)
cd ios
pod install
cd ..

# 3. 재실행
flutter run
```

---

### 4. 실제 기기에서 앱 작동 안됨

**증상**: 시뮬레이터는 되는데 실제 폰은 안됨

**해결**:
1. **IP 주소 확인**:
   ```bash
   ifconfig | grep "inet " | grep -v 127.0.0.1
   ```

2. **코드 수정** (`lib/api_service_handlers.dart:6`):
   ```dart
   static const String baseUrl = 'http://YOUR_IP:8080';
   ```

3. **같은 Wi-Fi 연결 확인**

---

### 5. H2 Database 에러

**증상**: "Table not found" 또는 데이터 조회 실패

**해결**:
```bash
# 서버 재시작 (H2 인메모리 DB 재생성)
cd api_sero_click
./gradlew bootRun
```

**H2 Console 확인**:
- URL: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:testdb`
- `SELECT * FROM PATTERN_DATA` 실행

---

## 📞 문의 및 피드백

### 프로토타입 관련 문의

**GitHub Issues**: [https://github.com/chaehunJeong/zero-click-briefing/issues](https://github.com/chaehunJeong/zero-click-briefing/issues)

**이메일**: chaehunjeong@example.com

**문의 시 포함해주세요**:
- 사용 환경 (macOS 버전, Java 버전)
- 에러 로그 또는 스크린샷
- 재현 방법

---

### 피드백 환영!

**이 프로토타입을 개선하는 데 도움을 주세요!**

- 💡 기능 제안
- 🐛 버그 제보
- ⭐ 사용 후기

---

## 🔮 로드맵 (향후 계획)

### Phase 1: 프로토타입 (현재) ✅

- ✅ 패턴 학습 알고리즘 구현
- ✅ 로컬 LLM 기반 브리핑
- ✅ Flutter 앱 + Spring Boot 백엔드
- ⚠️ 로컬 환경에서만 작동

### Phase 2: 클라우드 배포 (3개월)

- ☁️ AWS/GCP 서버 배포
- 🗄️ PostgreSQL 영구 저장
- 🔐 사용자 인증 (OAuth)
- 🌐 공개 베타 테스트 (100명)

### Phase 3: 프로덕션 준비 (6개월)

- 📱 App Store / Google Play 출시
- 💳 프리미엄 기능 (캘린더 연동)
- 🌍 다국어 지원
- 🎨 UI/UX 개선

### Phase 4: 고도화 (1년+)

- 🤖 On-Device LLM 연구
- 🏠 스마트홈 연동
- 🎙️ 음성 비서 통합

---

## 💡 개발자를 위한 팁

### Tip 1: 디버깅 모드

**서버 로그 확인**:
```bash
cd api_sero_click
./gradlew bootRun --info
```

**H2 Console 접속**:
- URL: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: (비어있음)

---

### Tip 2: API 테스트

**빠른 시뮬레이션**:
```bash
# 1주일 패턴 생성
curl -X POST "http://localhost:8080/api/patterns/simulate-week?userId=demo"

# 브리핑 확인
curl "http://localhost:8080/api/briefing/demo" | jq

# 패턴 삭제
curl -X DELETE "http://localhost:8080/api/patterns/all?userId=demo"
```

---

### Tip 3: Flutter Hot Reload

코드 수정 후:
- `r` 키: Hot Reload (빠름)
- `R` 키: Hot Restart (상태 초기화)

---

### Tip 4: 실제 기기 테스트

**iPhone**:
```bash
# USB 연결 후
flutter run -d <DEVICE_ID>

# IP 주소 변경 잊지 말기!
```

**Android**:
```bash
# USB 디버깅 켜기
# adb devices 확인
flutter run -d <DEVICE_ID>
```

---

## ⚠️ 주의사항 (프로토타입 제약)

1. **로컬 서버 필수** ⭐ 가장 중요!
   - Spring Boot 서버가 **항상 실행**되어야 함
   - 서버 종료 시 앱 작동 안됨
   - 같은 네트워크(Wi-Fi) 필요

2. **인터넷 연결 필수**
   - 브리핑 생성 시 서버 연결 필요
   - 오프라인에서는 작동 안됨

3. **위치 서비스 필수**
   - 위치 권한 없이는 작동 불가
   - "항상 허용" 권장

4. **평일 3일 이상 필요**
   - 주말만으로는 학습 불가
   - 최소 평일 3일 출근 필요

5. **불규칙한 일정**
   - 매일 다른 시간 출근하면 학습 어려움
   - 주 3회 이상 같은 패턴 필요

6. **데이터 휘발성**
   - 서버 재시작 시 모든 패턴 데이터 삭제
   - H2 인메모리 DB 사용 중

7. **프로덕션 미준비**
   - 테스트/데모 용도로만 사용
   - 실제 일상 사용 권장 안함

---

**Zero-Click Briefing 프로토타입 가이드 (2024.11)**
