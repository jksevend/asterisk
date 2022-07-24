import 'package:asterisk_app/page/authentication/login_page.dart';
import 'package:asterisk_app/service/http/authentication_service.dart';
import 'package:asterisk_app/utility/app_locale.dart';
import 'package:easy_localization/easy_localization.dart';
import 'package:flutter/material.dart';

class SettingsPage extends StatelessWidget {

  const SettingsPage({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    final AuthenticationService authenticationService = AuthenticationService();

    /// Must be ordered like [Gender]. Index 0 is male, 1 female
    return Scaffold(
      appBar: AppBar(
        centerTitle: true,
        title: Text(tr('app_settings_title')),
      ),
      body: ListView(
        shrinkWrap: true,
        children: [
          Card(
            child: ListTile(
              leading: const Icon(Icons.logout_outlined),
              title: Text('Logout'),
              subtitle: Text('Sign off and return to the login page'),
              onTap: () async =>
                  authenticationService.logout().whenComplete(() =>
                      Navigator.of(context).pushAndRemoveUntil(
                          MaterialPageRoute(builder: (context) => LoginPage()), (route) => false)),
            ),
          ),
          Card(
            child: ListTile(
              leading: const Icon(Icons.language),
              subtitle: Text(tr('app_settings_change_locale_subtitle')),
              title: DropdownButton<AppLocale>(
                value: context.locale == const Locale('en', 'US')
                    ? AppLocale.english
                    : AppLocale.german,
                items: AppLocale.values
                    .map(
                      (locale) =>
                      DropdownMenuItem(
                        value: locale,
                        child: Row(
                          children: [
                            CircleAvatar(
                              radius: 15,
                              backgroundColor: Colors.transparent,
                              backgroundImage: AssetImage(locale.localeFlag),
                            ),
                            const SizedBox(
                              width: 8,
                            ),
                            Text(tr(locale.translationKey)),
                          ],
                        ),
                      ),
                )
                    .toList(),
                onChanged: (value) {
                  if (value == AppLocale.german) {
                    context.setLocale(const Locale('de', 'DE'));
                  } else if (value == AppLocale.english) {
                    context.setLocale(const Locale('en', 'US'));
                  }
                },
              ),
            ),
          ),
          Card(
            child: ListTile(
              leading: const Icon(Icons.info_outline_rounded),
              title: Text(tr('app_settings_about_title')),
              subtitle: Text(tr('app_settings_about_subtitle')),
              onTap: () =>
                  showAboutDialog(
                      context: context, applicationName: 'Asterisk', applicationVersion: '0.0.1'),
            ),
          )
        ],
      ),
    );
  }
}
