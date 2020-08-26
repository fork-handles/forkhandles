# Result4K

Type safe error handling in Kotlin.

## Motivation

Kotlin does not type-check exceptions.  Result4k lets you type-check code that reports and recovers from errors.

A `Result<T,E>` represents the result of a calculation of a _T_ value that might fail with an error of type _E_.

You can use a `when` expression to determine if a Result represents a success or a failure, but most of the time you don't need to.  Result4k type provides many useful operations for handling success or failure without explicit conditionals.

Result4k works with the grain of the Kotlin language. Kotlin does not have language support for monads (known as "do notation" or "for comprehensions" in other languages). A pure monadic approach becomes verbose and awkward.  Therefore, Result4k lets you use early returns to avoid deep nesting when propagating errors.
