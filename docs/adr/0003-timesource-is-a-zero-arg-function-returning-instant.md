# 3. TimeSource is a zero-arg function returning Instant

Date: 2020-06-30

## Status

Accepted

## Context

We want a way to decouple code that depends on the current time from the source of that time, to make it easier to write tests or run that code as if at a different time.  A common way to do this will make it easier to combine the ForK libraries.

On the JVM, the java.time package contains a Clock type. However, this is an abstract class, not an interface or function type.  Therefore you cannot easily proxy a clock or use function composition to define new kinds of Clock.

The Clock name is taken, so what name should we use?  Suggestions included: Clock (and put up with the name clash), TimeSource, Now, Klock, Clokk, Clockk, Timepiece, Chronometer, ITellYouTheTime, ITellYouATime.

Should the type be defined as an interface with a method that returns the time:

```kotlin
interface TimeSourceType {
  fun now(): TimeType
}
```

... or a typealias to a function that returns the time:

```kotlin
typealias TimeSourceType = () -> TimeType
```

... or an interface that extends the type of a function that returns the time?  

```kotlin
interface TimeSourceType: () -> TimeType
```

Making TimeSource an interface that extends function means you can’t just pass a lambda where a TimeSource is needed, and you can’t use function composition to define new types of time sources

If we make it an interface then you can't use a reference to `Clock::now` as a TimeSource. It’s more verbose to treat it as just a plain ol’ function and pass it to generic higher-order functions

The typealias has the ability to play both sides of the debate. You don't have to use the typealias if you don't want to, and the people that don't want to declare bare function types don't have to.

If people can just use Clock::now, it's very easy to just ditch the abstraction completely and always use a real clock everywhere, defeating the purpose of this. But they can always just call Clock.standardClock().now() or `new Instant()`, through a static reference.  We can’t stop people doing that. Is it worth creating a bit more friction to increase the learning opportunity for those new to the concept?


Should the time be returned as am Instant or a ZonedDateTime.  The java.time.Clock class returns time as an Instant.  (It does _have_ a TimeZone property, but doesn't use it!).


## Decision

The type will be called TimeSource. The Source bit suggests that time information lives outside of your code, which is something most people seem to forget.

It will return Instant.

It will be defined as a typealias.

```kotlin
typealias TimeSource = ()->Instant
```

Time4k will provide an implementation that returns the current system time.


## Consequences

Code can define TimeSources with lambdas, function composition, function references.

Client code has to be explicit about converting Instants to ZonedDateTimes.

Client code does not need to use the typealias.
