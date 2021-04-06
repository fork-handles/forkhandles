package fabrikate4k.fabricators

import java.lang.reflect.*
import java.time.*
import java.util.*
import kotlin.reflect.*
import kotlin.reflect.full.createType

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
                        } catch (ignore: Throwable) {
                            System.err.println("Failed to call constructor. Seed=${config.seed}. Reason=${ignore.message}")
                            ignore.printStackTrace()
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
            Set::class -> makeRandomSet(classRef, type)
            List::class, Collection::class -> makeRandomList(classRef, type)
            Map::class -> makeRandomMap(classRef, type)
            else -> mappings[classRef]?.invoke()
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

    private fun makeRandomSet(classRef: KClass<*>, type: KType): Set<Any?> = with(config) {
        makeRandomList(classRef, type).toSet()
    }

    private fun makeRandomList(classRef: KClass<*>, type: KType): List<Any?> = with(config) {
        val numOfElements = random.nextInt(collectionSizes.first, collectionSizes.last + 1)
        val elemType = type.arguments[0].type!!
        (1..numOfElements).map { makeRandomInstanceForParam(elemType, classRef, type) }
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
