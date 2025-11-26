// lib/services/settings_service.dart
import 'package:shared_preferences/shared_preferences.dart';

class SettingsService {
  static const String _keyNotificationsEnabled = 'notifications_enabled';
  static const String _keyLocationTracking = 'location_tracking';
  static const String _keyAutoLearn = 'auto_learn';

  // 싱글톤 패턴
  static final SettingsService _instance = SettingsService._internal();
  factory SettingsService() => _instance;
  SettingsService._internal();

  SharedPreferences? _prefs;

  // 초기화
  Future<void> init() async {
    _prefs ??= await SharedPreferences.getInstance();
  }

  // 알림 활성화 여부
  Future<bool> getNotificationsEnabled() async {
    await init();
    return _prefs?.getBool(_keyNotificationsEnabled) ?? true;
  }

  Future<void> setNotificationsEnabled(bool value) async {
    await init();
    await _prefs?.setBool(_keyNotificationsEnabled, value);
  }

  // 위치 추적 여부
  Future<bool> getLocationTracking() async {
    await init();
    return _prefs?.getBool(_keyLocationTracking) ?? true;
  }

  Future<void> setLocationTracking(bool value) async {
    await init();
    await _prefs?.setBool(_keyLocationTracking, value);
  }

  // 자동 학습 여부
  Future<bool> getAutoLearn() async {
    await init();
    return _prefs?.getBool(_keyAutoLearn) ?? true;
  }

  Future<void> setAutoLearn(bool value) async {
    await init();
    await _prefs?.setBool(_keyAutoLearn, value);
  }

  // 모든 설정 초기화
  Future<void> resetAll() async {
    await init();
    await _prefs?.clear();
  }
}