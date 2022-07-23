import 'package:flutter/foundation.dart';

import 'base_values.dart';

/// A generator function which returns [T]
typedef ClassCreator<T> = T Function();

/// A base class for all providers used throughout this application
///
/// For each provider created the type of an [BaseValues] implementation needs to be passed
abstract class BaseProvider<V extends BaseValues> with ChangeNotifier {
  /// Underlying class which manages state values.
  late V _values;

  /// Helper to create an instance of [V]
  final ClassCreator<V> _valueCreator;

  /// Initialize this provider. Stores an instance of [V] in [_values]
  /// and loads all necessary data
  BaseProvider(this._valueCreator) {
    _values = _valueCreator();
    _values.load();
  }

  /// Expose the instance of [V]
  V get values => _values;
}