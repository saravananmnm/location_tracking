import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:location_tracking/location_tracking.dart';

void main() {
  const MethodChannel channel = MethodChannel('location_tracking');

  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
    expect(await LocationTracking.platformVersion, '42');
  });
}
