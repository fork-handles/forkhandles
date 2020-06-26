package dev.forkhandles.bunting

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.jupiter.api.Test
import java.lang.IllegalArgumentException
import java.util.UUID
import java.util.concurrent.atomic.AtomicReference

class BuntingTest {
    private val output = Output()

    enum class AnEnum {
        a, b
    }

    class MyTestFlags(args: Array<String>) : Bunting(args) {
        val required by requiredFlag("This is a required option")
        val defaulted by defaultedFlag("0.0.0", "This is a defaulted option")
        val mapped by requiredFlag("This is a mapped option").map { it.toInt() }
        val anEnum by requiredFlag("This is an Enum").enum<AnEnum>()
    }

    @Test
    fun `required option is parsed`() {
        MyTestFlags(arrayOf("--required", "foo")).use(output) {
            assertThat(required, equalTo("foo"))
        }
        assertThat(output.toString(), equalTo(""))
    }

    @Test
    fun `missing required option blows`() {
        MyTestFlags(arrayOf()).use(output) {
            required
        }
        assertThat(output.toString(), equalTo("Usage: <name> [OPTIONS]\nNMissing --required option"))
    }

    @Test
    fun `missing defaulted option is defaulted`() {
        MyTestFlags(arrayOf()).use(output) {
            assertThat(defaulted, equalTo("0.0.0"))
        }
        assertThat(output.toString(), equalTo(""))
    }

    @Test
    fun `passing short option`() {
        MyTestFlags(arrayOf("-r", "foo")).use(output) {
            assertThat(required, equalTo("foo"))
        }
        assertThat(output.toString(), equalTo(""))
    }

    @Test
    fun `passing uneven number of fields`() {
        MyTestFlags(arrayOf("--required", "foo", "other")).use(output) {
            assertThat(required, equalTo("foo"))
        }
        assertThat(output.toString(), equalTo(""))
    }

    @Test
    fun `mapped option`() {
        MyTestFlags(arrayOf("--mapped", "123")).use(output) {
            assertThat(mapped, equalTo(123))
        }
        assertThat(output.toString(), equalTo(""))
    }

    @Test
    fun `illegal value option`() {
        MyTestFlags(arrayOf("--mapped", "asd")).use(output) {
            mapped
        }
        assertThat(output.toString(), equalTo("Usage: <name> [OPTIONS]\nIllegal --mapped option: asd"))
    }

    @Test
    fun `asking for help`() {
        assertHelpText(arrayOf("--help"))
        assertHelpText(arrayOf("-h"))
    }

    private fun assertHelpText(strings: Array<String>) {
        MyTestFlags(strings).use(output) {
            throw IllegalArgumentException()
        }
        assertThat(output.toString(), equalTo("Usage: <name> [OPTIONS]\n" +
            "Options:\n" +
            "\t-a, --anEnum\t\tThis is an Enum. Option choice: [a, b]\n" +
            "\t-d, --defaulted\t\tThis is a defaulted option\n" +
            "\t-m, --mapped\t\tThis is a mapped option\n" +
            "\t-r, --required\t\tThis is a required option\n" +
            "    -h, --help          Show this message and exit"))
    }

    @Test
    fun extensions() {
        class ExtensionFlags(args: Array<String>) : Bunting(args) {
            val int by requiredFlag().int()
            val float by requiredFlag().float()
            val char by requiredFlag().char()
            val long by requiredFlag().long()
            val boolean by requiredFlag().boolean()
            val uuid by requiredFlag().uuid()
            val anEnum by requiredFlag().enum<AnEnum>()
        }

        ExtensionFlags(
            arrayOf(
                "--int", "123",
                "--char", "t",
                "--float", "1.23",
                "--long", "123",
                "--boolean", "true",
                "--anEnum", "a",
                "--uuid", "00000000-0000-0000-0000-000000000000"
            )
        ).use(output) {
            assertThat(int, equalTo(123))
            assertThat(char, equalTo('t'))
            assertThat(long, equalTo(123L))
            assertThat(float, equalTo(1.23F))
            assertThat(boolean, equalTo(true))
            assertThat(anEnum, equalTo(AnEnum.a))
            assertThat(uuid, equalTo(UUID(0,0)))
        }
        assertThat(output.toString(), equalTo(""))
    }
}

private class Output : (String) -> Unit {
    private val captured = AtomicReference<String>(null)

    override fun invoke(p1: String) = captured.set(p1)

    override fun toString() = captured.get()?.toString() ?: ""
}
