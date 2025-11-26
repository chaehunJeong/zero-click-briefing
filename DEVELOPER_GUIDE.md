# 🛠️ Zero-Click Briefing 개발자 가이드

## 📋 목차

1. [시스템 요구사항](#시스템-요구사항)
2. [프로젝트 구조](#프로젝트-구조)
3. [로컬 개발 환경 설정](#로컬-개발-환경-설정)
4. [API 문서](#api-문서)
5. [데이터베이스 스키마](#데이터베이스-스키마)
6. [핵심 알고리즘](#핵심-알고리즘)
7. [빌드 및 배포](#빌드-및-배포)
8. [테스트](#테스트)
9. [트러블슈팅](#트러블슈팅)

---

## 🖥️ 시스템 요구사항

### Backend (Spring Boot)

- **Java**: 21 이상
- **Spring Boot**: 3.x
- **Ollama**: 로컬 LLM 서버
- **Gradle**: 8.x

### Frontend (Flutter)

- **Flutter SDK**: 3.x 이상
- **Dart**: 3.x 이상
- **Xcode**: 15+ (iOS 빌드)
- **Android Studio**: 최신 버전

### 운영체제

- macOS (권장)
- Linux
- Windows (WSL 권장)

---

## 📁 프로젝트 구조

```
/Users/chaehunjeong/Documents/dev/
├── api_sero_click/          # Spring Boot Backend
│   ├── src/main/java/kr/co/reo/api_sero_click/
│   │   ├── api/             # REST Controllers
│   │   │   ├── WeatherController.java
│   │   │   ├── TrafficController.java
│   │   │   ├── PatternController.java
│   │   │   ├── ZeroClickBriefingController.java
│   │   │   ├── GeocodingController.java
│   │   │   └── DemoComparisonController.java
│   │   ├── api/service/     # Business Logic
│   │   │   ├── WeatherService.java
│   │   │   ├── TrafficService.java
│   │   │   ├── AIBriefingService.java
│   │   │   ├── GeocodingService.java
│   │   │   └── PatternLearningService.java
│   │   ├── model/           # Data Models
│   │   │   ├── WeatherResponse.java
│   │   │   ├── TrafficResponse.java
│   │   │   ├── BriefingResponse.java
│   │   │   └── PatternData.java
│   │   └── repository/      # Data Access
│   │       └── PatternRepository.java
│   ├── build.gradle         # Gradle 설정
│   └── application.properties
│
└── app_zero_click/          # Flutter Frontend
    ├── lib/
    │   ├── main.dart        # 앱 진입점
    │   ├── bootstrap.dart   # 백그라운드 워커
    │   ├── api_service_handlers.dart  # API 호출
    │   ├── location_retrieval_service.dart  # 위치 서비스
    │   ├── screens/         # UI 화면
    │   │   ├── home_screen.dart
    │   │   ├── settings_screen.dart
    │   │   └── pattern_history_screen.dart
    │   └── services/
    │       └── settings_service.dart
    ├── pubspec.yaml         # Flutter 의존성
    ├── ios/                 # iOS 설정
    └── android/             # Android 설정
```

---

## 🚀 로컬 개발 환경 설정

### 1단계: 저장소 클론

```bash
git clone https://github.com/your-repo/zero-click-briefing.git
cd zero-click-briefing
```

---

### 2단계: Ollama 설치 및 실행

**macOS / Linux**:
```bash
# Ollama 설치
curl -fsSL https://ollama.ai/install.sh | sh

# qwen2.5:3b 모델 다운로드
ollama pull qwen2.5:3b

# Ollama 서버 실행
ollama serve
```

**확인**:
```bash
# 다른 터미널에서
ollama list
# qwen2.5:3b가 있어야 함

ollama run qwen2.5:3b "안녕하세요"
# 응답이 오면 성공
```

---

### 3단계: Spring Boot 백엔드 실행

```bash
cd api_sero_click

# Gradle Wrapper 실행 권한 부여
chmod +x gradlew

# 의존성 설치 및 빌드
./gradlew clean build

# 서버 실행
./gradlew bootRun
```

**확인**:
```bash
# 다른 터미널에서
curl http://localhost:8080/actuator/health
# {"status":"UP"} 응답이 오면 성공
```

**포트 변경** (선택사항):
```properties
# src/main/resources/application.properties
server.port=8081
```

---

### 4단계: Flutter 프론트엔드 실행

```bash
cd app_zero_click

# 의존성 설치
flutter pub get

# 실행 가능한 디바이스 확인
flutter devices

# iOS 시뮬레이터 실행
flutter run -d "iPhone 15 Pro"

# Android 에뮬레이터 실행
flutter run -d emulator-5554

# Chrome 웹 실행
flutter run -d chrome
```

---

### 5단계: 환경 변수 설정 (선택사항)

**Backend** (`application.properties`):
```properties
# H2 Database
spring.h2.console.enabled=true
spring.datasource.url=jdbc:h2:mem:testdb

# Ollama URL
ollama.base.url=http://localhost:11434
ollama.model=qwen2.5:3b

# CORS
cors.allowed.origins=*
```

**Frontend** (`lib/api_service_handlers.dart`):
```dart
class ApiService {
  static const String baseUrl = 'http://localhost:8080';
  // 실제 기기 테스트 시: 'http://YOUR_COMPUTER_IP:8080'
}
```

---

## 📡 API 문서

### Base URL
```
http://localhost:8080
```

---

### 1️⃣ 날씨 API

**GET /api/weather**

쿼리 파라미터:
- `lat` (double): 위도
- `lon` (double): 경도

응답:
```json
{
  "temperature": 12.0,
  "feelsLike": 10.0,
  "humidity": 65,
  "condition": "맑음",
  "description": "오늘 서울 강서구 기온 12°C, 하늘 맑음",
  "timestamp": "2024-11-24T07:30:00"
}
```

예시:
```bash
curl "http://localhost:8080/api/weather?lat=37.5665&lon=126.9780"
```

---

### 2️⃣ 교통 API

**GET /api/traffic**

쿼리 파라미터:
- `lat` (double): 위도
- `lon` (double): 경도

응답:
```json
{
  "duration": 35,
  "congestion": "보통",
  "description": "출근길 약 35분 소요 예상",
  "timestamp": "2024-11-24T07:30:00"
}
```

예시:
```bash
curl "http://localhost:8080/api/traffic?lat=37.5665&lon=126.9780"
```

---

### 3️⃣ 패턴 API

**POST /api/patterns**

패턴 데이터 저장

요청 바디:
```json
{
  "userId": "user_123",
  "latitude": 37.5450,
  "longitude": 126.8411,
  "hour": 8,
  "dayOfWeek": 1,
  "timestamp": "2024-11-24T08:00:00"
}
```

응답:
```json
"Pattern saved successfully"
```

---

**GET /api/patterns/{userId}**

사용자의 저장된 패턴 조회

응답:
```json
[
  {
    "id": 1,
    "userId": "user_123",
    "latitude": 37.5450,
    "longitude": 126.8411,
    "hour": 8,
    "dayOfWeek": 1,
    "createdAt": "2024-11-24T08:00:00"
  },
  ...
]
```

---

**POST /api/patterns/simulate-week**

1주일 시뮬레이션 데이터 생성 (테스트용)

쿼리 파라미터:
- `userId` (string): 사용자 ID

응답:
```json
{
  "success": true,
  "message": "1주일 출근 패턴 시뮬레이션 완료",
  "userId": "demo_user",
  "totalDataCount": 62,
  "weekPattern": {
    "weekdays": "월~금 오전 8시 출발",
    "weekends": "주말 재택",
    "home": "강서구 (37.5450, 126.8411)",
    "office": "강남역 (37.4979, 127.0276)"
  },
  "analysis": {
    "predictedDepartureTime": "08:00",
    "confidence": "높음 (5일 연속 동일 패턴)",
    "recommendedNotificationTime": "07:30"
  }
}
```

---

**DELETE /api/patterns/all**

모든 패턴 데이터 삭제 (테스트용)

쿼리 파라미터:
- `userId` (string): 사용자 ID

응답:
```json
{
  "success": true,
  "message": "모든 패턴 데이터 삭제 완료",
  "userId": "demo_user",
  "deletedCount": 62
}
```

---

### 4️⃣ 브리핑 API

**GET /api/briefing/{userId}**

AI 브리핑 생성

응답:
```json
{
  "shouldNotify": true,
  "reason": "평소 출발 시간 30분 전입니다",
  "confidence": 0.85,
  "predictedTime": "08:30",
  "briefingText": "안녕하세요! 오늘 서울 강서구 기온 12°C, 구름 약간 있어요. 출근길 약 35분 소요 예상됩니다. 좋은 하루 되세요!"
}
```

예시:
```bash
curl "http://localhost:8080/api/briefing/user_123"
```

---

### 5️⃣ Geocoding API

**GET /api/geocoding**

좌표 → 주소 변환 (LLM 기반)

쿼리 파라미터:
- `lat` (double): 위도
- `lon` (double): 경도

응답:
```json
{
  "address": "서울 강서구"
}
```

예시:
```bash
curl "http://localhost:8080/api/geocoding?lat=37.5450&lon=126.8411"
```

---

### 6️⃣ 데모/검증 API

**GET /demo/weather-comparison**

LLM 생성 날씨 vs 실제 날씨 비교

응답:
```json
{
  "timestamp": "2024-11-24T07:30:00",
  "location": "서울",
  "llm": {
    "temperature": 12.0,
    "humidity": 65,
    "condition": "맑음"
  },
  "real": {
    "temperature": 11.5,
    "humidity": 68,
    "condition": "Clear"
  },
  "comparison": {
    "temperatureDiff": "0.5°C",
    "humidityDiff": "3%",
    "accuracyScore": "92.3%",
    "verdict": "✅ 정확함"
  }
}
```

---

**GET /demo/info**

시스템 정보

응답:
```json
{
  "app": "Zero-Click Briefing System",
  "version": "1.0.0",
  "features": ["LLM 기반 날씨 생성", "..."],
  "tech_stack": {
    "backend": "Spring Boot + Ollama",
    "frontend": "Flutter",
    "llm": "qwen2.5:3b"
  },
  "cost": {
    "total": "$0/월"
  }
}
```

---

## 🗄️ 데이터베이스 스키마

### PatternData 테이블

```sql
CREATE TABLE pattern_data (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id VARCHAR(255) NOT NULL,
  latitude DOUBLE NOT NULL,
  longitude DOUBLE NOT NULL,
  hour INT NOT NULL,
  day_of_week INT NOT NULL,
  created_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_user_id ON pattern_data(user_id);
CREATE INDEX idx_hour ON pattern_data(hour);
CREATE INDEX idx_day_of_week ON pattern_data(day_of_week);
```

### H2 Console 접속

```
URL: http://localhost:8080/h2-console
JDBC URL: jdbc:h2:mem:testdb
Username: sa
Password: (비어있음)
```

---

## 🧮 핵심 알고리즘

### 1️⃣ 집 위치 파악

**`AIBriefingService.java:118-134`**

```java
// 1. 밤 시간 패턴 필터링 (22:00~06:00)
List<PatternData> nightPatterns = patterns.stream()
  .filter(p -> p.getHour() >= 22 || p.getHour() <= 6)
  .toList();

// 2. 집 위치 평균 계산
double homeLat = nightPatterns.stream()
  .mapToDouble(PatternData::getLatitude)
  .average()
  .orElse(0);

double homeLon = nightPatterns.stream()
  .mapToDouble(PatternData::getLongitude)
  .average()
  .orElse(0);
```

**조건**:
- 최소 3일 이상의 밤 시간 데이터 필요
- 반경 100m 이내 클러스터링

---

### 2️⃣ 출발 시간 감지

**`AIBriefingService.java:136-164`**

```java
// 1. 평일 오전 패턴 필터링 (06:00~10:00)
List<PatternData> weekdayMorningPatterns = patterns.stream()
  .filter(p -> p.getDayOfWeek() >= 1 && p.getDayOfWeek() <= 5)
  .filter(p -> p.getHour() >= 6 && p.getHour() <= 10)
  .toList();

// 2. 집에서 500m 이상 떨어진 시점 찾기
List<Integer> departureHours = weekdayMorningPatterns.stream()
  .filter(p -> {
    double distance = calculateDistance(homeLat, homeLon,
                                       p.getLatitude(), p.getLongitude());
    return distance > 0.5;  // 500m 이상
  })
  .map(PatternData::getHour)
  .toList();

// 3. 평균 출발 시간
double avgDepartureHour = departureHours.stream()
  .mapToInt(Integer::intValue)
  .average()
  .orElse(8.5);
```

**조건**:
- 평일 3회 이상 동일 패턴
- 신뢰도 85% 이상

---

### 3️⃣ 거리 계산 (Haversine Formula)

**`AIBriefingService.java:183-196`**

```java
private double calculateDistance(double lat1, double lon1,
                                 double lat2, double lon2) {
  final double R = 6371;  // 지구 반지름 (km)

  double dLat = Math.toRadians(lat2 - lat1);
  double dLon = Math.toRadians(lon2 - lon1);

  double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
           + Math.cos(Math.toRadians(lat1))
           * Math.cos(Math.toRadians(lat2))
           * Math.sin(dLon / 2) * Math.sin(dLon / 2);

  double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

  return R * c;  // km 단위
}
```

---

### 4️⃣ LLM 날씨 생성

**`WeatherService.java:30-80`**

```java
// 1. 계절별 온도 범위
String seasonGuide = switch (month) {
  case 11 -> "늦가을: 10~15도, 서늘하고 건조함";
  case 12, 1, 2 -> "겨울: -5~5도, 춥고 건조함";
  // ...
};

// 2. Ollama LLM 호출
String prompt = """
당신은 기상 전문가입니다.
현재 시각: %s
위치: 위도 %.4f, 경도 %.4f
계절 가이드: %s

자연스러운 날씨 설명을 한국어로 생성하세요.
""".formatted(timeOfDay, lat, lon, seasonGuide);

Map<String, Object> response = ollama.post()
  .uri("/api/generate")
  .body(Map.of("model", "qwen2.5:3b", "prompt", prompt))
  .retrieve()
  .body(Map.class);
```

---

## 🏗️ 빌드 및 배포

### Backend 빌드

```bash
cd api_sero_click

# JAR 파일 생성
./gradlew clean build

# 생성된 JAR 확인
ls build/libs/
# api_sero_click-0.0.1-SNAPSHOT.jar
```

**실행**:
```bash
java -jar build/libs/api_sero_click-0.0.1-SNAPSHOT.jar
```

---

### Frontend 빌드

**iOS (App Store)**:
```bash
cd app_zero_click

# 빌드
flutter build ios --release

# Xcode에서 Archive 및 Upload
open ios/Runner.xcworkspace
```

**Android (Google Play)**:
```bash
cd app_zero_click

# AAB 파일 생성
flutter build appbundle --release

# 생성된 파일
ls build/app/outputs/bundle/release/
# app-release.aab
```

---

### Docker 배포 (선택사항)

**Dockerfile** (Backend):
```dockerfile
FROM openjdk:21-jdk-slim

COPY build/libs/api_sero_click-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app.jar"]
```

**빌드 및 실행**:
```bash
docker build -t zero-click-backend .
docker run -p 8080:8080 zero-click-backend
```

---

## 🧪 테스트

### Backend 단위 테스트

```bash
cd api_sero_click

# 모든 테스트 실행
./gradlew test

# 특정 테스트만
./gradlew test --tests AIBriefingServiceTest
```

**예시 테스트** (`src/test/java/...`):
```java
@Test
void testPatternLearning() {
  // Given
  PatternData pattern = new PatternData();
  pattern.setUserId("test_user");
  pattern.setLatitude(37.5450);
  pattern.setLongitude(126.8411);
  pattern.setHour(8);

  // When
  patternService.savePattern(pattern);

  // Then
  List<PatternData> patterns = patternService.getUserPatterns("test_user");
  assertEquals(1, patterns.size());
}
```

---

### Frontend 단위 테스트

```bash
cd app_zero_click

# 모든 테스트 실행
flutter test

# 특정 테스트만
flutter test test/api_service_test.dart
```

**예시 테스트** (`test/api_service_test.dart`):
```dart
void main() {
  test('getBriefing returns valid data', () async {
    final briefing = await ApiService.getBriefing('test_user');

    expect(briefing, isNotNull);
    expect(briefing['shouldNotify'], isA<bool>());
    expect(briefing['briefingText'], isNotEmpty);
  });
}
```

---

### 통합 테스트

```bash
# 1. 서버 실행
cd api_sero_click && ./gradlew bootRun

# 2. 시뮬레이션 데이터 생성
curl -X POST "http://localhost:8080/api/patterns/simulate-week?userId=test"

# 3. 브리핑 API 호출
curl "http://localhost:8080/api/briefing/test" | jq

# 4. 검증
# shouldNotify: true/false 확인
# briefingText: 자연스러운 한국어 확인
```

---

## 🐛 트러블슈팅

### 문제 1: Ollama 연결 실패

**증상**:
```
Connection refused: localhost:11434
```

**해결**:
```bash
# 1. Ollama 서버 실행 확인
ps aux | grep ollama

# 2. 서버 재시작
killall ollama
ollama serve

# 3. 포트 확인
lsof -i :11434
```

---

### 문제 2: H2 Database 초기화 안됨

**증상**:
```
Table "PATTERN_DATA" not found
```

**해결**:
```bash
# 1. 서버 재시작
./gradlew bootRun

# 2. application.properties 확인
spring.jpa.hibernate.ddl-auto=create
```

---

### 문제 3: Flutter build 실패

**증상**:
```
CocoaPods not installed
```

**해결**:
```bash
# macOS
sudo gem install cocoapods

cd ios
pod install
cd ..

flutter clean
flutter pub get
flutter build ios
```

---

### 문제 4: CORS 에러

**증상**:
```
Access to XMLHttpRequest has been blocked by CORS policy
```

**해결**:

**`WebConfig.java`**:
```java
@Configuration
public class WebConfig implements WebMvcConfigurer {
  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**")
      .allowedOrigins("*")
      .allowedMethods("GET", "POST", "DELETE");
  }
}
```

---

## 📚 추가 자료

### 공식 문서
- Spring Boot: https://spring.io/projects/spring-boot
- Flutter: https://flutter.dev/docs
- Ollama: https://ollama.ai/docs

### 기술 블로그
- 패턴 학습 알고리즘: [블로그 링크]
- LLM 날씨 생성 원리: [블로그 링크]

---

## 🤝 기여하기

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

## 📄 라이선스

MIT License

---

**Happy Coding! 🚀**
