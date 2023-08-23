package dev.forkhandles.mock4k

import com.natpryce.hamkrest.absent
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import dev.forkhandles.mock4k.MockMode.Relaxed
import dev.forkhandles.mock4k.MockMode.Strict
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail

interface Wallet {
    fun pay(item: String, coins: Int): Int?
}

class AppleStore(private val wallet: Wallet) {
    fun buyMacBook() = wallet.pay("MacBook", 9999)
    suspend fun seeGenius(): String = "sorted"
    fun buyMacbookUsing(fn: (String, Int) -> Int?) = fn("MacBook", 9999)
    suspend fun buyMacbookLater() = wallet.pay("MacBook", 9999)
}


class MockTest {
    @Test
    fun `mock call is strict by default`() {
        val appleStore = AppleStore(object : Wallet by mock() {
            override fun pay(item: String, coins: Int): Int {
                assertThat(item, equalTo("MacBook"))
                assertThat(coins, equalTo(9999))
                return 3
            }
        })
        assertThat(appleStore.buyMacBook(), equalTo(3))
    }

    @Test
    fun `strict mock call`() {
        val appleStore = AppleStore(object : Wallet by mock(Strict) {
            override fun pay(item: String, coins: Int): Int {
                assertThat(item, equalTo("MacBook"))
                assertThat(coins, equalTo(9999))
                return 3
            }
        })
        assertThat(appleStore.buyMacBook(), equalTo(3))
    }

    @Test
    fun `relaxed mock call returns null`() {
        val appleStore = AppleStore(mock(Relaxed))
        assertThat(appleStore.buyMacBook(), absent())
    }

    @Test
    fun `suspend mock call supported`() {
        val appleStore = AppleStore(mock())

        runBlocking {
            assertThat(appleStore.seeGenius(), equalTo("sorted"))
        }
    }

    @Test
    fun `function mock supported`() {
        try {
            AppleStore(mock()).buyMacbookUsing(mock())
            fail("didn't throw")
        } catch (e: UnstubbedCall) {
            assertThat(e.message, equalTo("Unstubbed call: Function2.invoke(MacBook, 9999)"))
        }
    }

    @Test
    fun `fails on unexpected call`() {
        try {
            AppleStore(mock<Wallet>()).buyMacBook()
            fail("didn't throw")
        } catch (e: UnstubbedCall) {
            assertThat(e.message, equalTo("Unstubbed call: Wallet.pay(MacBook, 9999)"))
        }
    }

    @Test
    fun `suspend mock call failure`() {

        runBlocking {
            try {
                AppleStore(mock<Wallet>()).buyMacbookLater()
                fail("didn't throw")
            } catch (e: UnstubbedCall) {
                assertThat(e.message, equalTo("Unstubbed call: Wallet.pay(MacBook, 9999)"))
            }
        }
    }
}
