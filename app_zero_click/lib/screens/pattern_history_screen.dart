import 'package:flutter/material.dart';
import '../api_service_handlers.dart';
import 'package:intl/intl.dart';

class PatternHistoryScreen extends StatefulWidget {
  const PatternHistoryScreen({super.key});

  @override
  State<PatternHistoryScreen> createState() => _PatternHistoryScreenState();
}

class _PatternHistoryScreenState extends State<PatternHistoryScreen> {
  bool _isLoading = true;
  List<Map<String, dynamic>> _patterns = [];
  String? _errorMessage;

  @override
  void initState() {
    super.initState();
    _loadPatterns();
  }

  Future<void> _loadPatterns() async {
    setState(() {
      _isLoading = true;
      _errorMessage = null;
    });

    try {
      // TODO: 실제 userId로 교체 (로그인 시스템 구현 후)
      final patterns = await ApiService.getPatternHistory('test_user');
      setState(() {
        _patterns = patterns;
        _isLoading = false;
      });
    } catch (e) {
      setState(() {
        _errorMessage = '패턴 기록을 불러오는데 실패했습니다: $e';
        _isLoading = false;
      });
    }
  }

  String _formatDateTime(String? dateTimeStr) {
    if (dateTimeStr == null) return '시간 정보 없음';

    try {
      final dateTime = DateTime.parse(dateTimeStr);
      return DateFormat('yyyy-MM-dd HH:mm').format(dateTime);
    } catch (e) {
      return dateTimeStr;
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('패턴 기록'),
        actions: [
          IconButton(
            icon: const Icon(Icons.refresh),
            onPressed: _loadPatterns,
          ),
        ],
      ),
      body: _isLoading
          ? const Center(child: CircularProgressIndicator())
          : _errorMessage != null
              ? Center(
                  child: Column(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      const Icon(Icons.error_outline, size: 64, color: Colors.red),
                      const SizedBox(height: 16),
                      Text(
                        _errorMessage!,
                        textAlign: TextAlign.center,
                        style: const TextStyle(color: Colors.red),
                      ),
                      const SizedBox(height: 16),
                      ElevatedButton.icon(
                        onPressed: _loadPatterns,
                        icon: const Icon(Icons.refresh),
                        label: const Text('다시 시도'),
                      ),
                    ],
                  ),
                )
              : _patterns.isEmpty
                  ? const Center(
                      child: Column(
                        mainAxisAlignment: MainAxisAlignment.center,
                        children: [
                          Icon(Icons.history, size: 64, color: Colors.grey),
                          SizedBox(height: 16),
                          Text(
                            '패턴 기록이 없습니다',
                            style: TextStyle(fontSize: 16, color: Colors.grey),
                          ),
                        ],
                      ),
                    )
                  : RefreshIndicator(
                      onRefresh: _loadPatterns,
                      child: ListView.builder(
                        padding: const EdgeInsets.all(16),
                        itemCount: _patterns.length,
                        itemBuilder: (context, index) {
                          final pattern = _patterns[index];
                          return Card(
                            margin: const EdgeInsets.only(bottom: 12),
                            child: ListTile(
                              leading: CircleAvatar(
                                backgroundColor: Colors.blue.withOpacity(0.1),
                                child: Text(
                                  '${index + 1}',
                                  style: const TextStyle(
                                    color: Colors.blue,
                                    fontWeight: FontWeight.bold,
                                  ),
                                ),
                              ),
                              title: Text(_formatDateTime(pattern['timestamp'])),
                              subtitle: Text(pattern['patternType'] ?? '패턴 정보 없음'),
                              trailing: const Icon(Icons.chevron_right),
                              onTap: () {
                                _showPatternDetail(pattern);
                              },
                            ),
                          );
                        },
                      ),
                    ),
    );
  }

  void _showPatternDetail(Map<String, dynamic> pattern) {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('패턴 상세 정보'),
        content: SingleChildScrollView(
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            mainAxisSize: MainAxisSize.min,
            children: [
              _buildDetailRow('시간', _formatDateTime(pattern['timestamp'])),
              const SizedBox(height: 8),
              _buildDetailRow('패턴 타입', pattern['patternType'] ?? '정보 없음'),
              const SizedBox(height: 8),
              _buildDetailRow('위치', '${pattern['latitude']}, ${pattern['longitude']}'),
              const SizedBox(height: 8),
              _buildDetailRow('활동', pattern['activity'] ?? '정보 없음'),
              const SizedBox(height: 8),
              _buildDetailRow('신뢰도', '${pattern['confidence'] ?? 0}%'),
            ],
          ),
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('닫기'),
          ),
        ],
      ),
    );
  }

  Widget _buildDetailRow(String label, String value) {
    return Row(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        SizedBox(
          width: 80,
          child: Text(
            label,
            style: const TextStyle(
              fontWeight: FontWeight.bold,
              color: Colors.grey,
            ),
          ),
        ),
        Expanded(
          child: Text(value),
        ),
      ],
    );
  }
}