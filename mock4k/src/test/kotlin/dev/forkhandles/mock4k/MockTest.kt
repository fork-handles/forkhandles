package dev.forkhandles.mock4k

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail

interface Wallet {
    fun pay(item: String, coins: Int): Int
}

class AppleStore(private val wallet: Wallet) {
    fun buyMacBook() = wallet.pay("MacBook", 9999)
    fun seeGenius() {}
}

class MockTest {
    @Test
    fun `mock call`() {
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
