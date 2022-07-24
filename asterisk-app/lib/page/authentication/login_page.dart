import 'package:asterisk_app/page/authentication/forgot_password_page.dart';
import 'package:asterisk_app/page/main_page.dart';
import 'package:asterisk_app/service/http/authentication_service.dart';
import 'package:asterisk_app/widget/loading_button.dart';
import 'package:asterisk_app/widget/password_field.dart';
import 'package:flutter/gestures.dart';
import 'package:flutter/material.dart';

import 'register_page.dart';

class LoginPage extends StatefulWidget {
  const LoginPage({Key? key}) : super(key: key);

  @override
  State<LoginPage> createState() => _LoginPageState();
}

class _LoginPageState extends State<LoginPage> {
  final GlobalKey<FormState> _formKey = GlobalKey<FormState>();

  late TextEditingController _emailController;
  late TextEditingController _passwordController;

  final AuthenticationService authenticationService = AuthenticationService();

  @override
  void initState() {
    _emailController = TextEditingController();
    _passwordController = TextEditingController();
    super.initState();
  }

  @override
  void dispose() {
    _emailController.dispose();
    _passwordController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        centerTitle: true,
        title: const Text('Login'),
      ),
      body: Form(
        key: _formKey,
        child: Column(
          children: [
            /// Email input
            TextFormField(
              controller: _emailController,
              maxLength: 255,
              keyboardType: TextInputType.emailAddress,
              decoration: const InputDecoration(
                prefixIcon: Icon(Icons.email_outlined),
                labelText: 'Email',
                hintText: 'me@mail.com',
              ),
              validator: (final String? value) {
                if (value == null || value.isEmpty) {
                  return 'Email is required';
                }
                return null;
              },
            ),

            /// Password input
            PasswordField(passwordController: _passwordController),

            /// Forgot password
            RichText(
              text: TextSpan(
                text: 'Forgot password?',
                style: const TextStyle(color: Colors.blue),
                recognizer: TapGestureRecognizer()
                  ..onTap = () => Navigator.of(context)
                      .push(MaterialPageRoute(builder: (context) => const ForgotPasswordPage())),
              ),
            ),

            /// Login button
            LoadingButton(
              onTap: () async => _onLogin(context),
              child: const Text('Login'),
            ),

            /// Register
            RichText(
              text: TextSpan(text: 'Don\'t have an account? ', children: [
                TextSpan(
                    text: 'Sign up',
                    style: const TextStyle(color: Colors.blue),
                    recognizer: TapGestureRecognizer()
                      ..onTap = () => Navigator.of(context)
                          .push(MaterialPageRoute(builder: (context) => const RegisterPage())))
              ]),
            ),
          ],
        ),
      ),
    );
  }

  void _onLogin(final BuildContext context) async {
    if (_formKey.currentState!.validate()) {
      final bool result = await authenticationService.login(_emailController.text, _passwordController.text);
      if (result) {
        Navigator.of(context).pushAndRemoveUntil(MaterialPageRoute(builder: (context) => const MainPage()), (route) => false);
      }
    }
  }
}
