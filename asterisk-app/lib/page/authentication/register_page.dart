import 'package:asterisk_app/page/authentication/confirm_registration_page.dart';
import 'package:asterisk_app/service/http/authentication_service.dart';
import 'package:asterisk_app/widget/loading_button.dart';
import 'package:asterisk_app/widget/password_field.dart';
import 'package:flutter/material.dart';

class RegisterPage extends StatefulWidget {
  const RegisterPage({Key? key}) : super(key: key);

  @override
  State<RegisterPage> createState() => _RegisterPageState();
}

class _RegisterPageState extends State<RegisterPage> {
  final GlobalKey<FormState> _formKey = GlobalKey<FormState>();
  final AuthenticationService _authenticationService = AuthenticationService();

  late TextEditingController _firstNameController;
  late TextEditingController _lastNameController;
  late TextEditingController _usernameController;
  late TextEditingController _emailController;
  late TextEditingController _passwordController;

  @override
  void initState() {
    _firstNameController = TextEditingController();
    _lastNameController = TextEditingController();
    _usernameController = TextEditingController();
    _emailController = TextEditingController();
    _passwordController = TextEditingController();
    super.initState();
  }

  @override
  void dispose() {
    _firstNameController.dispose();
    _lastNameController.dispose();
    _usernameController.dispose();
    _emailController.dispose();
    _passwordController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        centerTitle: true,
        title: Text('Register'),
      ),
      body: Form(
        key: _formKey,
        child: ListView(
          shrinkWrap: true,
          children: [
            /// First name input
            TextFormField(
              controller: _firstNameController,
              maxLength: 15,
              keyboardType: TextInputType.text,
              decoration: const InputDecoration(
                prefixIcon: Icon(Icons.person),
                labelText: 'First name',
                hintText: 'John',
              ),
              validator: (final String? value) {
                if (value == null || value.isEmpty) {
                  return 'First name is required';
                }
                return null;
              },
            ),
            /// Last name input
            TextFormField(
              controller: _lastNameController,
              maxLength: 15,
              keyboardType: TextInputType.text,
              decoration: const InputDecoration(
                prefixIcon: Icon(Icons.person),
                labelText: 'Last name',
                hintText: 'Smith',
              ),
              validator: (final String? value) {
                if (value == null || value.isEmpty) {
                  return 'Last name is required';
                }
                return null;
              },
            ),
            /// Username input
            TextFormField(
              controller: _usernameController,
              maxLength: 25,
              keyboardType: TextInputType.text,
              decoration: const InputDecoration(
                prefixIcon: Icon(Icons.account_box),
                labelText: 'Username',
                hintText: 'jsmith',
              ),
              validator: (final String? value) {
                if (value == null || value.isEmpty) {
                  return 'Username is required';
                }
                return null;
              },
            ),
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

            /// Register button
            LoadingButton(
              onTap: () async => _onRegister(context),
              child: Text('Register'),
            )
          ],
        ),
      ),
    );
  }

  void _onRegister(BuildContext context) async {
    if (_formKey.currentState!.validate()) {
      final String? confirmationId = await _authenticationService.register(
          _firstNameController.text,
          _lastNameController.text,
          _usernameController.text,
          _emailController.text,
          _passwordController.text);

      if (confirmationId != null) {
        if (!mounted) return;
        Navigator.of(context).pushAndRemoveUntil(
            MaterialPageRoute(
                builder: (context) => ConfirmRegistrationPage(confirmationId: confirmationId)),
            (route) => false);
      }
    }
  }
}
