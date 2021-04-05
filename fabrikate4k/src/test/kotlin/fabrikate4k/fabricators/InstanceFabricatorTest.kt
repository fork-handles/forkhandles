package fabrikate4k.fabricators

import fabrikate4k.Fabrikate
import fabrikate4k.fabricators.InstanceFabricator.NoUsableConstructor
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import kotlin.random.Random

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
        val a: A = Fabrikate.randomInstance()
        assertTrue(a is A)
        assertTrue("A" in a.toString())
    }

    @Test
    fun `throws NoUsableConstructor error if there is no constructor that could be used`() {
        try {
            Fabrikate.randomInstance<P>()
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
        val b: B = Fabrikate.randomInstance()
        assertTrue(b is B)
        assertEquals("B(a=A)", b.toString())
    }

    @Test
    fun `skips constructors that cannot be used`() {
        val d: D = Fabrikate.randomInstance()
        assertTrue(d is D)
        assertEquals("D(a=A)", d.toString())
    }

    @Test
    fun `creates primitives`() {
        assertTrue(Fabrikate.randomInstance<Int>() is Int)
        assertTrue(Fabrikate.randomInstance<Long>() is Long)
        assertTrue(Fabrikate.randomInstance<Double>() is Double)
        assertTrue(Fabrikate.randomInstance<Float>() is Float)
        assertTrue(Fabrikate.randomInstance<Char>() is Char)
        assertTrue(Fabrikate.randomInstance<String>() is String)
        //... etc. Don't forget about arrays
    }

    @Test
    fun `creates an instance using constructor with primitives and standard types`() {
        val b: B = Fabrikate.randomInstance()
        assertTrue(b is B)
        assertTrue("B(a=A)" in b.toString())

        val c: C = Fabrikate.randomInstance()
        assertTrue(c is C)
        assertTrue(
            c.toString().matches("C\\(b1=B\\(a=A\\), c=[A-z], b2=B\\(a=A\\), str=[A-z0-9]*, l=-?\\d*\\)".toRegex()),
            "It is $c"
        )
    }

    @Test
    fun `creates collections`() {
        val ints = Fabrikate.randomInstance<List<Int>>()
        assertTrue(ints is List<Int>)
        assertTrue(ints.toString().startsWith("["))
        assertTrue(ints.toString().endsWith("]"))

        val map = Fabrikate.randomInstance<Map<Long, String>>()
        assertTrue(map is Map<Long, String>)
        assertTrue(map.toString().startsWith("{"))
        assertTrue(map.toString().endsWith("}"))

        assertTrue(Fabrikate.randomInstance<Collection<A>>() is Collection<A>)
    }

    @Test
    fun `creates an instance using constructor with collections, primitives and standard types`() {
        val b: L = Fabrikate.randomInstance()
        assertTrue(b is L)
        assertTrue(b.ints is List<Int>)
        assertTrue(b.ints.firstOrNull() is Int?)

        val e: E = Fabrikate.randomInstance()
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
        val gt1 = Fabrikate.randomInstance<GT<Int>>()
        gt1.t = 1
        assertEquals(1, gt1.t)
        gt1.t = 10

        val gt2 = Fabrikate.randomInstance<GT<Long>>()
        gt2.t = 1L
        assertEquals(1L, gt2.t)
        gt2.t = 10L

        val gtRecursive = Fabrikate.randomInstance<GT<GT<Int>>>()
        gtRecursive.t = gt1
        assertEquals(gt1, gtRecursive.t)

        val ga1: GA<Int> = Fabrikate.randomInstance()
        assertTrue(ga1.t is Int)

        val ga2: GA<String> = Fabrikate.randomInstance()
        assertTrue(ga2.t is String)

        val gaa1: GAA<Int, String> = Fabrikate.randomInstance()
        assertTrue(gaa1.t1 is Int)
        assertTrue(gaa1.t2 is String)

        val gaa2: GAA<Long, List<Int>> = Fabrikate.randomInstance()
        assertTrue(gaa2.t1 is Long)
        assertTrue(gaa2.t2 is List<Int>)

        val gta: GTA<Long, String> = Fabrikate.randomInstance()
        gta.t2.length

        val gaaga: GAA<Long, GA<GT<Int>>> = Fabrikate.randomInstance()
        assertTrue(gaaga.t1 is Long)
        assertTrue(gaaga.t2 is GA<GT<Int>>)

        val gggg: GA<GA<GA<Int>>> = Fabrikate.randomInstance()
        gggg.t.t.t = 10
        assertEquals(10, gggg.t.t.t)
        gggg.t.t = GA(20)
        assertEquals(20, gggg.t.t.t)
    }

    @Test
    fun `when user expects empty collections, both Map and List are empty`() {
        val config = InstanceFabricatorConfig(
            collectionSizes = 0..0,
        )

        repeat(10) {
            assertEquals(
                emptyList<List<Int>>(),
                Fabrikate.randomInstance<List<Int>>(config)
            )
            assertEquals(
                emptyList<List<List<Int>>>(),
                Fabrikate.randomInstance<List<List<Int>>>(config)
            )
            assertEquals(
                emptyMap<Int, String>(),
                Fabrikate.randomInstance<Map<Int, String>>(config)
            )
        }
    }

    @Test
    fun `when user expects concrete collection size, both Map and List are of this size`() {
        val config = InstanceFabricatorConfig(
            collectionSizes = 5..5
        )

        repeat(10) {
            assertEquals(5, Fabrikate.randomInstance<List<Int>>(config).size)
            assertEquals(5, Fabrikate.randomInstance<List<List<Int>>>(config).size)
            assertEquals(5, Fabrikate.randomInstance<Map<Int, String>>(config).size)
        }
    }

    @Test
    fun `when user expects concrete String length, all Strings have this length`() {
        val config = InstanceFabricatorConfig(
            string = StringFabricator(5..5),
            collectionSizes = (2..2),
        )

        repeat(10) {
            assertEquals(5, Fabrikate.randomInstance<String>(config).length)
            assertEquals(5, Fabrikate.randomInstance<List<String>>(config)[0].length)
            assertEquals(5, Fabrikate.randomInstance<List<List<String>>>(config)[1][1].length)
        }
    }

    @Test
    fun `object set in config as Any, is always returned when we expect Any`() {
        val any = object {}
        val config = InstanceFabricatorConfig(any = AnyFabricator(any))

        repeat(10) {
            assertEquals(any, Fabrikate.randomInstance<Any>(config))
            assertEquals(any, Fabrikate.randomInstance<GA<Any>>(config).t)
            assertEquals(any, Fabrikate.randomInstance<GA<GA<Any>>>(config).t.t)
        }
    }

    @Test
    fun `check expected random values`() {
        val config = InstanceFabricatorConfig(random = Random(11))

        assertEquals(
            "A",
            Fabrikate.randomInstance<A>(config).toString()
        )

        assertEquals(
            "B(a=A)",
            Fabrikate.randomInstance<B>(config).toString()
        )

        assertEquals(
            "C(b1=B(a=A), c=h, b2=B(a=A), str=nGhZZ9O, l=8970572073855783324)",
            Fabrikate.randomInstance<C>(config).toString()
        )

        assertEquals(
            "D(a=A)",
            Fabrikate.randomInstance<D>(config).toString()
        )

        assertEquals(
            "E(b=B(a=A), map={1213693317552754728=DaEZaDZdlq, 2664417911166492291=Q, 3027621749827878795=Lz4, -6876593290335970832=sD, -5770131992713888015=wFRworN7}, l=-6053643314436350557)",
            Fabrikate.randomInstance<E>(config).toString()
        )

        assertEquals(
            "F(l=[E(b=B(a=A), map={4356568840000774042=wXbpNgKY, -5661535184940083983=31AN}, l=8355581566063425117), E(b=B(a=A), map={-4428731399617125871=858U2A, 4946810254014872353=EvlX, -8872764544214427894=Mq0e7Q}, l=-3647988506537829790), E(b=B(a=A), map={-5082975600357804165=4nidgrzQ5z, 4581876831692180829=7hU, 9182925820547913012=c0t}, l=-1694053907404763001)], e=E(b=B(a=A), map={4998150485251263969=2psruqY0, 6954434527856743375=ahWA5T1ewA}, l=1510234775683172956))",
            Fabrikate.randomInstance<F>(config).toString()
        )
    }
}
