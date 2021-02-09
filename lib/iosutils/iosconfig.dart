
import 'package:location_tracking/location_tracking_plugin.dart';

/// iOS specific options for location request.
///
/// Documentation: <https://developer.apple.com/documentation/corelocation/cllocationmanager>
class IOSConfig {
  const IOSConfig({
    this.showsBackgroundLocationIndicator = false,
    this.activityType = LocationAccuracy.NAVIGATION,
  });

  final bool showsBackgroundLocationIndicator;
  final LocationAccuracy activityType;

  Map toJson() => {
    'showsBackgroundLocationIndicator': showsBackgroundLocationIndicator,
   // 'activityType': _Codec.encodeEnum(activityType)
  };

  toMap() => {
    'showsBackgroundLocationIndicator': showsBackgroundLocationIndicator,
    'activityType': activityType.toString().split('.').last
  };
}
