# Values4K

<a href="https://bintray.com/fork-handles/maven/forkhandles-bom/_latestVersion"><img alt="Download" src="https://api.bintray.com/packages/fork-handles/maven/forkhandles-bom/images/download.svg"></a>
<a href="https://travis-ci.org/fork-handles/forkhandles"><img alt="build status" src="https://travis-ci.org/fork-handles/forkhandles.svg?branch=trunk"/></a>
<a href="https://codecov.io/gh/fork-handles/forkhandles"><img src="https://codecov.io/gh/fork-handles/forkhandles/branch/trunk/graph/badge.svg"/></a>
<a href="http//www.apache.org/licenses/LICENSE-2.0"><img alt="GitHub license" src="https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat"></a>
<a href="http://kotlinlang.org"><img alt="kotlin" src="https://img.shields.io/badge/kotlin-1.4-blue.svg"></a>
<a href="https://codebeat.co/projects/github-com-fork-handles-forkhandles-trunk"><img alt="codebeat badge" src="https://codebeat.co/badges/5b369ed4-af27-46f4-ad9c-a307d900617e"></a>

Lightweight, validatable base Value types - aka Microtypes - aka Tinytypes

## Installation

In Gradle, install the BOM and then any other modules in the dependency block:

```kotlin 
implementation(platform("dev.forkhandles:forkhandles-bom:X.Y.Z"))
implementation("dev.forkhandles:values4k")
```

## Motivation & Concepts
Subvert primitive obsession and provide type safety and other facilities for JVM programs.

### Typesafety

The problem which we are trying to solve is to avoid illegal values entering into our system. For this, it is best to use strongly typed values, which allow us to both lean on the compiler and improve the developer experience by engaging with IDE tooling.

For example, take this simple function:
```kotlin
fun transferMoneyTo(amount: Int, sortCode: String, accountNumber: String)
```

The first problem here is that `accountNumber` and `sortCode` fields are both of type `String`, meaning that a coder could accidentally switch these values around and we would not potentially  notice until runtime.

The base type provided by this lib is the interface `Value<T>`. This ie extended by `AbstractValue<T>` or one of the typealiases, which are just a simple wrapper around a `value` field and can be used for defining your own domain types. Inline classes are also supported by just implementing `Value<T>`:

```kotlin
class Money(value: Int): AbstractValue<Int>(value)
class AccountNumber(value: String): StringValue(value)
inline class SortCode(override val value: String): Value<String>

fun transferMoneyTo(amount: Money, sortCode: SortCode, accountNumber: AccountNumber)
```

### Validation
The next problem is that there is no domain validation on our values. What if someone passed in a negative amount? Or an `accountNumber` containing letters instead of digits?

We can fix that by validating to ensure we can never create an illegal value. We want values to fail on construction (at the entry point to our system) instead of deep inside our domain logic. For this we can force construction to go through a `ValueFactory` or one of the provided convenience subclasses (`IntValueFactory`, `LocalDateTimeValueFactory` etc..), passing a `Validation` predicate:

```kotlin
class Money private constructor(value: Int) : AbstractValue<Int>(value) {
    companion object : ValueFactory<Money, Int>(::Money, 1.minValue)
}

class AccountNumber private constructor(value: String) : StringValue(value) {
    companion object : StringValueFactory<AccountNumber>(::AccountNumber, "\\d{8}".regex)
}

// note that private constructors are not available on inline classes until Kotlin 1.4.30
inline class SortCode /** private constructor **/(override val value: String) : Value<T> {
    companion object : StringValueFactory<SortCode>(::SortCode, "\\d{6}".regex)
}
```

Constructing the instances then happens using one of the built-in or user-supplied factories:

```kotlin
Money.of(123) // returns Money(123)
Money.of(0) // throws IllegalArgumentException
SortCode.ofOrNull("123") // returns null
SortCode.ofResult4k("asdf12") // returns Failure<Exception>
Money.parse("123") // returns Money(123)
Money.parse("notmoney") // throws IllegalArgumentException
SortCode.parseOrNull("123") // returns null
SortCode.parseResult4k("asdf12") // returns Failure<Exception>
```

Validations are modelled as a simple typealias and there are several useful ones bundled with values4k:
```kotlin
typealias Validation<T> = (T) -> Boolean
```

### Masking
The last big problem is one of PII data. We need to ensure that sensitive values are never outputted in their raw form into any logging infrastructure where they could be mined for nefarious purposes. 

```kotlin
class AccountNumber private constructor(value: String) : StringValue(value, hidden()) {
    companion object : StringValueFactory<AccountNumber>(::AccountNumber, "\\d{8}".regex)
}
```

If we attempt to print our `AccountNumber` using toString() now will result in:
```kotlin
********
```

Masking rules are modelled as a simple typealias and there are several useful ones bundled with values4k:
```kotlin
typealias Masking<T> = T.() -> String
```

### Show
For times where we want to display the underlying value as a String, we can use `show()`, which is the natural opposite to `parse()`. This is different (and safer than) using toString(), where we will have to deal with the Masking rules. In order to maintain symmetry (and to ensure that we can support inline classes), this method is present on the ValueFactory instance - this looks a little strange but it actually is consistent because the display and parse logic should NOT be part of the `Value` itself, but be separated logically.

```kotlin
Money.show(Money.of(123)) // returns "123"
```
