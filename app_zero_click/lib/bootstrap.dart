import 'package:flutter/foundation.dart' show kIsWeb;
import 'package:flutter/material.dart';
import 'package:flutter_local_notifications/flutter_local_notifications.dart';
import 'package:timezone/data/latest_all.dart' as tz;
import 'package:timezone/timezone.dart' as tz;
import 'package:workmanager/workmanager.dart';
import 'location_retrieval_service.dart';
import 'api_service_handlers.dart';

class Bootstrap {
  static final _noti = FlutterLocalNotificationsPlugin();

  static Future<void> init() async {
    WidgetsFlutterBinding.ensureInitialized();

    // 웹에서는 백그라운드 기능 건너뛰기
    if (kIsWeb) {
      print('웹 플랫폼: 백그라운드 기능 비활성화');
      return;
    }

    // 한국 시간대 설정
    tz.initializeTimeZones();
    tz.setLocalLocation(tz.getLocation('Asia/Seoul'));

    // 로컬 알림 초기화
    await _noti.initialize(
      const InitializationSettings(
        android: AndroidInitializationSettings('@mipmap/ic_launcher'),
        iOS: DarwinInitializationSettings(
          requestAlertPermission: true,
          requestBadgePermission: true,
          requestSoundPermission: true,
        ),
        macOS: DarwinInitializationSettings(
          requestAlertPermission: true,
          requestBadgePermission: true,
          requestSoundPermission: true,
        ),
      ),
    );

    // 🔸 권한 요청(확실히)
    await _noti
    .resolvePlatformSpecificImplementation<IOSFlutterLocalNotificationsPlugin>()
    ?.requestPermissions(alert: true, badge: true, sound: true);

    // 백그라운드 워커 초기화
    Workmanager().initialize(_callback, isInDebugMode: false);

    // 15분마다 위치 수집 (집에서 나가는 시간 정확히 감지)
    // Android: 최소 15분, iOS: Background App Refresh 사용
    await Workmanager().registerPeriodicTask(
      'learn',
      'learn_task',
      frequency: const Duration(minutes: 15),  // 🔄 15분마다 (3시간 → 15분)
      existingWorkPolicy: ExistingPeriodicWorkPolicy.replace,  // 기존 작업 대체
      constraints: Constraints(
        networkType: NetworkType.connected,  // 네트워크 연결 시에만
      ),
    );
  }

  // 백그라운드에서 실행되는 콜백
  @pragma('vm:entry-point')
  static void _callback() {
    Workmanager().executeTask((task, input) async {
      try {
        // 1. 현재 위치 가져오기
        final position = await LocationService.getCurrentLocation();
        if (position == null) return true;
        
        // 2. 사용자 활동 패턴 데이터 생성
        final patternData = {
          'timestamp': DateTime.now().toIso8601String(),
          'latitude': position.latitude,
          'longitude': position.longitude,
          'hour': DateTime.now().hour,
          'dayOfWeek': DateTime.now().weekday,
        };
        
        // 3. Spring Boot 서버로 전송
        await ApiService.sendUserPattern(patternData);
        
        // 4. AI 분석 결과 받아오기
        final shouldNotify = await _checkIfShouldNotify();
        
        // 5. 알림이 필요한 시점이면 브리핑 전송
        if (shouldNotify) {
          await _sendSmartBriefing(position.latitude, position.longitude);
        }
      } catch (e) {
        print('백그라운드 작업 오류: $e');
      }
      return true;
    });
  }
  
  // AI 기반으로 알림 시점 판단
  static Future<bool> _checkIfShouldNotify() async {
    try {
      final briefing = await ApiService.getBriefing('user_id_here');
      return briefing['shouldNotify'] ?? false;
    } catch (e) {
      print('알림 판단 오류: $e');
      return false;
    }
  }
  
  // 스마트 브리핑 전송
  static Future<void> _sendSmartBriefing(double lat, double lon) async {
    try {
      final weather = await ApiService.getWeather(lat, lon);
      final traffic = await ApiService.getTrafficInfo(lat, lon);

      await _noti.show(
        DateTime.now().millisecondsSinceEpoch % 100000,
        '🌅 ${weather['condition']} ${weather['temperature']}℃',
        '🚗 ${traffic['carDuration']}분 (${traffic['carCongestion']}) · 🚇 ${traffic['transitDuration']}분 (환승 ${traffic['transfers']}회)',
        const NotificationDetails(
          android: AndroidNotificationDetails('daily', 'Daily'),
          iOS: DarwinNotificationDetails(
            presentAlert: true,
            presentSound: true,
            presentBadge: true,
          ),
          macOS: DarwinNotificationDetails(
            presentAlert: true,
            presentSound: true,
            presentBadge: true,
          ),
        ),
      );
    } catch (e) {
      print('브리핑 전송 오류: $e');
    }
  }

  // 지금 바로 눈으로 확인할 데모 알림 (5초 후에 푸시)
  static Future<void> demoMorningNotification() async {
    // 웹에서는 알림 건너뛰기
    if (kIsWeb) {
      print('웹 플랫폼: 알림 기능 비활성화');
      return;
    }

    await Future.delayed(const Duration(seconds: 5));

    await _noti.show(
      DateTime.now().millisecondsSinceEpoch % 100000, // id 겹침 방지
      '🌅 아침 브리핑',
      '체감 21℃ · 출근 42분 · 9:00 팀미팅',
      const NotificationDetails(
        android: AndroidNotificationDetails('daily', 'Daily'),
        iOS: DarwinNotificationDetails(
          presentAlert: true,    // 🔸 앱이 켜져 있어도 배너/알럿 허용
          presentSound: true,
          presentBadge: true,
        ),
      ),
    );
  }
}