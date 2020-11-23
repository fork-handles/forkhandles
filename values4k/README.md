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

The base type provided by this lib is `Value`, which is just a simple wrapper around a `value` field and can be used for defining your own domain types:

```kotlin
class Money(value: Int): Value<Int>(value)
class SortCode(value: String): Value<String>(value)
class AccountNumber(value: String): Value<String>(value)

fun transferMoneyTo(amount: Money, accountNumber: AccountNumber, sortCode: SortCode)
```

### Validation
The next problem is that there is no domain validation on our values. What if someone passed in a negative amount? Or an `accountNumber` containing letters instead of digits?

We can fix that by validating to ensure we can never create an illegal value. We want values to fail on construction (at the entry point to our system) instead of deep inside our domain logic. For this we can force construction to go through a ValueFactory:

```kotlin
class Money private constructor(value: Int) : Value<Int>(value) {
    companion object : ValueFactory<Money, Int>(::Money, 1.minValue)
}

class SortCode  private constructor(value: String) : StringValue(value) {
    companion object : ValueFactory<SortCode, String>(::SortCode, "\\d{6}".regex)
}
```

Constructing the instances then happens using one of the built-in or user-supplied factories:

```kotlin
Money.of(123) // returns Money(123)
Money.of(0) // throws IllegalArgumentException
SortCode.ofNullable("123") // returns null
SortCode.ofResult4k("asdf12") // returns Failure<Exception>
```

Validations are modelled as a simple typealias and there are several useful ones bundled with values4k:
```kotlin
typealias Validation<T> = (T) -> Boolean
```

### Masking
The final problem is one of PII data. We need to ensure that sensitive values are never outputted in their raw form into any logging infrastructure where they could be mined for nefarious purposes. 

```kotlin
class AccountNumber  private constructor(value: String) : Value<String>(value, hidden()) {
    companion object : ValueFactory<AccountNumber, String>(::AccountNumber, "\\d{8}".regex)
}
```

If we attempt to print our `AccountNumber` now will result in:
```kotlin
********
```

Masking rules are modelled as a simple typealias and there are several useful ones bundled with values4k:
```kotlin
typealias Masking<T> = T.() -> String
```
