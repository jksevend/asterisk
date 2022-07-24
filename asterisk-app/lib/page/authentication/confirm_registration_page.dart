import 'package:asterisk_app/page/authentication/login_page.dart';
import 'package:asterisk_app/widget/loading_button.dart';
import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';

import '../../service/http/authentication_service.dart';

class ConfirmRegistrationPage extends StatefulWidget {
  final String confirmationId;

  const ConfirmRegistrationPage({Key? key, required this.confirmationId}) : super(key: key);

  @override
  State<ConfirmRegistrationPage> createState() => _ConfirmRegistrationPageState();
}

class _ConfirmRegistrationPageState extends State<ConfirmRegistrationPage> {
  final GlobalKey<FormState> _formKey = GlobalKey<FormState>();
  final AuthenticationService authenticationService = AuthenticationService();

  late TextEditingController _codeController;

  @override
  void initState() {
    _codeController = TextEditingController();
    super.initState();
  }

  @override
  void dispose() {
    _codeController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        centerTitle: true,
        title: Text('Confirm registration'),
      ),
      body: Form(
        child: Column(
          children: [
            /// Code input
            TextFormField(
              controller: _codeController,
              maxLength: 11,
              keyboardType: TextInputType.text,
              decoration: InputDecoration(
                hintText: 'XXX-XXX-XXX',
                labelText: 'Confirmation code',
                prefixIcon: Icon(Icons.confirmation_num_outlined)
              ),
              validator: (final String? value) {
                if (value == null ||value.isEmpty) {
                  return 'Confirmation code is required';
                }
                final RegExp regex = RegExp(r'^[a-zA-Z\d]{3}-[a-zA-Z\d]{3}-[a-zA-Z\d]{3}$');
                if (!regex.hasMatch(value)) {
                  return 'Confirmation code must be of type XXX-XXX-XXX';
                }
                return null;
              },
            ),

            /// Submit button
            LoadingButton(
              onTap: () async => _onConfirmRegistration(context),
              child: Text('Submit'),
            )
          ],
        ),
      ),
    );
  }

  void _onConfirmRegistration(final BuildContext context) async {
    if (_formKey.currentState!.validate()) {
      final bool result = await authenticationService.confirmRegistration(widget.confirmationId, _codeController.text);
      if (result) {
        if (!mounted) return;
        Navigator.of(context).pushAndRemoveUntil(MaterialPageRoute(builder: (context) => LoginPage()), (route) => false);
      }
    }
  }
}
