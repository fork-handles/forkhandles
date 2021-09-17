package dev.forkhandles.fabrikate

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.present
import dev.forkhandles.fabrikate.InstanceFabricator.NoUsableConstructor
import kotlinx.serialization.Serializable
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.File
import java.math.BigDecimal
import java.math.BigInteger
import java.net.URI
import java.net.URL
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.OffsetTime
import java.time.YearMonth
import java.time.ZonedDateTime
import java.util.Date
import java.util.UUID

@Suppress(
    "ConvertSecondaryConstructorToPrimary",
    "JoinDeclarationAndAssignment",
    "unused",
    "USELESS_IS_CHECK"
)
class InstanceFabricatorTest {
    class A {
        override fun toString(): String = "A"
    }

    data class B(val a: A)
    data class C(val b1: B, val c: Char, val b2: B, val str: String, val l: Long)

    class D {
        lateinit var a: A

        constructor() {
            throw Error("Do not use this one")
        }

        constructor(a: A) {
            this.a = a
        }

        override fun toString() = "D(a=$a)"
    }

    data class E(val b: B, val map: Map<Long, String>, val l: Long)
    data class F(val l: List<E>, val e: E)
    data class L(val ints: List<Int>)

    class P {
        private constructor()
    }

    @Test
    fun `creates single instance using an empty constructor`() {
        val a: A = Fabrikate().random()
        assertTrue(a is A)
        assertTrue("A" in a.toString())
    }

    @Test
    fun `throws NoUsableConstructor error if there is no constructor that could be used`() {
        try {
            Fabrikate().random<P>()
            error("makeRandomInstance should throw NoUsableConstructor error")
        } catch (e: NoUsableConstructor) {
            // no-op
        }
    }

    private fun catchError(function: () -> Unit): Throwable? = try {
        function()
        null
    } catch (throwable: Throwable) {
        throwable
    }

    @Test
    fun `creates using constructor`() {
        val b: B = Fabrikate().random()
        assertTrue(b is B)
        assertEquals("B(a=A)", b.toString())
    }

    @Test
    fun `skips constructors that cannot be used`() {
        val d: D = Fabrikate().random()
        assertTrue(d is D)
        assertEquals("D(a=A)", d.toString())
    }

    @Test
    fun `creates primitives`() {
        assertTrue(Fabrikate().random<Boolean>() is Boolean)
        assertTrue(Fabrikate().random<Int>() is Int)
        assertTrue(Fabrikate().random<Long>() is Long)
        assertTrue(Fabrikate().random<Double>() is Double)
        assertTrue(Fabrikate().random<Float>() is Float)
        assertTrue(Fabrikate().random<Char>() is Char)
        assertTrue(Fabrikate().random<String>() is String)
        //... etc. Don't forget about arrays
    }

    @Test
    fun `creates an instance using constructor with primitives and standard types`() {
        val b: B = Fabrikate().random()
        assertTrue(b is B)
        assertTrue("B(a=A)" in b.toString())

        val c: C = Fabrikate().random()
        assertTrue(c is C)
        assertTrue(
            c.toString().matches("C\\(b1=B\\(a=A\\), c=[A-z], b2=B\\(a=A\\), str=[A-z0-9]*, l=-?\\d*\\)".toRegex()),
            "It is $c"
        )
    }

    @Test
    fun `creates collections`() {
        val ints = Fabrikate().random<List<Int>>()
        assertTrue(ints is List<Int>)
        assertTrue(ints.toString().startsWith("["))
        assertTrue(ints.toString().endsWith("]"))

        val map = Fabrikate().random<Map<Long, String>>()
        assertTrue(map is Map<Long, String>)
        assertTrue(map.toString().startsWith("{"))
        assertTrue(map.toString().endsWith("}"))

        assertTrue(Fabrikate().random<Collection<A>>() is Collection<A>)
    }

    @Test
    fun `creates an instance using constructor with collections, primitives and standard types`() {
        val b: L = Fabrikate().random()
        assertTrue(b is L)
        assertTrue(b.ints is List<Int>)
        assertTrue(b.ints.firstOrNull() is Int?)

        val e: E = Fabrikate().random()
        assertTrue(e is E)
        assertTrue(e.map.all { (k, v) -> k is Long && v is String })
        assertTrue(e.toString().startsWith("E(b=B(a=A), map={"))
    }

    class GT<T> {
        var t: T? = null
    }

    class GA<T>(var t: T)
    class GAA<T1, T2>(val t1: T1, val t2: T2)
    class GTA<T1, T2>(val t2: T2)

    @Test
    fun `generic classes are supported`() {
        val gt1 = Fabrikate().random<GT<Int>>()
        gt1.t = 1
        assertEquals(1, gt1.t)
        gt1.t = 10

        val gt2 = Fabrikate().random<GT<Long>>()
        gt2.t = 1L
        assertEquals(1L, gt2.t)
        gt2.t = 10L

        val gtRecursive = Fabrikate().random<GT<GT<Int>>>()
        gtRecursive.t = gt1
        assertEquals(gt1, gtRecursive.t)

        val ga1: GA<Int> = Fabrikate().random()
        assertTrue(ga1.t is Int)

        val ga2: GA<String> = Fabrikate().random()
        assertTrue(ga2.t is String)

        val gaa1: GAA<Int, String> = Fabrikate().random()
        assertTrue(gaa1.t1 is Int)
        assertTrue(gaa1.t2 is String)

        val gaa2: GAA<Long, List<Int>> = Fabrikate().random()
        assertTrue(gaa2.t1 is Long)
        assertTrue(gaa2.t2 is List<Int>)

        val gta: GTA<Long, String> = Fabrikate().random()
        gta.t2.length

        val gaaga: GAA<Long, GA<GT<Int>>> = Fabrikate().random()
        assertTrue(gaaga.t1 is Long)
        assertTrue(gaaga.t2 is GA<GT<Int>>)

        val gggg: GA<GA<GA<Int>>> = Fabrikate().random()
        gggg.t.t.t = 10
        assertEquals(10, gggg.t.t.t)
        gggg.t.t = GA(20)
        assertEquals(20, gggg.t.t.t)
    }

    @Test
    fun `when user expects empty collections, both Map and List are empty`() {
        val config = FabricatorConfig(collectionSizes = 0..0).withStandardMappings()

        repeat(10) {
            assertEquals(
                emptyList<List<Int>>(),
                Fabrikate(config).random<List<Int>>()
            )
            assertEquals(
                emptyList<List<List<Int>>>(),
                Fabrikate(config).random<List<List<Int>>>()
            )
            assertEquals(
                emptyMap<Int, String>(),
                Fabrikate(config).random<Map<Int, String>>()
            )
        }
    }

    @Test
    fun `when user expects concrete collection size, both Map and List are of this size`() {
        val config = FabricatorConfig(collectionSizes = 5..5).withStandardMappings()

        repeat(10) {
            assertEquals(5, Fabrikate(config).random<List<Int>>().size)
            assertEquals(5, Fabrikate(config).random<List<List<Int>>>().size)
            assertEquals(5, Fabrikate(config).random<Map<Int, String>>().size)
        }
    }

    @Test
    fun `when user expects concrete String length, all Strings have this length`() {
        val config = FabricatorConfig(collectionSizes = (2..2))
            .withStandardMappings()
            .register(StringFabricator(5..5))

        repeat(10) {
            assertEquals(5, Fabrikate(config).random<String>().length)
            assertEquals(5, Fabrikate(config).random<List<String>>()[0].length)
            assertEquals(5, Fabrikate(config).random<List<List<String>>>()[1][1].length)
        }
    }

    @Test
    fun `object set in config as Any, is always returned when we expect Any`() {
        val any = object {}
        val config = FabricatorConfig()
            .withStandardMappings()
            .register(AnyFabricator(any))

        repeat(10) {
            assertEquals(any, Fabrikate(config).random<Any>())
            assertEquals(any, Fabrikate(config).random<GA<Any>>().t)
            assertEquals(any, Fabrikate(config).random<GA<GA<Any>>>().t.t)
        }
    }

    @Test
    fun `check expected random values`() {
        val config = FabricatorConfig(11).withStandardMappings()

        assertEquals(
            "A",
            Fabrikate(config).random<A>().toString()
        )

        assertEquals(
            "B(a=A)",
            Fabrikate(config).random<B>().toString()
        )

        assertEquals(
            "C(b1=B(a=A), c=h, b2=B(a=A), str=nGhZZ9O, l=8970572073855783324)",
            Fabrikate(config).random<C>().toString()
        )

        assertEquals(
            "D(a=A)",
            Fabrikate(config).random<D>().toString()
        )

        assertEquals(
            "E(b=B(a=A), map={1213693317552754728=DaEZaDZdlq, 2664417911166492291=Q, 3027621749827878795=Lz4, -6876593290335970832=sD, -5770131992713888015=wFRworN7}, l=-6053643314436350557)",
            Fabrikate(config).random<E>().toString()
        )

        assertEquals(
            "F(l=[E(b=B(a=A), map={4356568840000774042=wXbpNgKY, -5661535184940083983=31AN}, l=8355581566063425117), E(b=B(a=A), map={-4428731399617125871=858U2A, 4946810254014872353=EvlX, -8872764544214427894=Mq0e7Q}, l=-3647988506537829790), E(b=B(a=A), map={-5082975600357804165=4nidgrzQ5z, 4581876831692180829=7hU, 9182925820547913012=c0t}, l=-1694053907404763001)], e=E(b=B(a=A), map={4998150485251263969=2psruqY0, 6954434527856743375=ahWA5T1ewA}, l=1510234775683172956))",
            Fabrikate(config).random<F>().toString()
        )
    }

    @Suppress("ArrayInDataClass")
    data class Foobar(
        val a: Int,
        val b: Long,
        val c: Double,
        val d: Float,
        val e: Char,
        val f: String,
        val g: ByteArray,
        val h: BigInteger,
        val i: BigDecimal,
        val j: Instant,
        val k: LocalDate,
        val l: LocalTime,
        val m: LocalDateTime,
        val n: OffsetTime,
        val o: OffsetDateTime,
        val p: ZonedDateTime,
        val q: Set<String>,
        val r: List<String>,
        val s: Map<String, String>,
        val t: URI,
        val u: URL,
        val v: Date,
        val w: File,
        val x: UUID,
        val y: Duration,
        val z: YearMonth,
        val aa: Boolean
    )

    @Test
    fun `supports common types`() {
        assertThat(Fabrikate().random<Foobar>(), present())
    }

    @Suppress("DataClassPrivateConstructor")
    data class X private constructor(val a: Instant) {
        companion object {
            fun parse(a: String): X = X(Instant.parse(a))
            fun factory(a: Instant): X = X(a)
            fun somethingElse(a: String): String = a
        }
    }

    interface Q

    @Suppress("DataClassPrivateConstructor")
    data class R private constructor(val a: String) : Q {
        companion object {
            fun of(name: String): Q = R(name)
            fun somethingElse(a: String): String = a
        }
    }

    @Test
    fun `supports static factory methods`() {
        val config = FabricatorConfig(19191).withStandardMappings()
        assertThat(Fabrikate(config).random<X>().toString(), equalTo("X(a=2018-12-07T16:10:15Z)"))
        assertThat(Fabrikate(config).random<R>().toString(), equalTo("R(a=2dfFRx3d6v)"))
    }

    data class S(val a: Instant?)

    @Test
    fun `randomly sets nullable properties to null by default`() {
        assertThat(Fabrikate(FabricatorConfig(1).withStandardMappings()).random<S>().toString(), equalTo("S(a=1982-07-13T19:53:20Z)"))
        assertThat(Fabrikate(FabricatorConfig(2).withStandardMappings()).random<S>().toString(), equalTo("S(a=null)"))
    }

    @Test
    fun `does not set nullable properties to null if explicitly configured`() {
        assertThat(Fabrikate(FabricatorConfig(1, nullableStrategy = FabricatorConfig.NullableStrategy.NeverSetToNull)
            .withStandardMappings()).random<S>().toString(), equalTo("S(a=1992-11-18T07:29:28Z)"))
        assertThat(Fabrikate(FabricatorConfig(2,nullableStrategy = FabricatorConfig.NullableStrategy.NeverSetToNull)
            .withStandardMappings()).random<S>().toString(), equalTo("S(a=2002-01-05T13:32:51Z)"))
    }

    @Test
    fun `sets nullable properties to null if explicitly configured`() {
        assertThat(Fabrikate(FabricatorConfig(1, nullableStrategy = FabricatorConfig.NullableStrategy.AlwaysSetToNull)
            .withStandardMappings()).random<S>().toString(), equalTo("S(a=null)"))
        assertThat(Fabrikate(FabricatorConfig(2,nullableStrategy = FabricatorConfig.NullableStrategy.AlwaysSetToNull)
            .withStandardMappings()).random<S>().toString(), equalTo("S(a=null)"))
    }

    enum class RandomEnum {
        A, B, C, D
    }

    data class T(val a: RandomEnum, val b: List<RandomEnum>)

    @Test
    fun `does enums`() {
        assertThat(Fabrikate(FabricatorConfig(2).withStandardMappings()).random<T>().toString(), equalTo("T(a=D, b=[D, A, A, D, A])"))
    }

    @Serializable
    data class KotlinSerializable(val string: String)

    @Test
    fun `does not create nulls on non-nullables for serializable classes`(){
        val random = Fabrikate(FabricatorConfig(84).withStandardMappings()).random<KotlinSerializable>()
        val nonNullableString = random.string
        assertThat(nonNullableString, present())
    }

    class TestListFabricator: Fabricator<List<String>>{
        override fun invoke(): List<String> {
            return testList
        }

        companion object {
            val testList = listOf("TestList")
        }
    }

    @Test
    fun `registered Fabricators override defaults`(){
        val defaultFabrikate = Fabrikate(FabricatorConfig())
        val overriddenFabrikate = Fabrikate(FabricatorConfig().register(TestListFabricator()))

        val randomDefault = defaultFabrikate.random<List<String>>()
        val randomOverridden = overriddenFabrikate.random<List<String>>()

        assertNotEquals(randomDefault, randomOverridden)
        assertThat(randomOverridden, equalTo(TestListFabricator.testList))
    }
}
