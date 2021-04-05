package fabrikate4k.fabricators

import java.io.File
import java.lang.reflect.*
import java.math.BigDecimal
import java.math.BigInteger
import java.net.URI
import java.net.URL
import java.time.*
import java.util.*
import kotlin.random.Random
import kotlin.reflect.*
import kotlin.reflect.full.createType

data class FabricatorConfig(
    val random: Random = Random,
    val collectionSizes: IntRange = 1..5,
    val string: Fabricator<String> = StringFabricator(random = random),
    val long: Fabricator<Long> = LongFabricator(random),
    val int: Fabricator<Int> = IntFabricator(random),
    val double: Fabricator<Double> = DoubleFabricator(random),
    val float: Fabricator<Float> = FloatFabricator(random),
    val char: Fabricator<Char> = CharFabricator(random = random),
    val byte: Fabricator<ByteArray> = BytesFabricator(random = random),
    val bigInteger: Fabricator<BigInteger> = BigIntegerFabricator(random = random),
    val bigDecimal: Fabricator<BigDecimal> = BigDecimalFabricator(random = random),
    val instant: Fabricator<Instant> = InstantFabricator(random),
    val localDate: Fabricator<LocalDate> = LocalDateFabricator(random),
    val localTime: Fabricator<LocalTime> = LocalTimeFabricator(random),
    val localDateTime: Fabricator<LocalDateTime> = LocalDateTimeFabricator(random),
    val offsetDateTime: Fabricator<OffsetDateTime> = OffsetDateTimeFabricator(random),
    val offsetTime: Fabricator<OffsetTime> = OffsetTimeFabricator(random),
    val zonedDateTime: Fabricator<ZonedDateTime> = ZonedDateTimeFabricator(random),
    val duration: Fabricator<Duration> = DurationFabricator(random),
    val date: Fabricator<Date> = DateFabricator(random),
    val uri: Fabricator<URI> = UriFabricator(random),
    val url: Fabricator<URL> = UrlFabricator(random),
    val file: Fabricator<File> = FileFabricator(),
    val uuid: Fabricator<UUID> = UUIDFabricator(),
    val any: Fabricator<Any> = AnyFabricator(),
)

open class InstanceFabricator(
    private val config: FabricatorConfig = FabricatorConfig()
) {
    class NoUsableConstructor : Error()

    fun makeRandomInstance(classRef: KClass<*>, type: KType): Any =
        when (val primitive = makeStandardInstanceOrNull(classRef, type)) {
            null -> {
                classRef.constructors
                    .shuffled(config.random)
                    .forEach { constructor ->
                        try {
                            return constructor.parameters
                                .map { makeRandomInstanceForParam(it.type, classRef, type) }
                                .toTypedArray()
                                .let(constructor::call)
                        } catch (e: Throwable) {
                            e.printStackTrace()
                            // no-op. We catch any possible error here that might occur during class creation
                        }
                    }
                throw NoUsableConstructor()
            }
            else -> primitive
        }

    private fun makeRandomInstanceForParam(
        paramType: KType,
        classRef: KClass<*>,
        type: KType
    ): Any = when (val classifier = paramType.classifier) {
        is KClass<*> -> makeRandomInstance(classifier, paramType)
        is KTypeParameter -> {
            val typeParameterName = classifier.name
            val typeParameterId = classRef.typeParameters.indexOfFirst { it.name == typeParameterName }
            val parameterType = type.arguments[typeParameterId].type ?: getKType<Any>()
            makeRandomInstance(parameterType.classifier as KClass<*>, parameterType)
        }
        else -> throw Error("Type of the classifier $classifier is not supported")
    }

    private fun makeStandardInstanceOrNull(classRef: KClass<*>, type: KType) = with(config) {
        when (classRef) {
            Any::class -> any()
            Int::class -> int()
            Long::class -> long()
            Double::class -> double()
            Float::class -> float()
            Char::class -> char()
            String::class -> string()
            ByteArray::class -> byte()
            BigInteger::class -> bigInteger()
            BigDecimal::class -> bigDecimal()
            Instant::class -> instant()
            LocalDate::class -> localDate()
            LocalTime::class -> localTime()
            LocalDateTime::class -> localDateTime()
            OffsetDateTime::class -> offsetDateTime()
            OffsetTime::class -> offsetTime()
            ZonedDateTime::class -> zonedDateTime()
            Date::class -> date()
            URI::class -> uri()
            URL::class -> url()
            File::class -> file()
            Duration::class -> duration()
            UUID::class -> uuid()
            Set::class -> makeRandomSet(classRef, type)
            List::class, Collection::class, Set::class -> makeRandomList(classRef, type)
            Map::class -> makeRandomMap(classRef, type)
            else -> null
        }
    }

    private fun makeRandomMap(classRef: KClass<*>, type: KType): Map<Any?, Any?> = with(config) {
        val numOfElements = random.nextInt(collectionSizes.first, collectionSizes.last + 1)
        val keyType = type.arguments[0].type!!
        val valType = type.arguments[1].type!!
        val keys = (1..numOfElements).map { makeRandomInstanceForParam(keyType, classRef, type) }
        val values = (1..numOfElements).map { makeRandomInstanceForParam(valType, classRef, type) }
        keys.zip(values).toMap()
    }

    private fun makeRandomList(classRef: KClass<*>, type: KType): List<Any?> = with(config) {
        val numOfElements = random.nextInt(collectionSizes.first, collectionSizes.last + 1)
        val elemType = type.arguments[0].type!!
        (1..numOfElements).map { makeRandomInstanceForParam(elemType, classRef, type) }
    }

    private fun makeRandomSet(classRef: KClass<*>, type: KType): Set<Any?> = with(config) {
        val numOfElements = random.nextInt(collectionSizes.first, collectionSizes.last + 1)
        val elemType = type.arguments[0].type!!
        (1..numOfElements).map { makeRandomInstanceForParam(elemType, classRef, type) }.toSet()
    }
}

// --- Interface ---

inline fun <reified T : Any> getKType(): KType =
    object : SuperTypeTokenHolder<T>() {}.getKTypeImpl()

// --- Implementation ---

open class SuperTypeTokenHolder<T>

fun SuperTypeTokenHolder<*>.getKTypeImpl(): KType =
    javaClass.genericSuperclass.toKType().arguments.single().type!!

fun KClass<*>.toInvariantFlexibleProjection(arguments: List<KTypeProjection> = emptyList()): KTypeProjection {
    // TODO: there should be an API in kotlin-reflect which creates KType instances corresponding to flexible types
    // Currently we always produce a non-null type, which is obviously wrong
    val args = if (java.isArray) listOf(java.componentType.kotlin.toInvariantFlexibleProjection()) else arguments
    return KTypeProjection.invariant(createType(args, nullable = false))
}

fun Type.toKTypeProjection(): KTypeProjection = when (this) {
    is Class<*> -> this.kotlin.toInvariantFlexibleProjection()
    is ParameterizedType -> {
        val erasure = (rawType as Class<*>).kotlin
        erasure.toInvariantFlexibleProjection(
            (erasure.typeParameters.zip(actualTypeArguments).map { (parameter, argument) ->
                val projection = argument.toKTypeProjection()
                projection.takeIf {
                    // Get rid of use-site projections on arguments, where the corresponding parameters already have a declaration-site projection
                    parameter.variance == KVariance.INVARIANT || parameter.variance != projection.variance
                } ?: KTypeProjection.invariant(projection.type!!)
            })
        )
    }
    is WildcardType -> when {
        lowerBounds.isNotEmpty() -> KTypeProjection.contravariant(lowerBounds.single().toKType())
        upperBounds.isNotEmpty() -> KTypeProjection.covariant(upperBounds.single().toKType())
        // This looks impossible to obtain through Java reflection API, but someone may construct and pass such an instance here anyway
        else -> KTypeProjection.STAR
    }
    is GenericArrayType -> Array<Any>::class.toInvariantFlexibleProjection(listOf(genericComponentType.toKTypeProjection()))
    is TypeVariable<*> -> TODO() // TODO
    else -> throw IllegalArgumentException("Unsupported type: $this")
}

fun Type.toKType(): KType = toKTypeProjection().type!!
