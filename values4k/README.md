# Values4K

Lightweight, validatable base Value types - aka Microtypes - aka Tinytypes

## Motivation & Concepts
Subvert primitive obsession and provide type safety and other facilities for JVM programs.

### Typesafety

The problem which we are trying to solve is to avoid illegal values entering into our system. For this, it is best to use strongly typed values, which allow us to both lean on the compiler and improve the developer experience by engaging with IDE tooling.

For example, take this simple function:
```kotlin
fun transferMoneyTo(amount: Int, accountNumber: String, sortCode: String)
```

The first problem here is that `accountNumber` and `sortCode` fields are both of type `String`, meaning that a coder could accidentally switch these values around and we would not potentially  notice until runtime.

The base type provided by this lib is `ValueType`, which is just a simple wrapper around a `value` field and can be used for defining your own domain types:

```kotlin
class Money(value: Int): Value<Int>(value)
class SortCode(value: String): Value<String>(value)
class AccountNumber(value: String): Value<String>(value)

fun transferMoneyTo(amount: Money, accountNumber: AccountNumber, sortCode: SortCode)
```

### Validation
The next problem is that there is no domain validation on our values. What if someone passed in a negative amount? Or an `accountNumber` containing letters instead of digits?

We can fix that by passing in `validations` to ensure we can never create an illegal value. Illegal values will blow up on construction (at the entry point to our system) instead of deep inside our domain logic:

```kotlin
class Money(value: Int): Value<Int>(value, 1.minValue)
class SortCode(value: String): Value<String>(value, "\\d{6}".regex)
class AccountNumber(value: String): Value<String>(value, "\\d{8}".regex)
```

Validations are modelled as a simple typealias and there are several useful ones bundled with values4k:
```kotlin
typealias Validation<T> = (T) -> Boolean
```

### Masking
The final problem is one of PII data. We need to ensure that sensitive values are never outputted in their raw form into any logging infrastructure where they could be mined for nefarious purposes. 

```kotlin
val regex = "\\d{8}".regex // cache this statically so we don't keep creating them
class AccountNumber(value: String): Value<String>(value, regex, Maskers.hidden())
```

If we attempt to print our `AccountNumber` now will result in:
```kotlin
********
```

Masking rules are modelled as a simple typealias and there are several useful ones bundled with values4k:
```kotlin
typealias Masking<T> = T.() -> String
```
