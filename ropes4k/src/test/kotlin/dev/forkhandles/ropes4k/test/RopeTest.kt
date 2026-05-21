/*
 * Copyright (C) 2024 James Richardson
 *  - Originally Copyright (C) 2007 Amin Ahmad.
 * Licenced under GPL
 */
package dev.forkhandles.ropes4k.test

import dev.forkhandles.ropes4k.Rope
import dev.forkhandles.ropes4k.impl.ConcatenationRope
import dev.forkhandles.ropes4k.impl.FlatCharArrayRope
import dev.forkhandles.ropes4k.impl.FlatCharSequenceRope
import dev.forkhandles.ropes4k.impl.ReverseRope
import dev.forkhandles.ropes4k.impl.SubstringRope
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import strikt.api.Assertion
import strikt.api.expectThat
import strikt.api.expectThrows
import strikt.assertions.isA
import strikt.assertions.isEqualTo
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.StringWriter

class RopeTest {
    private fun Rope.substring(start: Int, end: Int): String {
        val out = StringWriter(end - start)
        write(out, start, end - start)
        return out.toString()
    }

    @IgnorableReturnValue
    private fun <T : Iterator<Char>> Assertion.Builder<T>.isFinished(): Assertion.Builder<Boolean> {
        return get { hasNext() }.isEqualTo(false)
    }

    @Test
    fun testSubstringDeleteBug() {
        val s = "12345678902234567890"

        var rope = Rope.ofCopy(s.toCharArray()) // bugs

        rope = rope.delete(0, 1)

        expectThat(rope.substring(0, 2)).isEqualTo("23")
        expectThat(rope.substring(0, 0)).isEqualTo("")
        expectThat(rope.substring(7, 10)).isEqualTo("902")


        rope = Rope.of(s) // no bugs
        rope = rope.delete(0, 1)
        expectThat(rope.substring(0, 2)).isEqualTo("23")
        expectThat(rope.substring(0, 0)).isEqualTo("")
        expectThat(rope.substring(7, 10)).isEqualTo("902")
    }

    /**
     * Bug reported by ugg.ugg@gmail.com.
     */
    @Test
    fun testRopeWriteBug() {
        var r = Rope.of("")
        r = r.append("round ")
        r = r.append(0.toString())
        r = r.append(" 1234567890")

        expectThat(r.substring(0, 6)).isEqualTo("round ")
        expectThat(r.substring(0, 7)).isEqualTo("round 0")
        expectThat(r.substring(0, 8)).isEqualTo("round 0 ")
        expectThat(r.substring(0, 9)).isEqualTo("round 0 1")
        expectThat(r.substring(0, 10)).isEqualTo("round 0 12")
        expectThat(r.substring(0, 18)).isEqualTo("round 0 1234567890")
        expectThat(r.substring(0, r.length)).isEqualTo("round 0 1234567890")
    }


    @Test
    fun testLengthOverflow() {
        var x1 = Rope.of("01")
        for (j in 2..30) x1 = x1.append(x1)
        expectThat(x1.length).isEqualTo(1073741824)
        try {
            val _ = x1.append(x1)
            Assertions.fail<Any>("Expected overflow.")
        } catch (e: IllegalArgumentException) {
            // this is what we expect
        }
    }

    @Test
    fun testMatches() {
        val x1 = FlatCharSequenceRope("0123456789")
        val x2: Rope = ConcatenationRope(x1, x1)

        Assertions.assertTrue(x2.matches("0.*9".toRegex()))
        Assertions.assertTrue(x2.matches("0.*90.*9".toRegex()))
    }

    @Test
    fun testConcatenationFlatFlat() {
        var r1 = Rope.of("alpha")
        val r2 = Rope.of("beta")
        var r3 = r1.append(r2)
        expectThat(r3).isString("alphabeta")

        r1 = Rope.of("The quick brown fox jumped over")
        r3 = r1.append(r1)
        expectThat(r3).isString("The quick brown fox jumped overThe quick brown fox jumped over")
    }

    @Test
    fun testIterator() {
        val x1 = FlatCharSequenceRope("0123456789")
        val x2 = FlatCharSequenceRope("0123456789")
        val x3 = FlatCharSequenceRope("0123456789")
        val c1 = ConcatenationRope(x1, x2)
        val c2 = ConcatenationRope(c1, x3)

        var i: Iterator<Char> = c2.iterator()
        for (j in 0 until c2.length) {
            Assertions.assertTrue(i.hasNext(), "Has next (" + j + "/" + c2.length + ")")
            val _ = i.next()
        }
        expectThat(i).isFinished()

        val z1 = FlatCharSequenceRope("0123456789")
        val z2 = SubstringRope(z1, 2, 0)
        val z3 = SubstringRope(z1, 2, 2)
        val z4 = ConcatenationRope(z3, SubstringRope(z1, 6, 2)) // 2367

        i = z2.iterator()
        expectThat(i).isFinished()
        i = z3.iterator()
        expectThat(i.next()).isEqualTo('2')
        expectThat(i.next()).isEqualTo('3')
        expectThat(i).isFinished()
        for (j in 0..z3.length) {
            try {
                val _ = z3.iterator(j)
            } catch (e: Exception) {
                Assertions.fail<Any>("$j $e")
            }
        }
        Assertions.assertTrue(4 == z4.length)
        for (j in 0..z4.length) {
            try {
                val _ = z4.iterator(j)
            } catch (e: Exception) {
                Assertions.fail<Any>("$j $e")
            }
        }
        i = z4.iterator(4)
        expectThat(i).isFinished()
        i = z4.iterator(2)
        expectThat(i.next()).isEqualTo('6')
        expectThat(i.next()).isEqualTo('7')
        expectThat(i).isFinished()
    }

    @Test
    fun testReverse() {
        val x1 = FlatCharSequenceRope("012345")
        val x2 = FlatCharSequenceRope("67")
        val x3 = ConcatenationRope(x1, x2)

        expectThat(x1.reverse()).isString("543210")
        expectThat(x3.reverse()).isString("76543210")
        expectThat(x3.reverse().reverse().reverse()).isEqualTo(x3.reverse())
        expectThat(x3.reverse().subSequence(1, 7)).isString("654321")


        val rope: Rope = Rope.of("0123445").subSequence(1, 2)
        expectThat(rope).isString("1")
    }

    @Test
    fun testTrim() {
        val x1 = FlatCharSequenceRope("\u0012  012345")
        val x2 = FlatCharSequenceRope("\u0002 67	       \u0007")
        val x3 = ConcatenationRope(x1, x2)

        expectThat(x1.trimStart()).isString("012345")
        expectThat(x2.trimStart()).isString("67	       \u0007")
        expectThat(x3.trimStart()).isString("012345\u0002 67	       \u0007")

        expectThat(x1.trimEnd()).isString("\u0012  012345")
        expectThat(x2.trimEnd()).isString("\u0002 67")
        expectThat(x3.trimEnd()).isString("\u0012  012345\u0002 67")
        expectThat(x3.trimEnd().reverse().trimEnd().reverse()).isString("012345\u0002 67")

        expectThat(x3.trimEnd().trimStart()).isEqualTo(x3.trimStart().trimEnd())
        expectThat(x3.trimStart().reverse().trimStart().reverse()).isEqualTo(x3.trimStart().trimEnd())
        expectThat(x3.trim()).isEqualTo(x3.trimStart().trimEnd())
    }

    @Test
    fun testCreation() {
        try {
            val _ = Rope.of("The quick brown fox jumped over")
        } catch (e: Exception) {
            Assertions.fail<Any>("Nonempty string: " + e.message)
        }
        try {
            val _ = Rope.of("")
        } catch (e: Exception) {
            Assertions.fail<Any>("Empty string: " + e.message)
        }
    }

    @Test
    fun testEquals() {
        val r1 = Rope.of("alpha")
        val r2 = Rope.of("beta")
        val r3 = Rope.of("alpha")

        expectThat(r3).isEqualTo(r1)
        Assertions.assertFalse(r1 == r2)
    }

    @Test
    fun testHashCode() {
        val r1 = Rope.of("alpha")
        val r2 = Rope.of("beta")
        val r3 = Rope.of("alpha")

        expectThat(r3.hashCode()).isEqualTo(r1.hashCode())
        Assertions.assertFalse(r1.hashCode() == r2.hashCode())
    }

    @Test
    fun testHashCode2() {
        val r1: Rope = FlatCharSequenceRope(StringBuffer("The quick brown fox."))
        val r2: Rope = ConcatenationRope(FlatCharSequenceRope(""), FlatCharSequenceRope("The quick brown fox."))

        Assertions.assertTrue(r1 == r2)
        Assertions.assertTrue(r1 == r2)
    }

    @Test
    fun testIndexOf() {
        val r1 = Rope.of("alpha")
        val r2 = Rope.of("beta")
        val r3 = r1.append(r2)
        expectThat(r3.indexOf('l')).isEqualTo(1)
        expectThat(r3.indexOf('e')).isEqualTo(6)


        var r = Rope.of("abcdef")
        expectThat(r.indexOf('z')).isEqualTo(-1)
        expectThat(r.indexOf('a')).isEqualTo(0)
        expectThat(r.indexOf('b')).isEqualTo(1)
        expectThat(r.indexOf('f')).isEqualTo(5)


        expectThat(r.indexOf('b', 0)).isEqualTo(1)
        expectThat(r.indexOf('a', 0)).isEqualTo(0)
        expectThat(r.indexOf('z', 0)).isEqualTo(-1)
        expectThat(r.indexOf('b', 2)).isEqualTo(-1)
        expectThat(r.indexOf('f', 5)).isEqualTo(5)

        expectThat(r.indexOf("cd", 1)).isEqualTo(2)

        r = Rope.of("The quick brown fox jumped over the jumpy brown dog.")
        expectThat(r.indexOf("The")).isEqualTo(0)
        expectThat(r.indexOf("brown")).isEqualTo(10)
        expectThat(r.indexOf("brown", 10)).isEqualTo(10)
        expectThat(r.indexOf("brown", 11)).isEqualTo(42)
        expectThat(r.indexOf("brown", 43)).isEqualTo(-1)
        expectThat(r.indexOf("hhe")).isEqualTo(-1)

        r = Rope.of("zbbzzz")
        expectThat(r.indexOf("ab", 1)).isEqualTo(-1)
    }

    @Test
    fun testInsert() {
        val r1 = Rope.of("alpha")
        expectThat(r1.insert(0, "beta")).isString("betaalpha")
        expectThat(r1.insert(r1.length, "beta")).isString("alphabeta")
        expectThat(r1.insert(1, "beta")).isString("abetalpha")
    }

    @Test
    fun testPrepend() {
        var r1 = Rope.of("alphabeta")
        for (j in 0..1) r1 = r1.subSequence(0, 5).append(r1)
        expectThat(r1).isString("alphaalphaalphabeta")
        r1 = r1.append(r1.subSequence(5, 15))
        expectThat(r1).isString("alphaalphaalphabetaalphaalpha")
    }

    @Test
    fun testCompareTo() {
        val r1 = Rope.of("alpha")
        val r2 = Rope.of("beta")
        val r3 = Rope.of("alpha")
        val r4 = Rope.of("alpha1")
        val s2 = "beta"

        Assertions.assertTrue(r1.compareTo(r3) == 0)
        Assertions.assertTrue(r1.compareTo(r2) < 0)
        Assertions.assertTrue(r2.compareTo(r1) > 0)
        Assertions.assertTrue(r1.compareTo(r4) < 0)
        Assertions.assertTrue(r4.compareTo(r1) > 0)
        Assertions.assertTrue(r1.compareTo(s2) < 0)
        Assertions.assertTrue(r2.compareTo(s2) == 0)
    }

    @Test
    fun testToString() {
        val phrase = "The quick brown fox jumped over the lazy brown dog. Boy am I glad the dog was asleep."
        val r1 = Rope.of(phrase)
        Assertions.assertTrue(phrase == r1.toString())
        Assertions.assertTrue(phrase.subSequence(7, 27) == r1.subSequence(7, 27).toString())
    }

    @Test
    fun reverse() {
        val orig = "hello this is some sort of string"
        val r = Rope.of(orig)
        val rev = orig.reversed()

        val pairs = r.reverseIterator().asSequence().zip(rev.iterator().asSequence())

        for (pair in pairs) {
            expectThat(pair.first).isEqualTo(pair.second)
        }
    }

    @Test
    fun testReverseIterator() {
        val r1 = FlatCharSequenceRope("01234")
        val r2 = ReverseRope(r1)
        val r3 = SubstringRope(r1, 0, 3)
        val r4 = ConcatenationRope(ConcatenationRope(r1, r2), r3) //0123443210012

        var x = r1.reverseIterator()
        expectThat(x.next()).isEqualTo('4')
        expectThat(x.next()).isEqualTo('3')
        expectThat(x.next()).isEqualTo('2')
        expectThat(x.next()).isEqualTo('1')
        expectThat(x.next()).isEqualTo('0')
        expectThat(x).isFinished()

        x = r1.reverseIterator(4)
        expectThat(x.next()).isEqualTo('0')
        expectThat(x).isFinished()

        x = r2.reverseIterator()
        expectThat(x.next()).isEqualTo('0')
        expectThat(x.next()).isEqualTo('1')
        expectThat(x.next()).isEqualTo('2')
        expectThat(x.next()).isEqualTo('3')
        expectThat(x.next()).isEqualTo('4')
        expectThat(x).isFinished()

        x = r2.reverseIterator(4)
        expectThat(x.next()).isEqualTo('4')
        expectThat(x).isFinished()

        x = r3.reverseIterator()
        expectThat(x.next()).isEqualTo('2')
        expectThat(x.next()).isEqualTo('1')
        expectThat(x.next()).isEqualTo('0')
        expectThat(x).isFinished()

        x = r3.reverseIterator(1)
        expectThat(x.next()).isEqualTo('1')
        expectThat(x.next()).isEqualTo('0')
        expectThat(x).isFinished()

        x = r4.reverseIterator() //0123443210012
        expectThat(x.next()).isEqualTo('2')
        expectThat(x.next()).isEqualTo('1')
        expectThat(x.next()).isEqualTo('0')
        expectThat(x.next()).isEqualTo('0')
        expectThat(x.next()).isEqualTo('1')
        expectThat(x.next()).isEqualTo('2')
        expectThat(x.next()).isEqualTo('3')
        expectThat(x.next()).isEqualTo('4')
        expectThat(x.next()).isEqualTo('4')
        expectThat(x.next()).isEqualTo('3')
        expectThat(x.next()).isEqualTo('2')
        expectThat(x.next()).isEqualTo('1')
        expectThat(x.next()).isEqualTo('0')
        expectThat(x).isFinished()

        x = r4.reverseIterator(7)
        expectThat(x.next()).isEqualTo('4')
        expectThat(x.next()).isEqualTo('4')
        expectThat(x.next()).isEqualTo('3')
        expectThat(x.next()).isEqualTo('2')
        expectThat(x.next()).isEqualTo('1')
        expectThat(x.next()).isEqualTo('0')
        expectThat(x).isFinished()

        x = r4.reverseIterator(12)
        expectThat(x.next()).isEqualTo('0')
        expectThat(x).isFinished()

        x = r4.reverseIterator(13)
        expectThat(x).isFinished()
    }

    @Test
    fun testSerialize() {
        val r1 = FlatCharSequenceRope("01234")
        val r2 = ReverseRope(r1)
        val r3 = SubstringRope(r1, 0, 1)
        val r4 = ConcatenationRope(ConcatenationRope(r1, r2), r3) //01234432100

        val out = ByteArrayOutputStream()
        ObjectOutputStream(out).use {
            it.writeObject(r4)
        }
        val `in` = ByteArrayInputStream(out.toByteArray())
        val ois = ObjectInputStream(`in`)
        val r = ois.readObject()
        expectThat(r).isA<FlatCharSequenceRope>()
    }

    @Test
    fun testPadStart() {
        val r = Rope.of("hello")
        expectThat(r.padStart(5)).isString("hello")
        expectThat(r.padStart(0)).isString("hello")
        expectThat(r.padStart(-1)).isString("hello")
        expectThat(r.padStart(6)).isString(" hello")
        expectThat(r.padStart(7)).isString("  hello")
        expectThat(r.padStart(6, '~')).isString("~hello")
        expectThat(r.padStart(7, '~')).isString("~~hello")
        expectThat(r.padStart(30, '~')).isString("~~~~~~~~~~~~~~~~~~~~~~~~~hello")
    }

    @Test
    fun testPadEnd() {
        val r = Rope.of("hello")
        expectThat(r.padEnd(5)).isString("hello")
        expectThat(r.padEnd(0)).isString("hello")
        expectThat(r.padEnd(-1)).isString("hello")
        expectThat(r.padEnd(6)).isString("hello ")
        expectThat(r.padEnd(7)).isString("hello  ")
        expectThat(r.padEnd(6, '~')).isString("hello~")
        expectThat(r.padEnd(7, '~')).isString("hello~~")
        expectThat(r.padEnd(30, '~')).isString("hello~~~~~~~~~~~~~~~~~~~~~~~~~")
    }

    @Test
    fun testSubstringBounds() {
        val r =
            Rope.ofCopy("01234567890123456789012345678901234567890123456789012345678901234567890123456789".toCharArray())
        val r2 = r.subSequence(0, 30)
        try {
            val _ = r2[31]
            Assertions.fail<Any>("Expected IndexOutOfBoundsException")
        } catch (e: IndexOutOfBoundsException) {
            // success
        }
    }

    @Test
    fun testAppend() {
        var r = Rope.of("")
        r = r.append('a')
        expectThat(r).isString("a")
        r = r.append("boy")
        expectThat(r).isString("aboy")
        r = r.append("test", 0, 4)
        expectThat(r).isString("aboytest")
    }

    @Test
    fun testEmpty() {
        val r1 = Rope.of("")
        val r2 = Rope.of("012345")

        Assertions.assertTrue(r1.isEmpty())
        Assertions.assertFalse(r2.isEmpty())
        Assertions.assertTrue(r2.subSequence(2, 2).isEmpty())
    }

    @Test
    fun testCharAt() {
        val r1 = FlatCharSequenceRope("0123456789")
        val r2 = SubstringRope(r1, 0, 1)
        val r3 = SubstringRope(r1, 9, 1)
        val r4 = ConcatenationRope(r1, r3)

        expectThat(r1[0]).isEqualTo('0')
        expectThat(r1[9]).isEqualTo('9')
        expectThat(r2[0]).isEqualTo('0')
        expectThat(r3[0]).isEqualTo('9')
        expectThat(r4[0]).isEqualTo('0')
        expectThat(r4[9]).isEqualTo('9')
        expectThat(r4[10]).isEqualTo('9')
    }

    @Test
    fun testRegexp() {
        val r = ConcatenationRope(FlatCharSequenceRope("012345"), FlatCharSequenceRope("6789"))
        var c = r.getForSequentialAccess()
        for (j in 0..9) {
            expectThat(c[j]).isEqualTo(r[j])
        }
        c = r.getForSequentialAccess()

        val indices = intArrayOf(1, 2, 1, 3, 5, 0, 6, 7, 8, 1, 7, 7, 7)
        for (i in indices) {
            Assertions.assertEquals(r[i], c[i], "Index: $i")
        }
    }

    @Test
    fun testStartsEndsWith() {
        val r = Rope.of("Hello sir, how do you do?")
        Assertions.assertTrue(r.startsWith(""))
        Assertions.assertTrue(r.startsWith("H"))
        Assertions.assertTrue(r.startsWith("He"))
        Assertions.assertTrue(r.startsWith("Hello "))
        Assertions.assertTrue(r.startsWith("", 0))
        Assertions.assertTrue(r.startsWith("H", 0))
        Assertions.assertTrue(r.startsWith("He", 0))
        Assertions.assertTrue(r.startsWith("Hello ", 0))
        Assertions.assertTrue(r.startsWith("", 1))
        Assertions.assertTrue(r.startsWith("e", 1))
        Assertions.assertTrue(r.endsWith("?"))
        Assertions.assertTrue(r.endsWith("do?"))
        Assertions.assertTrue(r.endsWith("o", 1))
        Assertions.assertTrue(r.endsWith("you do", 1))
    }

    @Test
    fun `index of same index as string 1 flat char sequence`() {
        val s = "CCCCCCPIFPCFFP"
        val r = Rope.of(s)

        expectThat(r).isA<FlatCharSequenceRope>()
        val find = "IFPCFFP"

        expectThat(r.indexOf(find)).isEqualTo(s.indexOf(find))
    }

    @Test
    fun `index of same index as string 1 flat char array`() {
        val s = "CCCCCCPIFPCFFP"
        val r = Rope.ofCopy(s.toCharArray())

        expectThat(r).isA<FlatCharArrayRope>()
        val find = "IFPCFFP"

        expectThat(r.indexOf(find)).isEqualTo(s.indexOf(find))
    }

    @Test
    fun `index of same as string 2 flat char sequence`() {
        val s = "ABABAABBABABBAAABBBAAABABABABBBBAA"
        val r = Rope.of(s)

        expectThat(r).isA<FlatCharSequenceRope>()
        val find = "ABABAB"

        expectThat(r.indexOf(find)).isEqualTo(s.indexOf(find))
    }

    @Test
    fun `index of same as string 2 flat char array`() {
        val s = "ABABAABBABABBAAABBBAAABABABABBBBAA"
        val r = Rope.ofCopy(s.toCharArray())

        expectThat(r).isA<FlatCharArrayRope>()
        val find = "ABABAB"

        expectThat(r.indexOf(find)).isEqualTo(s.indexOf(find))
    }

    @Test
    fun `adding ropes`() {
        val r1 = Rope.of("1")
        val r2 = Rope.of("2")
        expectThat(r1 + r2).isEqualTo(r1.append(r2))
    }

    @Test
    fun `iterating throws right kind of exceptions`() {
        val s = "A"
        val rs = Rope.of(s)
        val rca = Rope.ofCopy(s.toCharArray())
        val cat = rs.append(rca)


        expectThrows<NoSuchElementException> {
            rs.iterator().also {
                val _ = it.next()
                val _ = it.next()
            }
        }
        expectThrows<NoSuchElementException> {
            rca.iterator().also {
                val _ = it.next()
                val _ = it.next()
            }
        }
        expectThrows<NoSuchElementException> {
            cat.iterator().also {
                val _ = it.next()
                val _ = it.next()
                val _ = it.next()
            }
        }
    }

    @Test
    fun `utf-8`() {
        val text = File("test-files/w3c-utf-8-test.txt").readText()

        val sb = StringBuilder(text)
        val rcs = Rope.of(text)
        val rca = Rope.ofCopy(text.toCharArray())

        expectThat(text.length) {
            isEqualTo(sb.length)
            isEqualTo(rcs.length)
            isEqualTo(rca.length)
        }

        val cat = rcs + rcs + rca
        expectThat(cat).isA<ConcatenationRope>()
        expectThat(cat.length).isEqualTo(text.length * 3)
    }

    @Test
    fun `times and repeat`() {
        val r = Rope.of("HI")

        expectThat(r * 0).isEqualTo(Rope.of(""))
        expectThat(r * 1).isEqualTo(Rope.of("HI"))
        expectThat(r * 2).isEqualTo(Rope.of("HIHI"))
        expectThat(r * 3).isEqualTo(Rope.of("HIHIHI"))
        expectThat(r.repeat(3)).isEqualTo(r * 3)
    }

    @Test
    fun `times small`() {
        expectThat(Rope.of("") * 10).isEqualTo(Rope.of(""))
        expectThat(Rope.of("A") * 0).isEqualTo(Rope.of(""))
        expectThat(Rope.of("A") * 1).isEqualTo(Rope.of("A"))
        expectThat(Rope.of("A") * 4).isEqualTo(Rope.of("AAAA"))
    }
}
