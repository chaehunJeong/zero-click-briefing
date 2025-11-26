// lib/services/api_service.dart
import 'package:http/http.dart' as http;
import 'dart:convert';

class ApiService {
  static const String baseUrl = 'http://localhost:8080';
  
  // 날씨 정보 가져오기
  static Future<Map<String, dynamic>> getWeather(double lat, double lon) async {
    final response = await http.get(
      Uri.parse('$baseUrl/api/weather?lat=$lat&lon=$lon'),
    );
    return json.decode(response.body);
  }
  
  // 도로 환경 정보 가져오기
  static Future<Map<String, dynamic>> getTrafficInfo(double lat, double lon) async {
    final response = await http.get(
      Uri.parse('$baseUrl/api/traffic?lat=$lat&lon=$lon'),
    );
    return json.decode(response.body);
  }
  
  // 사용자 패턴 학습 데이터 전송
  static Future<void> sendUserPattern(Map<String, dynamic> patternData) async {
    await http.post(
      Uri.parse('$baseUrl/api/patterns'),
      headers: {'Content-Type': 'application/json'},
      body: json.encode(patternData),
    );
  }
  
  // AI 추천 브리핑 받기
  static Future<Map<String, dynamic>> getBriefing(String userId) async {
    final response = await http.get(
      Uri.parse('$baseUrl/api/briefing/$userId'),
    );
    return json.decode(response.body);
  }

  // 패턴 기록 조회
  static Future<List<Map<String, dynamic>>> getPatternHistory(String userId) async {
    final response = await http.get(
      Uri.parse('$baseUrl/api/patterns/$userId'),
    );

    if (response.statusCode == 200) {
      final List<dynamic> data = json.decode(response.body);
      return data.cast<Map<String, dynamic>>();
    } else {
      throw Exception('Failed to load pattern history');
    }
  }

  // LLM 텍스트 리라이팅
  static Future<Map<String, dynamic>> rewriteText(String text) async {
    final response = await http.post(
      Uri.parse('$baseUrl/llm/rewrite'),
      headers: {'Content-Type': 'application/json'},
      body: json.encode({'text': text}),
    );

    if (response.statusCode == 200) {
      return json.decode(response.body);
    } else {
      throw Exception('Failed to rewrite text');
    }
  }

  // 위도/경도를 주소로 변환 (LLM 기반 Geocoding)
  static Future<String?> getAddress(double lat, double lon) async {
    try {
      final response = await http.get(
        Uri.parse('$baseUrl/api/geocoding?lat=$lat&lon=$lon'),
      );

      if (response.statusCode == 200) {
        final Map<String, dynamic> data = json.decode(response.body);
        return data['address'];
      } else {
        return null;
      }
    } catch (e) {
      print('Geocoding API error: $e');
      return null;
    }
  }

  // 패턴 학습 진행률 조회
  static Future<Map<String, dynamic>> getPatternProgress(String userId) async {
    try {
      final patternsResponse = await http.get(
        Uri.parse('$baseUrl/api/patterns/$userId'),
      );

      if (patternsResponse.statusCode == 200) {
        final List<dynamic> patterns = json.decode(patternsResponse.body);

        // 진행률 계산
        int totalCount = patterns.length;
        int nightCount = patterns.where((p) => p['hour'] >= 22 || p['hour'] <= 6).length;
        int morningCount = patterns.where((p) => p['hour'] >= 6 && p['hour'] <= 10).length;

        // 최소 요구사항
        const int minTotal = 10;
        const int minNight = 3;
        const int minMorning = 3;

        double progress = (
          (totalCount / minTotal).clamp(0.0, 1.0) * 0.5 +
          (nightCount / minNight).clamp(0.0, 1.0) * 0.25 +
          (morningCount / minMorning).clamp(0.0, 1.0) * 0.25
        ) * 100;

        return {
          'progress': progress.clamp(0.0, 100.0),
          'total': totalCount,
          'night': nightCount,
          'morning': morningCount,
          'ready': totalCount >= minTotal && nightCount >= minNight && morningCount >= minMorning,
          'message': _getProgressMessage(progress),
        };
      }
    } catch (e) {
      print('Pattern progress error: $e');
    }

    return {
      'progress': 0.0,
      'total': 0,
      'night': 0,
      'morning': 0,
      'ready': false,
      'message': '패턴 학습을 시작하세요',
    };
  }

  static String _getProgressMessage(double progress) {
    if (progress >= 100) return '학습 완료! 알림을 받을 준비가 되었습니다';
    if (progress >= 75) return '거의 다 왔어요! 조금만 더';
    if (progress >= 50) return '절반 완료! 계속 진행 중';
    if (progress >= 25) return '학습 시작! 좋은 시작입니다';
    return '데이터 수집 중...';
  }
}
