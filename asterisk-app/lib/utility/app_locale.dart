/// Helper for all supported locales in this app
enum AppLocale { german, english }

extension GameLocaleExtension on AppLocale {
  String get localeFlag => _localeFlag(this);

  String get translationKey => _translationKey(this);

  /// Returns the path to a flag image of a given [appLocale]
  String _localeFlag(final AppLocale appLocale) {
    switch (appLocale) {
      case AppLocale.german:
        return 'assets/img/germany.png';
      case AppLocale.english:
        return 'assets/img/uk.png';
    }
  }

  /// Translation key for a [appLocale] located in assets/lang/
  String _translationKey(final AppLocale appLocale) {
    switch (appLocale) {
      case AppLocale.german:
        return 'german';
      case AppLocale.english:
        return 'english';
    }
  }
}