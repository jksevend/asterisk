import 'package:asterisk_app/page/authentication/login_page.dart';
import 'package:asterisk_app/page/main_page.dart';
import 'package:asterisk_app/service/http/authentication_service.dart';
import 'package:asterisk_app/style/theme.dart';
import 'package:easy_localization/easy_localization.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:logging/logging.dart';
import 'package:provider/provider.dart';

import 'state/settings/settings_provider.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  // Configure logger
  Logger.root.level = Level.ALL;
  Logger.root.onRecord.listen((record) {
    debugPrint('${record.level.name}: ${record.time}: '
        '${record.loggerName}: '
        '${record.message}');
  });

  // Initialize localization
  await EasyLocalization.ensureInitialized();

  runApp(
    EasyLocalization(
      supportedLocales: const [
        Locale('en', 'US'),
        Locale('de', 'DE'),
      ],
      path: 'assets/lang',
      fallbackLocale: const Locale('en', 'US'),
      child: const AsteriskApp(),
    ),
  );
}

class AsteriskApp extends StatelessWidget {
  const AsteriskApp({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return MultiProvider(
      providers: [
        ChangeNotifierProvider<SettingsProvider>(create: (_) => SettingsProvider()),
      ],
      child: Consumer<SettingsProvider>(
        builder: (context, settingsProvider, child) {
          return MaterialApp(
            title: 'Asterisk',
            debugShowCheckedModeBanner: kDebugMode,
            localizationsDelegates: context.localizationDelegates,
            supportedLocales: context.supportedLocales,
            locale: context.locale,
            themeMode: ThemeMode.system,
            theme: AppTheme.light,
            darkTheme: AppTheme.dark,
            home: FutureBuilder<bool>(
              future: AuthenticationService.isJwtExpired(),
              builder: (context, snapshot) {
                if (snapshot.hasData) {
                  final bool expired = snapshot.data ?? true;
                  return expired ? const LoginPage() : const MainPage();
                } else {
                  return const Scaffold(
                    body: Center(child: LinearProgressIndicator(),),
                  );
                }
              },
            ),
          );
        },
      ),
    );
  }
}
