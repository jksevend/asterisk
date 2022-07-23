import 'package:asterisk_app/page/settings_page.dart';
import 'package:flutter/material.dart';

class MainPage extends StatelessWidget {
  const MainPage({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        centerTitle: true,
        title: const Text('Main page'),
        actions: [
          IconButton(
            onPressed: () =>
                Navigator.of(context).push(MaterialPageRoute(builder: (context) => SettingsPage())),
            icon: Icon(Icons.settings),
          )
        ],
      ),
      body: const Center(
        child: Text('Hello world!'),
      ),
    );
  }
}
