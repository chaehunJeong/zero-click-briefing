import 'package:geolocator/geolocator.dart';
import 'package:geocoding/geocoding.dart';
import 'dart:async';

class LocationService {
  /// GPS 상태 확인 및 현재 위치 가져오기 (개선된 버전)
  static Future<Position?> getCurrentLocation({bool highAccuracy = true}) async {
    print('=== GPS 위치 가져오기 시작 ===');

    // 1. 위치 서비스 활성화 확인
    bool serviceEnabled = await Geolocator.isLocationServiceEnabled();
    if (!serviceEnabled) {
      print('❌ 위치 서비스가 비활성화되어 있습니다.');
      print('   설정 > 위치 > 위치 서비스를 켜주세요.');
      return null;
    }
    print('✅ 위치 서비스 활성화됨');

    // 2. 위치 권한 확인 및 요청
    LocationPermission permission = await Geolocator.checkPermission();
    print('현재 권한 상태: $permission');

    if (permission == LocationPermission.denied) {
      print('위치 권한 요청 중...');
      permission = await Geolocator.requestPermission();
      if (permission == LocationPermission.denied) {
        print('❌ 위치 권한이 거부되었습니다.');
        return null;
      }
    }

    if (permission == LocationPermission.deniedForever) {
      print('❌ 위치 권한이 영구적으로 거부되었습니다.');
      print('   설정 > 앱 > 권한에서 위치 권한을 허용해주세요.');
      return null;
    }
    print('✅ 위치 권한 허용됨');

    try {
      // 3. GPS 위치 가져오기
      print('GPS 위치 조회 중...');

      final LocationSettings locationSettings = LocationSettings(
        accuracy: highAccuracy ? LocationAccuracy.best : LocationAccuracy.high,
        distanceFilter: 0,
        timeLimit: const Duration(seconds: 30), // 타임아웃 30초
      );

      final position = await Geolocator.getCurrentPosition(
        locationSettings: locationSettings,
      );

      // 4. GPS 정보 출력
      print('✅ GPS 위치 획득 성공!');
      print('   위도: ${position.latitude}');
      print('   경도: ${position.longitude}');
      print('   정확도: ${position.accuracy.toStringAsFixed(1)}m');
      print('   고도: ${position.altitude}m');
      print('   속도: ${position.speed}m/s');
      print('   시간: ${position.timestamp}');

      // 정확도 경고
      if (position.accuracy > 100) {
        print('⚠️  GPS 정확도가 낮습니다 (${position.accuracy.toStringAsFixed(1)}m)');
        print('   야외에서 하늘이 보이는 곳으로 이동하면 더 정확합니다.');
      } else if (position.accuracy > 50) {
        print('⚠️  GPS 정확도가 보통입니다 (${position.accuracy.toStringAsFixed(1)}m)');
      } else {
        print('✅ GPS 정확도 우수 (${position.accuracy.toStringAsFixed(1)}m)');
      }

      print('=== GPS 위치 가져오기 완료 ===\n');
      return position;

    } catch (e) {
      print('❌ GPS 위치 가져오기 실패: $e');

      if (e.toString().contains('TIMEOUT')) {
        print('   타임아웃: GPS 신호를 찾을 수 없습니다.');
        print('   - 실내에서는 GPS 신호가 약할 수 있습니다.');
        print('   - 창가나 야외로 이동해보세요.');
      }

      return null;
    }
  }

  /// 실시간 위치 스트림 (백그라운드 추적용)
  static Stream<Position> getPositionStream({
    LocationAccuracy accuracy = LocationAccuracy.high,
    int distanceFilter = 10, // 10m 이동시마다 업데이트
  }) {
    print('실시간 위치 추적 시작 (거리 필터: ${distanceFilter}m)');

    final LocationSettings locationSettings = LocationSettings(
      accuracy: accuracy,
      distanceFilter: distanceFilter,
    );

    return Geolocator.getPositionStream(locationSettings: locationSettings);
  }

  /// 위도/경도를 주소로 변환 (Reverse Geocoding)
  static Future<String?> getAddressFromCoordinates(double latitude, double longitude) async {
    try {
      List<Placemark> placemarks = await placemarkFromCoordinates(latitude, longitude);

      if (placemarks.isEmpty) return null;

      Placemark place = placemarks[0];

      // 한국어 주소 포맷: 시/도 + 구/군 + 동/면
      List<String> addressParts = [];

      // 국가가 한국인 경우
      if (place.isoCountryCode == 'KR') {
        if (place.administrativeArea != null) {
          addressParts.add(place.administrativeArea!); // 시/도
        }
        if (place.subAdministrativeArea != null && place.subAdministrativeArea != place.administrativeArea) {
          addressParts.add(place.subAdministrativeArea!); // 구/군
        }
        if (place.locality != null && place.locality != place.subAdministrativeArea) {
          addressParts.add(place.locality!); // 시/구
        }
        if (place.subLocality != null) {
          addressParts.add(place.subLocality!); // 동/면
        }
      } else {
        // 해외 주소 포맷
        if (place.locality != null) {
          addressParts.add(place.locality!); // 도시
        }
        if (place.subAdministrativeArea != null && place.subAdministrativeArea != place.locality) {
          addressParts.add(place.subAdministrativeArea!); // 지역
        }
        if (place.administrativeArea != null) {
          addressParts.add(place.administrativeArea!); // 주/도
        }
        if (place.country != null) {
          addressParts.add(place.country!); // 국가
        }
      }

      return addressParts.isNotEmpty ? addressParts.join(' ') : null;
    } catch (e) {
      print('Reverse geocoding error: $e');
      return null;
    }
  }
}
