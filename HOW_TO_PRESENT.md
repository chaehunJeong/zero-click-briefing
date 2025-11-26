# 🎬 발표 준비 완료 가이드

**GitHub 저장소**: https://github.com/chaehunJeong/zero-click-briefing

## 📋 3단계로 발표 준비하기

---

## 1️⃣ Marp로 PPT 만들기 (5분)

### VSCode에서 Marp 확장 설치

1. VSCode 실행
2. 확장 탭 (Cmd+Shift+X)
3. "Marp for VS Code" 검색 → 설치
4. `PRESENTATION.md` 파일 열기
5. 우측 상단 "미리보기" 아이콘 클릭

### PDF/PPT 내보내기

**방법 1: Marp 확장으로 PDF 내보내기** (추천)
```
1. PRESENTATION.md 파일 열기
2. Cmd+Shift+P → "Marp: Export slide deck"
3. PDF 선택
4. 저장 위치 선택
```

**방법 2: PowerPoint로 변환**
```
1. PDF를 먼저 내보내기
2. Adobe Acrobat 또는 온라인 변환기 사용
3. PDF → PPT 변환
```

**방법 3: Google Slides 사용**
```
1. Google Slides 열기
2. 파일 → 가져오기 → PRESENTATION.md 내용 복사/붙여넣기
3. 각 슬라이드 수동 정리
```

---

## 2️⃣ 발표 리허설 (30분 × 2회)

### 첫 번째 리허설 (혼자)

```bash
# 타이머 시작
# 10분 타이머 설정

# PRESENTATION.md를 보며 발표 연습
# PRESENTER_NOTES.md를 참고하며 대본 읽기
```

**체크 포인트**:
- [ ] 시간이 10분 이내인가? (9~10분이 적당)
- [ ] 목소리가 떨리지 않는가?
- [ ] 슬라이드 전환이 자연스러운가?
- [ ] 시연 명령어를 외웠는가?

### 두 번째 리허설 (실전처럼)

```bash
# 실제 시연 포함 리허설
cd /Users/chaehunjeong/Documents/dev/api_sero_click
./gradlew bootRun

# 서버 실행 후 명령어 순서대로 실행
curl -X POST "http://localhost:8080/api/patterns/simulate-week?userId=demo_user"
curl "http://localhost:8080/api/briefing/demo_user" | jq
curl "http://localhost:8080/demo/weather-comparison" | jq
```

**체크 포인트**:
- [ ] 서버가 제대로 실행되는가?
- [ ] 모든 API가 정상 응답하는가?
- [ ] Flutter 앱이 실행되는가?
- [ ] iPhone 알림이 오는가?

---

## 3️⃣ 발표 당일 준비 (1시간 전)

### 기술 환경 체크 (30분)

```bash
# 1. Ollama 확인
ollama list
# qwen2.5:3b가 있는지 확인

# 2. 기존 데이터 삭제 (깨끗한 시연을 위해)
curl -X DELETE "http://localhost:8080/api/patterns/all?userId=demo_user"

# 3. Spring Boot 실행
cd /Users/chaehunjeong/Documents/dev/api_sero_click
./gradlew bootRun

# 4. 서버 동작 확인
curl http://localhost:8080/actuator/health

# 5. Flutter 앱 빌드 (iOS 실제 기기)
cd /Users/chaehunjeong/Documents/dev/app_zero_click
flutter run -d <YOUR_DEVICE_ID>
```

### 발표 환경 설정 (15분)

**화면 공유 준비**:
- [ ] 브라우저 탭 정리 (필요한 것만)
- [ ] 터미널 폰트 크기 키우기 (Cmd + +)
- [ ] 터미널 테마 밝게 (프로젝터에서 잘 보이도록)
- [ ] Dock 숨기기 (Cmd+Option+D)
- [ ] 알림 끄기 (방해금지 모드)

**백업 자료 준비**:
- [ ] API 결과 스크린샷 저장
- [ ] 시연 동영상 녹화 (백업용)
- [ ] PDF 파일 USB 저장

### 발표자 준비 (15분)

- [ ] 물 한 잔 준비
- [ ] 심호흡 3회
- [ ] 대본 마지막 리뷰
- [ ] 손목시계 확인 (시간 체크용)

---

## 🎤 발표 시작!

### 순서

1. **자기소개** (10초)
   - 이름, 소속, 프로젝트명

2. **슬라이드 발표** (6분)
   - 슬라이드 1~6: 문제 & 솔루션 & 기술

3. **실시간 시연** (4분) ⭐
   - 터미널 화면 공유
   - 명령어 실행
   - 결과 설명

4. **마무리** (1분)
   - 경쟁 우위 강조
   - "감사합니다!"

5. **Q&A** (시간 허용 시)
   - PRESENTER_NOTES.md의 Q&A 참고

---

## 🚨 돌발 상황 대처

### 시나리오 1: API가 응답 안함

**대처법**:
> "죄송합니다, 네트워크 문제로 제가 미리 준비한 결과를 보여드리겠습니다."

→ 스크린샷 또는 동영상 재생

### 시나리오 2: 시간 초과

**대처법**:
- 슬라이드 10 (사용자 시나리오) 건너뛰기
- 슬라이드 11 (개인정보) 건너뛰기
- 슬라이드 13 (확장 가능성) 건너뛰기
- 바로 결론으로

### 시나리오 3: 질문을 못 알아들음

**대처법**:
> "죄송합니다, 다시 한번 말씀해주시겠어요?"

또는

> "질문이 [반복]인가요? 맞다면 답변드리겠습니다..."

### 시나리오 4: 답변 모를 질문

**대처법**:
> "좋은 질문입니다! 현재 프로토타입 단계라 그 부분은 아직 구현하지 못했습니다. 향후 개선 계획에 추가하겠습니다."

---

## 📱 시연 명령어 치트시트

### 복사해서 터미널에 붙여넣기

```bash
# 1. 1주일 패턴 시뮬레이션
curl -X POST "http://localhost:8080/api/patterns/simulate-week?userId=demo_user"

# 2. AI 브리핑 확인
curl "http://localhost:8080/api/briefing/demo_user" | jq

# 3. 날씨 검증
curl "http://localhost:8080/demo/weather-comparison" | jq

# 4. 패턴 데이터 확인
curl "http://localhost:8080/api/patterns/demo_user" | jq | head -50

# 5. 시스템 정보
curl "http://localhost:8080/demo/info" | jq
```

---

## 🎯 핵심 메시지 (꼭 전달!)

발표 중 이 4가지는 반드시 강조하세요:

1. **트렌드 코리아 2026** ⭐ 새로 추가!
   > "제로클릭 시대 트렌드를 개인 생활에 적용했습니다!"

2. **제로 클릭** ⭐
   > "사용자는 설정도, 입력도, 클릭도 필요 없습니다!"

3. **제로 비용** ⭐
   > "외부 API 없이 로컬 LLM만 사용해서 월 0원입니다!"

4. **3일 학습** ⭐
   > "단 3일만 출근하면 패턴 학습이 완료됩니다!"

---

## 📊 성공 체크리스트

발표 후 자가 평가:

- [ ] 시간을 10분 이내로 지켰는가?
- [ ] 핵심 메시지 3가지를 강조했는가?
- [ ] 실시간 시연이 성공했는가?
- [ ] 청중과 눈 맞춤을 했는가?
- [ ] 질문에 당황하지 않았는가?
- [ ] 자신감 있게 마무리했는가?

---

## 🎉 발표 완료 후

1. **피드백 수집**
   - 심사위원 코멘트 메모
   - 동료 의견 청취

2. **개선사항 정리**
   - 부족했던 부분 기록
   - 다음에 개선할 점

3. **결과 공유**
   - GitHub에 프로젝트 공개
   - 블로그에 후기 작성

---

## 🚀 마지막 조언

**"완벽한 발표는 없습니다. 자신감 있는 발표가 최고입니다!"**

- 실수해도 괜찮습니다. 자연스럽게 넘어가세요.
- 긴장되면 심호흡 3회.
- 청중은 당신의 성공을 응원합니다.
- 3일간 고생한 당신의 프로젝트를 자랑스럽게 보여주세요!

**화이팅! 🎉🎉🎉**

---

## 📞 긴급 연락처

**기술 문제 발생 시:**
- Spring Boot 재시작: `./gradlew bootRun`
- Ollama 재시작: `ollama serve`
- Flutter 재빌드: `flutter clean && flutter run`

**발표 중 멘붕 시:**
- 5초 침묵 후 "죄송합니다, 다시 시작하겠습니다"
- PRESENTER_NOTES.md 참고
- 최악의 경우: 슬라이드만으로 구두 설명

**힘내세요! 여러분의 Zero-Click은 충분히 훌륭합니다! 🌟**
