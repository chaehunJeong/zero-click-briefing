import 'package:flutter/material.dart';
import '../services/settings_service.dart';

class SettingsScreen extends StatefulWidget {
  const SettingsScreen({super.key});

  @override
  State<SettingsScreen> createState() => _SettingsScreenState();
}

class _SettingsScreenState extends State<SettingsScreen> {
  final SettingsService _settingsService = SettingsService();
  bool _notificationsEnabled = true;
  bool _locationTracking = true;
  bool _autoLearn = true;
  bool _isLoading = true;

  @override
  void initState() {
    super.initState();
    _loadSettings();
  }

  Future<void> _loadSettings() async {
    setState(() => _isLoading = true);

    try {
      final notificationsEnabled = await _settingsService.getNotificationsEnabled();
      final locationTracking = await _settingsService.getLocationTracking();
      final autoLearn = await _settingsService.getAutoLearn();

      setState(() {
        _notificationsEnabled = notificationsEnabled;
        _locationTracking = locationTracking;
        _autoLearn = autoLearn;
        _isLoading = false;
      });
    } catch (e) {
      setState(() => _isLoading = false);
    }
  }

  Future<void> _updateNotifications(bool value) async {
    await _settingsService.setNotificationsEnabled(value);
    setState(() => _notificationsEnabled = value);

    if (mounted) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text(value ? '알림이 활성화되었습니다' : '알림이 비활성화되었습니다')),
      );
    }
  }

  Future<void> _updateLocationTracking(bool value) async {
    await _settingsService.setLocationTracking(value);
    setState(() => _locationTracking = value);

    if (mounted) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text(value ? '위치 추적이 활성화되었습니다' : '위치 추적이 비활성화되었습니다')),
      );
    }
  }

  Future<void> _updateAutoLearn(bool value) async {
    await _settingsService.setAutoLearn(value);
    setState(() => _autoLearn = value);

    if (mounted) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text(value ? '자동 학습이 활성화되었습니다' : '자동 학습이 비활성화되었습니다')),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('설정'),
      ),
      body: _isLoading
          ? const Center(child: CircularProgressIndicator())
          : ListView(
        children: [
          const Padding(
            padding: EdgeInsets.all(16.0),
            child: Text(
              '알림 설정',
              style: TextStyle(
                fontSize: 18,
                fontWeight: FontWeight.bold,
              ),
            ),
          ),
          SwitchListTile(
            title: const Text('알림 활성화'),
            subtitle: const Text('브리핑 알림을 받습니다'),
            value: _notificationsEnabled,
            onChanged: _updateNotifications,
          ),
          const Divider(),
          const Padding(
            padding: EdgeInsets.all(16.0),
            child: Text(
              '데이터 수집',
              style: TextStyle(
                fontSize: 18,
                fontWeight: FontWeight.bold,
              ),
            ),
          ),
          SwitchListTile(
            title: const Text('위치 추적'),
            subtitle: const Text('패턴 학습을 위해 위치를 기록합니다'),
            value: _locationTracking,
            onChanged: _updateLocationTracking,
          ),
          SwitchListTile(
            title: const Text('자동 학습'),
            subtitle: const Text('백그라운드에서 패턴을 자동으로 학습합니다'),
            value: _autoLearn,
            onChanged: _updateAutoLearn,
          ),
          const Divider(),
          const Padding(
            padding: EdgeInsets.all(16.0),
            child: Text(
              '정보',
              style: TextStyle(
                fontSize: 18,
                fontWeight: FontWeight.bold,
              ),
            ),
          ),
          ListTile(
            leading: const Icon(Icons.info),
            title: const Text('앱 버전'),
            subtitle: const Text('1.0.0'),
          ),
          ListTile(
            leading: const Icon(Icons.privacy_tip),
            title: const Text('개인정보 처리방침'),
            trailing: const Icon(Icons.chevron_right),
            onTap: () {},
          ),
        ],
      ),
    );
  }
}