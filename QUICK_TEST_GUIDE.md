# ⚡ 빠른 테스트 가이드 - "집에서 나가는 시간" 패턴 학습

## 🚀 Spring Boot 서버 재시작 필수!

서버를 재시작해야 새로운 패턴 감지 로직이 적용됩니다!

```bash
# IntelliJ IDEA: 정지 → 실행
# 또는
cd /Users/chaehunjeong/Documents/dev/api_sero_click
./gradlew bootRun
```

---

## ⚡ 1분 만에 테스트하기

### 1단계: 시뮬레이션 데이터 생성

**3일간 평일 출근 패턴을 즉시 생성**:

```bash
curl -X POST "http://localhost:8080/api/patterns/simulate?userId=test_user"
```

**응답 예시**:
```json
{
  "success": true,
  "message": "시뮬레이션 패턴 데이터 생성 완료",
  "userId": "test_user",
  "createdCount": 21,
  "description": "3일간 평일 출근 패턴 (밤 집 위치 + 오전 8시 출발)"
}
```

**생성되는 데이터**:
```
3일 전 (평일):
  - 밤 22시, 23시, 01시, 02시, 05시, 06시 → 집 (37.5450, 126.8411)
  - 오전 08시 → 출발 (37.5500, 126.8480) ← 집에서 600m 떨어짐

2일 전 (평일):
  - 밤 22시, 23시, 01시, 02시, 05시, 06시 → 집
  - 오전 08시 → 출발

1일 전 (평일):
  - 밤 22시, 23시, 01시, 02시, 05시, 06시 → 집
  - 오전 08시 → 출발

→ 총 21개 데이터 (3일 × 7개)
```

---

### 2단계: 생성된 패턴 확인

```bash
curl "http://localhost:8080/api/patterns/test_user" | jq
```

**확인할 내용**:
- 밤 시간대 데이터: 집 위치 (37.5450, 126.8411)
- 오전 8시 데이터: 출발 위치 (37.5500, 126.8480)
- 총 21개 데이터

---

### 3단계: AI 브리핑 테스트

**현재 시각이 평일 오전 7~8시라면**:

```bash
curl "http://localhost:8080/api/briefing/test_user" | jq
```

**예상 결과** (평일 오전 7~8시):
```json
{
  "shouldNotify": true,  ← ✅ 알림 ON!
  "reason": "평소 출발 시간 30분 전입니다",
  "confidence": 0.85,
  "predictedTime": "08:30",
  "briefingText": "안녕하세요! 오늘 아침 12℃, 출근길 35분 소요 예상..."
}
```

**예상 결과** (그 외 시간):
```json
{
  "shouldNotify": false,  ← 아직 출발 시간 아님
  "reason": "아직 출발 시간이 아닙니다",
  "confidence": 0.85,
  "predictedTime": "08:30",
  "briefingText": "안녕하세요! 지금은 12℃..."
}
```

---

### 4단계: 패턴 삭제 (다시 테스트하려면)

```bash
curl -X DELETE "http://localhost:8080/api/patterns/all?userId=test_user"
```

**응답**:
```json
{
  "success": true,
  "message": "모든 패턴 데이터 삭제 완료",
  "userId": "test_user",
  "deletedCount": 21
}
```

---

## 🔍 패턴 분석 로직 확인

### 어떻게 "집"을 파악하나요?

```bash
# 시뮬레이션 후 브리핑 API를 디버그 모드로 실행하면:

1. 밤 시간대(22~06시) 데이터 필터링
   → 18개 (3일 × 6개)

2. 집 위치 평균 계산
   → lat: 37.5450, lon: 126.8411

3. 평일 오전(6~10시) 데이터 필터링
   → 3개 (3일 × 1개)

4. 집에서 500m 이상 떨어진 데이터 찾기
   → 3개 (모두 08시)

5. 평균 출발 시간
   → 8시

6. 알림 판단 (현재가 7~8시 & 평일)
   → shouldNotify: true
```

---

## 📊 시뮬레이션 vs 실제 데이터

### 시뮬레이션 (지금 바로 테스트)

**장점**:
- ✅ 1분 만에 테스트 가능
- ✅ 경진대회 시연용
- ✅ 패턴 학습 로직 검증

**단점**:
- ❌ 실제 사용자 행동 반영 안됨
- ❌ 알림 타이밍 테스트 어려움 (현재 시각 기준)

---

### 실제 데이터 (3일 소요)

**장점**:
- ✅ 실제 사용자 패턴 학습
- ✅ 정확한 집 위치 파악
- ✅ 개인화된 알림 타이밍

**단점**:
- ❌ 최소 3일 필요
- ❌ 평일 3일 연속 출근 필요

---

## 🎯 경진대회 시연 시나리오

### 시연 흐름

1. **문제 제기** (30초):
   > "매일 아침 날씨, 교통을 확인하는 건 번거롭습니다"

2. **시뮬레이션 실행** (1분):
   ```bash
   # 화면 공유하면서
   curl -X POST "http://localhost:8080/api/patterns/simulate?userId=demo_user"
   ```
   > "3일간의 출근 패턴을 학습했습니다"

3. **패턴 데이터 시각화** (1분):
   ```bash
   curl "http://localhost:8080/api/patterns/demo_user" | jq
   ```
   > "밤에는 강서구 집에 계시고, 평일 오전 8시에 출발하시네요"

4. **브리핑 결과 확인** (1분):
   ```bash
   curl "http://localhost:8080/api/briefing/demo_user" | jq
   ```
   > "AI가 출발 30분 전인 7시 30분에 알림을 보내줍니다!"

5. **Flutter 앱 시연** (2분):
   - iPhone에서 앱 실행
   - "테스트 알림" 버튼 클릭
   - 5초 후 iPhone + Apple Watch에 알림 표시

6. **강조** (30초):
   > "사용자는 아무 설정도 안했습니다!"
   > "앱이 자동으로 학습하고, 최적의 시간에 알림!"
   > "진정한 제로클릭입니다!"

---

## 🐛 트러블슈팅

### Q1: `shouldNotify: false`가 계속 나옵니다

**원인**: 현재 시각이 알림 시간대(7~8시)가 아님

**해결**:
- 평일 오전 7~8시에 테스트하거나
- [AIBriefingService.java:166-169](api_sero_click/src/main/java/kr/co/reo/api_sero_click/api/service/AIBriefingService.java:166-169)의 시간 조건을 임시로 수정:
  ```java
  // 테스트용: 항상 true 반환
  boolean isBeforeDeparture = true;
  ```

---

### Q2: 시뮬레이션 데이터가 저장 안됩니다

**원인**: H2 인메모리 DB는 서버 재시작 시 초기화됨

**해결**:
- 서버가 실행 중인지 확인
- 시뮬레이션 API 재실행

---

### Q3: 거리 계산이 안맞습니다

**원인**: 좌표가 너무 가까움

**테스트**:
```bash
# 거리 계산 확인
집: 37.5450, 126.8411
출발: 37.5500, 126.8480

→ 약 600~700m (정상)
```

---

## 📱 Flutter 앱에서 테스트

### 백그라운드 워커 확인

```dart
// bootstrap.dart:51-61
// 15분마다 위치 수집 확인

// iOS: 설정 > 일반 > 백그라운드 앱 새로고침 ON
// Android: 배터리 최적화 OFF
```

### 수동 패턴 전송 테스트

```dart
// Flutter 앱에서
await ApiService.sendUserPattern({
  'timestamp': DateTime.now().toIso8601String(),
  'latitude': 37.5450,
  'longitude': 126.8411,
  'hour': DateTime.now().hour,
  'dayOfWeek': DateTime.now().weekday,
});
```

---

## ✅ 체크리스트

### Spring Boot
- [ ] 서버 재시작 완료
- [ ] 시뮬레이션 API 작동 (`/api/patterns/simulate`)
- [ ] 패턴 조회 API 작동 (`/api/patterns/test_user`)
- [ ] 브리핑 API 작동 (`/api/briefing/test_user`)

### Flutter
- [ ] 백그라운드 권한 허용
- [ ] 15분 주기 워커 등록 확인
- [ ] API 연동 확인

### 패턴 학습
- [ ] 밤 시간 데이터 수집 (집 위치)
- [ ] 오전 출발 데이터 수집
- [ ] 거리 계산 정상 작동
- [ ] `shouldNotify: true` 확인

---

**준비 완료!** 이제 Spring Boot 서버를 재시작하고 테스트하세요! 🚀
