package fabrikate4k.fabricators

import java.lang.reflect.*
import java.time.*
import java.util.*
import kotlin.reflect.*
import kotlin.reflect.full.companionObject
import kotlin.reflect.full.companionObjectInstance
import kotlin.reflect.full.createType
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.memberFunctions

open class InstanceFabricator(
    private val config: FabricatorConfig
) {
    class NoUsableConstructor : Error()

    fun makeRandomInstance(classRef: KClass<*>, type: KType): Any? =
        when {
            type.isMarkedNullable && config.random.nextBoolean() -> null
            else -> when (val primitive = makeStandardInstanceOrNull(classRef, type)) {
                null -> {
                    val constructors = findConstructorFunctions(classRef, type)
                    val factories = findFactoryFunctions(classRef, type)
                    (constructors + factories).forEach { (fn, transform) ->
                        try {
                            return fn.parameters.map(transform).toTypedArray().let(fn::call)!!
                        } catch (ignore: Throwable) {
                            // do nothing
                        }
                    }
                    throw NoUsableConstructor()
                }
                else -> primitive
            }
        }

    private fun findFactoryFunctions(
        classRef: KClass<*>,
        type: KType
    ): List<Pair<KFunction<*>, (KParameter) -> Any?>> = (classRef.companionObject
        ?.memberFunctions
        ?.filter { it.returnType == type || type.isSubtypeOf(it.returnType) }
        ?.sortedByDescending { it.parameters.any { p -> p.type == String::class } }
        ?.toTypedArray() ?: emptyArray())
        .toList()
        .map {
            it to { param: KParameter ->
                if (param.kind == KParameter.Kind.INSTANCE) classRef.companionObjectInstance
                else makeRandomInstanceForParam(param.type, classRef, type)
            }
        }

    private fun findConstructorFunctions(
        classRef: KClass<*>,
        type: KType
    ): List<Pair<KFunction<Any>, (KParameter) -> Any?>> = classRef.constructors
        .shuffled(config.random)
        .map { it to { param: KParameter -> makeRandomInstanceForParam(param.type, classRef, type) } }

    private fun makeRandomInstanceForParam(
        paramType: KType,
        classRef: KClass<*>,
        type: KType
    ): Any? = when (val classifier = paramType.classifier) {
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
        when {
            classRef == Set::class -> makeRandomSet(classRef, type)
            classRef == List::class -> makeRandomList(classRef, type)
            classRef == Collection::class -> makeRandomList(classRef, type)
            classRef == Map::class -> makeRandomMap(classRef, type)
            classRef.isSubclassOf(Enum::class) -> makeRandomEnum(classRef)
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

    private fun makeRandomEnum(classRef: KClass<*>): Any? = with(config) {
        val enumConstants = classRef.java.enumConstants
        val sizes = 0..enumConstants.toList().size
        val randomIdx = random.nextInt(sizes.first(), sizes.last)
        enumConstants[randomIdx]
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
