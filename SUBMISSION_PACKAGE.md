# 📦 Zero-Click 경진대회 제출 패키지

## 제출 일시
- **마감**: 2025년 11월 28일(금)
- **제출자**: [이름/팀명]

---

## 📂 제출 파일 목록

### 1. 서비스 접속 정보
```
서비스명: Zero-Click - AI 기반 제로클릭 루틴 브리핑

로컬 실행 (데모용):
- Spring Boot API: http://localhost:8080
- Flutter Web: http://localhost:8081

GitHub 저장소:
- 통합 저장소: https://github.com/chaehunJeong/zero-click-briefing
  (Backend + Frontend + 문서 포함)

실행 파일:
- iOS: zero_click.ipa
- Android: zero_click.apk
```

### 2. 사용자 매뉴얼
- [QUICK_TEST_GUIDE.md](QUICK_TEST_GUIDE.md) - 빠른 시작 가이드
- [AI_BRIEFING_GUIDE.md](AI_BRIEFING_GUIDE.md) - AI 브리핑 상세 설명
- [PATTERN_LEARNING_GUIDE.md](PATTERN_LEARNING_GUIDE.md) - 패턴 학습 가이드
- [WEBSTORM_FLUTTER_GUIDE.md](WEBSTORM_FLUTTER_GUIDE.md) - 개발 환경 설정

### 3. 프레젠테이션 자료
- [PRESENTATION_GUIDE.md](PRESENTATION_GUIDE.md) - PPT 구성안 + 데모 스크립트
- 슬라이드 PPT (별도 작성)
- 데모 영상 (선택사항)

---

## 🎯 프레젠테이션 필수 항목

### 1. 서비스 개발/활용 배경 ✅
**문제점**:
- 매일 아침 날씨/교통 앱을 일일이 확인하는 번거로움 (5~10분 소요)
- 기존 알림 앱들은 수동 설정 필요 ("평일 오전 8시" 직접 입력)
- 외부 API 비용 부담 (OpenWeatherMap $10/월, Google Maps $7/월)

**개발 배경**:
- 제로클릭 콘셉트: 사용자가 아무것도 설정하지 않아도 자동 작동
- AI 기술 활용: 로컬 LLM(Ollama)으로 외부 API 비용 절감
- 개인화: 각 사용자의 출퇴근 패턴을 학습하여 맞춤형 서비스

---

### 2. 과정 설명 ✅
**개발 프로세스**:

**1주차: 기획 및 설계**
- 문제 정의 및 요구사항 분석
- 기술 스택 선정 (Flutter + Spring Boot + Ollama)
- 시스템 아키텍처 설계

**2주차: 핵심 기능 개발**
- LLM 기반 날씨/교통 데이터 생성 구현
- 스마트 패턴 학습 알고리즘 개발
- Flutter 앱 UI/UX 구현

**3주차: 고도화 및 검증**
- 실시간 데이터 검증 대시보드 추가
- 1주일 시뮬레이터 구현
- 테스트 및 버그 수정

**4주차: 발표 준비**
- 프레젠테이션 자료 작성
- 데모 시나리오 준비
- 리허설

---

### 3. 특장점 및 기능 설명 ✅

**핵심 기능**:
1. **자동 패턴 학습**
   - 밤 시간대(22~06시) 위치 → 집 파악
   - 평일 오전 500m 이동 → 출발 시간 감지
   - 최소 3회 이상 패턴 → 신뢰도 검증

2. **LLM 기반 데이터 생성**
   - 날씨: 계절/시간 고려 (11월 하순 10~15도)
   - 교통: 시간대/요일 고려 (평일 출근시간 혼잡)
   - 브리핑: 자연스러운 한국어

3. **개인화 알림**
   - 각 사용자 출근 시간 자동 학습
   - 출발 30분 전 최적 타이밍 알림
   - iPhone + Apple Watch 동시 알림

**차별점**:
| 항목 | 기존 앱 | Zero-Click |
|------|---------|------------|
| 설정 | 수동 입력 필요 | 자동 학습 |
| 비용 | $42/월 | $0/월 |
| 개인화 | 획일적 | 맞춤형 |
| API | 외부 의존 | 로컬 LLM |

---

### 4. 기대 효과 ✅

**개인 사용자**:
- 시간 절약: 매일 5~10분 → 연간 30시간
- 스트레스 감소: 깜빡하고 늦는 일 방지
- 편리성 향상: 아무것도 설정 안해도 자동 작동

**비용 절감**:
- 외부 API 비용: 연 $504 절감
- 서버 호스팅: 로컬 실행으로 $0
- 무제한 사용 가능

**기술적 효과**:
- 로컬 LLM 활용 사례 제시
- AI 기반 패턴 학습 알고리즘 검증
- 크로스 플랫폼 앱 개발 경험

**확장 가능성**:
- 캘린더 연동 (일정 자동 반영)
- 스마트홈 연동 (조명/난방 자동 제어)
- 음성 브리핑 (TTS)
- 다국어 지원 (글로벌 확장)

---

### 5. 시연 ✅

**라이브 데모 순서** (3분):

**1단계: 실시간 데이터 검증** (30초)
```bash
curl http://localhost:8080/demo/weather-comparison | jq
```
→ 실제 서울 날씨 vs LLM 생성 날씨 비교
→ 정확도 85~95% 확인

**2단계: 1주일 패턴 시뮬레이션** (1분)
```bash
curl -X POST "http://localhost:8080/api/patterns/simulate-week?userId=demo_user" | jq
```
→ 월~금 8시 출근, 주말 재택 패턴 생성
→ AI가 패턴 학습하는 과정 시각화

**3단계: AI 브리핑 생성** (1분)
```bash
curl "http://localhost:8080/api/briefing/demo_user" | jq
```
→ shouldNotify: true
→ 출발 30분 전(7:30) 알림 결정
→ 자연스러운 한국어 브리핑 텍스트

**4단계: Flutter 앱 시연** (30초)
1. iPhone에서 앱 실행
2. "테스트 알림" 버튼 클릭
3. 5초 후 iPhone + Apple Watch 알림 표시
4. "사용자는 아무것도 설정하지 않았습니다!"

---

### 6. 수익화 가능성 (선택) ✅

**현재 단계**: 개인용 무료 앱

**B2B 수익화**:
- 타겟: 중소/대기업 출퇴근 관리
- 기능: 직원 출퇴근 패턴 분석, 지각 예측, 알림
- 가격: 월 $5~10/사용자
- 시장: 국내 기업 100만개 × 평균 50명 = 5천만 사용자

**B2C 수익화**:
- 기본: 무료 (날씨/교통/브리핑)
- 프리미엄: 월 $2.99
  - 음성 브리핑 (TTS)
  - 캘린더 연동
  - 스마트홈 연동
  - 다국어 지원

**시장 규모**:
- 글로벌 출퇴근 인구: 10억+
- TAM (Total Addressable Market): $10B+
- 프리미엄 전환율 5% 가정 → $500M+

**경쟁 우위**:
- 유일한 "완전 자동화" 솔루션
- 외부 API 비용 0원 → 가격 경쟁력
- 로컬 LLM → 개인정보 보호

---

## 🎬 시연 준비사항

### 환경 체크리스트
- [ ] Spring Boot 서버 실행 중 (localhost:8080)
- [ ] Ollama 실행 중 (localhost:11434)
- [ ] Flutter 앱 빌드 완료
- [ ] iPhone + Apple Watch 페어링 확인
- [ ] 터미널 준비 (jq 설치 확인)

### 백업 자료
- [ ] 스크린샷 (API 응답, 앱 화면)
- [ ] 데모 영상 (인터넷 끊김 대비)
- [ ] PPT 슬라이드

---

## 📊 기술 스택

**Frontend**:
- Flutter 3.x (iOS/Android/Web)
- Dart

**Backend**:
- Spring Boot 3.x
- Java 21
- H2 Database (in-memory)

**AI/ML**:
- Ollama (로컬 LLM)
- qwen2.5:3b 모델

**DevOps**:
- Gradle
- Git/GitHub

---

## 📞 문의

**개발자**: [이름]
**이메일**: [이메일]
**GitHub**: [GitHub 프로필]

---

**감사합니다!** 🎉
