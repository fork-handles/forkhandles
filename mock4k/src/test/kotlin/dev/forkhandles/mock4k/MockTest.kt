package dev.forkhandles.mock4k

import com.natpryce.hamkrest.absent
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import dev.forkhandles.mock4k.MockMode.Relaxed
import dev.forkhandles.mock4k.MockMode.Strict
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail

interface Wallet {
    fun pay(item: String, coins: Int): Int?
}

class AppleStore(private val wallet: Wallet) {
    fun buyMacBook() = wallet.pay("MacBook", 9999)
    fun seeGenius(): String? = if (true) "sorted" else null
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
    fun `fails on unexpected call`() {
        try {
            val appleStore = AppleStore(mock())
            appleStore.buyMacBook()
            fail("didn't throw")
        } catch (e: UnstubbedCall) {
            assertThat(e.message, equalTo("Unstubbed call: Wallet.pay(MacBook, 9999)"))
        }
    }
}
