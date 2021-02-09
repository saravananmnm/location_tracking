
import 'dart:async';

import 'package:flutter/services.dart';

class LocationTracking {
  static const MethodChannel _channel =
      const MethodChannel('location_tracking');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }
}
