package dev.forkhandles.lens

import kotlin.reflect.KProperty1
import kotlin.reflect.full.instanceParameter
import kotlin.reflect.full.memberFunctions

interface Lens<T, R> : (T) -> R {
    fun get(subject: T): R
    fun inject(subject: T, value: R): T
    fun update(subject: T, f: (R) -> R): T = inject(subject, f(get(subject)))
    override fun invoke(subject: T): R = get(subject)
    operator fun invoke(subject: T, value: R): T = inject(subject, value)
}

infix fun <T1, T2, R> LensObject<T1, T2>.andThen(second: LensObject<T2, R>) = LensObject<T1, R>(
    { second.get(get(it)) },
    { subject, value ->
        inject(
            subject,
            second.inject(get(subject), value)
        )
    }
)

operator fun <IN : Any, OUT> IN.get(extractor: (IN) -> OUT) = extractor(this)
fun <IN : Any, OUT> IN.with(lens: Lens<IN, OUT>, of: OUT) = lens.inject(this, of)
fun <IN : Any, OUT> IN.updatedWith(lens: Lens<IN, OUT>, f: (OUT) -> OUT) = lens.update(this, f)

data class LensObject<IN, OUT>(
    val getter: (IN) -> OUT,
    val injector: (IN, OUT) -> IN
) : Lens<IN, OUT> {
    override fun get(subject: IN) = getter(subject)
    override fun inject(subject: IN, value: OUT) = injector(subject, value)
}

inline fun <reified IN : Any, OUT> KProperty1<IN, OUT>.asLens(): LensObject<IN, OUT> = LensObject(
    ::get,
    reflectiveCopy(name)
)

inline fun <reified T : Any, R> reflectiveCopy(propertyName: String): (T, R) -> T {
    val copyFunction = T::class.memberFunctions.firstOrNull { it.name == "copy" }
        ?: error("No copy method found")
    val instanceParam = copyFunction.instanceParameter
        ?: error("No copy method instance parameter found")
    val nameParam = copyFunction.parameters.find { it.name == propertyName }
        ?: error("No copy method parameter named $propertyName found")
    return { subject, value ->
        copyFunction.callBy(
            mapOf(
                instanceParam to subject,
                nameParam to value
            )
        ) as T
    }
}
