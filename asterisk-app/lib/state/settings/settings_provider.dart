import '../base_provider.dart';
import 'settings_values.dart';

/// Manager to load and expose [SettingsValues]
class SettingsProvider extends BaseProvider<SettingsValues> {
  SettingsProvider() : super(() => SettingsValues());
}