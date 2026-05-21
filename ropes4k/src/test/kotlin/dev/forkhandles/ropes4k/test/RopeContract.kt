/*
 * Copyright (C) 2024 James Richardson
 *  - Originally Copyright (C) 2007 Amin Ahmad.
 * Licenced under GPL
 */

package dev.forkhandles.ropes4k.test

import dev.forkhandles.ropes4k.Rope
import dev.forkhandles.ropes4k.impl.FlatCharArrayRope
import dev.forkhandles.ropes4k.impl.FlatCharSequenceRope
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.api.expectThrows
import strikt.assertions.endsWith
import strikt.assertions.hasLength
import strikt.assertions.hasSize
import strikt.assertions.isA
import strikt.assertions.isEmpty
import strikt.assertions.isEqualTo
import strikt.assertions.isGreaterThan
import strikt.assertions.isLessThan
import strikt.assertions.isNotEmpty
import strikt.assertions.isNotEqualTo
import strikt.assertions.startsWith
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

abstract class RopeContract {

    abstract fun make(s: String): Rope

    @Test
    fun `basics`() {
        expectThat(make("")).hasLength(0)
        expectThat(make("1")).hasLength(1)
        expectThat(make("12")).hasLength(2)
        expectThat(make("123")).hasLength(3)
    }

    @Test
    fun testSubstringDeleteBug() {
        val s = "12345678902234567890"
        val sub = make(s).delete(0, 1)
        expectThat(sub.substring(0, 2)).isEqualTo("23")
        expectThat(sub.substring(0, 0)).isEqualTo("")
        expectThat(sub.substring(7, 10)).isEqualTo("902")
    }

    @Test
    fun testRopeWriteBug() {
        val r = make("")
            .append("round ")
            .append(0.toString())
            .append(" 1234567890")

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
        val r = (2..30).fold(make("01")) { acc, i -> acc.append(acc) }
        expectThat(r.length).isEqualTo(1073741824)
        
        try {
            val _ = r.append(r)
            Assertions.fail<Any>("Expected overflow.")
        } catch (_: IllegalArgumentException) {
            // this is what we expect
        }
    }

    @Test
    fun testMatches() {
        val r = make("01234567890123456789")

        Assertions.assertTrue(r.matches("0.*90.*9".toRegex()))
    }

    @Test
    fun `concatenation with flat char array rope`() {
        val r1 = make("alpha")

        val r2 = FlatCharArrayRope("beta".toCharArray())

        expectThat(r1.append(r2)).isString("alphabeta")
    }

    @Test
    fun `concatenation with flat char sequence rope`() {
        val r1 = make("alpha")
        val r2 = FlatCharSequenceRope("beta")

        expectThat(r1.append(r2)).isString("alphabeta")
    }

    @Test
    fun `prepend with flat char array rope`() {
        val r1 = make("alpha")

        val r2 = FlatCharArrayRope("beta".toCharArray())

        expectThat(r2.append(r1)).isString("betaalpha")
    }

    @Test
    fun `prepend with flat char sequence rope`() {
        val r1 = make("alpha")

        val r2 = FlatCharSequenceRope("beta")

        expectThat(r2.append(r1)).isString("betaalpha")
    }

    @Test
    fun `join to self`() {
        val r1 = make("The quick brown fox jumped over")
        expectThat(r1.append(r1)).isString("The quick brown fox jumped overThe quick brown fox jumped over")
    }

    @Test
    fun `iterator`() {
        val s = "01234567890123456789"
        val r = make(s)
        expectThat(r.iterator().asSequence().toList()).hasSize(20)
        expectThat(r.iterator().asSequence().toList()).isEqualTo(s.toList())
    }

    @Test
    fun `iterator with offset`() {
        val s = "01234567890123456789"
        val r = make(s)
        expectThat(r.iterator(5).asSequence().toList()).hasSize(15)
        expectThat(r.iterator(5).asSequence().toList()).isEqualTo(s.toList().drop(5))
    }

    @Test
    fun `reverse iterator`() {
        val s = "01234567890123456789"
        val r = make(s)
        expectThat(r.reverseIterator().asSequence().toList()).hasSize(20)
        expectThat(r.reverseIterator().asSequence().toList()).isEqualTo(s.reversed().toList())
    }

    @Test
    fun `reverse iterator with offset`() {
        val s = "01234567890123456789"
        val r = make(s)
        expectThat(r.reverseIterator(5).asSequence().toList()).hasSize(15)
        expectThat(r.reverseIterator(5).asSequence().toList()).isEqualTo(s.reversed().toList().drop(5))
    }

    @Test
    fun `reverse`() {
        val r = make("0123456789")
        expectThat(r).isString("0123456789")
        expectThat(r.reverse()).isString("9876543210")
    }

    @Test
    fun `trim`() {
        val r = make("\u0012  012345 \u0002 ")
        expectThat(r.trim()).isString("012345")
        expectThat(r.trimStart()).isString("012345 \u0002 ")
        expectThat(r.trimEnd()).isString("\u0012  012345")
    }

    @Test
    fun `equals flat char sequence`() {
        val r = make("alpha")
        expectThat(r).isEqualTo(FlatCharSequenceRope("alpha"))
        expectThat(r).isNotEqualTo(FlatCharSequenceRope("alphaxx"))
    }

    @Test
    fun `equals flat char array`() {
        val r = make("alpha")
        expectThat(r).isEqualTo(FlatCharArrayRope("alpha".toCharArray()))
        expectThat(r).isNotEqualTo(FlatCharArrayRope("alphaxx".toCharArray()))
    }

    @Test
    fun `equals concatenation`() {
        val r = make("alpha")
        expectThat(r).isEqualTo(concatenationRope("alpha"))
        expectThat(r).isNotEqualTo(concatenationRope("alphaxx"))
    }

    @Test
    fun `hashcode compatible with flat char sequence`() {
        val r = make("alpha")
        expectThat(r.hashCode()).isEqualTo(FlatCharSequenceRope("alpha").hashCode())
        expectThat(r.hashCode()).isNotEqualTo(FlatCharSequenceRope("alphaxx").hashCode())
    }

    @Test
    fun `hashcode compatible with flat char array`() {
        val r = make("alpha")
        expectThat(r.hashCode()).isEqualTo(FlatCharArrayRope("alpha".toCharArray()).hashCode())
        expectThat(r.hashCode()).isNotEqualTo(FlatCharArrayRope("alphaxx".toCharArray()).hashCode())
    }

    @Test
    fun `hashcode compatible with concatenation`() {
        val r = make("alpha")
        expectThat(r.hashCode()).isEqualTo(concatenationRope("alpha").hashCode())
        expectThat(r.hashCode()).isNotEqualTo(concatenationRope("alphaxx").hashCode())
    }

    @Test
    fun `index of char`() {
        val r = make("alphabeta")
        expectThat(r.indexOf('a')).isEqualTo(0)
        expectThat(r.indexOf('a', 1)).isEqualTo(4)
        expectThat(r.indexOf('a', 5)).isEqualTo(8)
        expectThat(r.indexOf('l')).isEqualTo(1)
        expectThat(r.indexOf('b')).isEqualTo(5)
        expectThat(r.indexOf('z')).isEqualTo(-1)
    }

    @Test
    fun `index of string`() {
        val r = make("The quick brown fox jumped over the jumpy brown dog.")
        expectThat(r.indexOf("The")).isEqualTo(0)
        expectThat(r.indexOf("brown")).isEqualTo(10)
        expectThat(r.indexOf("brown", 10)).isEqualTo(10)
        expectThat(r.indexOf("brown", 11)).isEqualTo(42)
        expectThat(r.indexOf("brown", 43)).isEqualTo(-1)
        expectThat(r.indexOf("hhe")).isEqualTo(-1)
    }

    @Test
    fun `reversed index of string`() {
        val r = make("The quick brown fox jumped over the jumpy brown dog.").reverse()
        expectThat(r.indexOf("ehT")).isEqualTo(49)
        expectThat(r.indexOf("nworb")).isEqualTo(5)
        expectThat(r.indexOf("nworb", 5)).isEqualTo(5)
        expectThat(r.indexOf("nworb", 11)).isEqualTo(37)
        expectThat(r.indexOf("nworb", 43)).isEqualTo(-1)
        expectThat(r.indexOf("ehh")).isEqualTo(-1)
    }

    @Test
    fun `index of repeated`() {
        //              0123456789012345
        val r = make("aaaazzzzzzzbbbbb")
        expectThat(r.indexOf('a')).isEqualTo(0)
        expectThat(r.indexOf('a', 1)).isEqualTo(1)

        expectThat(r.indexOf("aaz")).isEqualTo(2)
        expectThat(r.indexOf("zzz")).isEqualTo(4)
        expectThat(r.indexOf("zzb")).isEqualTo(9)
        expectThat(r.indexOf("zbb")).isEqualTo(10)
        expectThat(r.indexOf("bbb")).isEqualTo(11)
        expectThat(r.indexOf("bbb")).isEqualTo(11)
    }

    @Test
    fun `index of substrings`() {
        val s = "CCCCCCPIFPCFFP"
        val r = make(s)
        val find = "IFPCFFP"
        expectThat(r.indexOf(find)).isEqualTo(s.indexOf(find))
    }

    @Test
    fun `index of substrings again`() {
        val s = "ABABAABBABABBAAABBBAAABABABABBBBAA"
        val r = make(s)
        val find = "ABABAB"
        expectThat(r.indexOf(find)).isEqualTo(s.indexOf(find))
    }

    @Test
    fun prepending() {
        val r = make("alphabeta")

        expectThat(r + r).isString("alphabetaalphabeta")
    }

    @Test
    fun `prepending subsequences`() {
        val r = (0..1).fold(make("alphabeta")) { a, _ -> a.subSequence(0, 5) + a }
        expectThat(r).isString("alphaalphaalphabeta")

        val r2 = r.append(r.subSequence(5, 15))
        expectThat(r2).isString("alphaalphaalphabetaalphaalpha")
    }

    @Test
    fun `comparing`() {
        val r1 = make("alpha")
        val r2 = make("beta")
        val r3 = make("alpha")
        val r4 = make("alpha1")
        val s2 = "beta"

        expectThat(r1).isEqualTo(r3)

        expectThat(r1).isLessThan(r2)
        expectThat(r2).isGreaterThan(r1)

        expectThat(r1).isLessThan(r4)
        expectThat(r4).isGreaterThan(r1)

        expectThat(r2.compareTo(s2)).isEqualTo(0)
    }

    @Test
    fun `to string`() {
        val phrase = "The quick brown fox jumped over the lazy brown dog. Boy am I glad the dog was asleep."
        val r1 = make(phrase)

        expectThat(r1.toString()).isEqualTo(phrase)
        expectThat(r1.subSequence(7, 27).toString()).isEqualTo(phrase.substring(7, 27))
    }

    @Test
    fun `serializing`() {

        val start = make("0123456789")

        val out = ByteArrayOutputStream()
        ObjectOutputStream(out).use {
            it.writeObject(start)
        }
        val `in` = ByteArrayInputStream(out.toByteArray())
        val ois = ObjectInputStream(`in`)
        val r = ois.readObject()
        expectThat(r).isA<FlatCharSequenceRope>()
        expectThat(r).isEqualTo(start)
    }

    @Test
    fun `pad start`() {
        val r = make("hello")
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
    fun `pad end`() {
        val r = make("hello")
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
    fun `substring bounds`() {
        val r = make("01234567890123456789012345678901234567890123456789012345678901234567890123456789")

        expectThrows<IndexOutOfBoundsException> {
            r.subSequence(0, 100)
        }

        val r2 = r.subSequence(0, 30)
        expectThrows<IndexOutOfBoundsException> {
            r2[31]
        }
    }

    @Test
    fun `index bounds`() {
        val r = make("01234567890123456789012345678901234567890123456789012345678901234567890123456789")

        expectThrows<IndexOutOfBoundsException> {
            r[-1]
        }

        expectThrows<IndexOutOfBoundsException> {
            r[r.length]
        }
    }

    @Test
    fun `appending`() {
        val r = make("")
        expectThat(r.append('a')).isString("a")
        expectThat(r.append("a")).isString("a")
        expectThat(r.append("a").append("boy")).isString("aboy")
        expectThat(r.append("a").append("boy").append("test")).isString("aboytest")
    }

    @Test
    fun testEmpty() {
        val r1 = make("")
        val r2 = make("012345")

        expectThat(r1).isEmpty()
        expectThat(r2).isNotEmpty()

        expectThat(r2.subSequence(2, 2).isEmpty())
    }

    @Test
    fun indexing() {
        val r = make("0123456789")
        expectThat(r[0]).isEqualTo('0')
        expectThat(r[9]).isEqualTo('9')
    }


    @Test
    fun adding() {
        val r1 = make("11")
        val r2 = make("22")
        expectThat(r1 + r2).isEqualTo(r1.append(r2))
        expectThat(r1 + r2).isString("1122")
    }


    @IgnorableReturnValue
    fun <T : Rope> strikt.api.Assertion.Builder<T>.startsWith(
        expected: CharSequence,
        startIndex: Int
    ): strikt.api.Assertion.Builder<T> =
        assert("starts with $expected (at index $startIndex)", expected) {
            if (it.startsWith(expected, startIndex)) {
                pass(actual = it.drop(startIndex).take(expected.length))
            } else {
                fail(actual = it.drop(startIndex).take(expected.length))
            }
        }


    @IgnorableReturnValue
    fun <T : Rope> strikt.api.Assertion.Builder<T>.endsWith(
        expected: CharSequence,
        startIndex: Int
    ): strikt.api.Assertion.Builder<T> =
        assert("ends with $expected (at index $startIndex)", expected) {
            if (it.endsWith(expected, startIndex)) {
                pass(actual = it.drop(startIndex).take(expected.length))
            } else {
                fail(actual = it.drop(startIndex).take(expected.length))
            }
        }


    @Test
    fun testStartsEndsWith() {
        val r = make("Hello sir, how do you do?")
        expectThat(r).startsWith("")

        expectThat(r).startsWith("")
        expectThat(r).startsWith("H")
        expectThat(r).startsWith("He")
        expectThat(r).startsWith("Hello ")
        expectThat(r).startsWith("", 0)
        expectThat(r).startsWith("H", 0)
        expectThat(r).startsWith("He", 0)
        expectThat(r).startsWith("Hello ", 0)
        expectThat(r).startsWith("", 1)
        expectThat(r).startsWith("e", 1)
        expectThat(r).endsWith("?")
        expectThat(r).endsWith("do?")
        expectThat(r).endsWith("o", 1)
        expectThat(r).endsWith("you do", 1)
    }

    @Test
    fun iterating() {
        val s = make("AB")

        val iterator = s.iterator()
        expectThat(iterator.next()).isEqualTo('A')
        expectThat(iterator.next()).isEqualTo('B')

        expectThrows<NoSuchElementException> {
            iterator.next()
        }
    }

    @Test
    fun `utf-8`() {
        val text = File("test-files/w3c-utf-8-test.txt").readText()

        val r = make(text)

        expectThat(r).hasLength(text.length)
        expectThat((r * 3).length).isEqualTo(text.length * 3)
    }


    @Test
    fun `times and repeat`() {
        val r = make("HI")

        expectThat(r * 0).isEqualTo(make(""))
        expectThat(r * 1).isEqualTo(make("HI"))
        expectThat(r * 2).isEqualTo(make("HIHI"))
        expectThat(r * 3).isEqualTo(make("HIHIHI"))
        expectThat(r.repeat(3)).isEqualTo(r * 3)
    }

    @Test
    fun `times small`() {
        expectThat(make("") * 10).isEqualTo(make(""))
        expectThat(make("A") * 0).isEqualTo(make(""))
        expectThat(make("A") * 1).isEqualTo(make("A"))
        expectThat(make("A") * 4).isEqualTo(make("AAAA"))
    }
}
