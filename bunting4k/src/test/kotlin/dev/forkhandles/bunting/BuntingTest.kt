package dev.forkhandles.bunting

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.jupiter.api.Test
import java.util.UUID
import java.util.concurrent.atomic.AtomicReference

class BuntingTest {

    enum class AnEnum {
        a, b
    }

    class MyGrandChildFlags(args: Array<String>) : Bunting(args, io = TestIO())

    class MyChildFlags(args: Array<String>) : Bunting(args, "This is a command flag", io = TestIO()) {
        val noDescription by option().defaultsTo("no description default")
        val grandchild by command(::MyGrandChildFlags)
    }

    class MyTestFlags(args: Array<String>) : Bunting(args, "some description of all my commands", "MyTestFlags", TestIO()) {
        val noValueFlag by switch("This is a no option flag")
        val optional by option("This is an optional flag").required()
        val required by option("This is a required flag").required()
        val defaulted: String by option("This is a defaulted flag").defaultsTo("0.0.0")
        val mapped: Int by option("This is a mapped flag").map { it.toInt() }.required()
        val anEnum by option().enum<AnEnum>().defaultsTo(AnEnum.b)
        val command by command(::MyChildFlags)
    }

    @Test
    fun `no value flag is parsed`() {
        val flags = MyTestFlags(arrayOf("--noValueFlag"))
        flags.use {
            assertThat(noValueFlag, equalTo(true))
        }
        assertThat(flags.io.toString(), equalTo(""))
    }

    @Test
    fun `when we run out of space`() {
        class Foo(args: Array<String>) : Bunting(args, "description", "foo", io = TestIO()) {
            val aReallyReallyReallyReallyReallyReallyReallyReallyLongName by option("some description").defaultsTo("foobar")
        }

        val flags = Foo(arrayOf("--help"))
        flags.use {
            aReallyReallyReallyReallyReallyReallyReallyReallyLongName
        }
        assertThat(flags.io.toString(), equalTo("""Usage: foo [commands] [options]
description
[options]:
  -a, --aReallyReallyReallyReallyReallyReallyReallyReallyLongName    some description. Defaults to "foobar" (STRING)
  -h, --help                            Show this message and exit"""))
    }

    @Test
    fun `required flag is parsed`() {
        val flags = MyTestFlags(arrayOf("--required", "foo"))
        flags.use {
            assertThat(required, equalTo("foo"))
        }
        assertThat(flags.io.toString(), equalTo(""))
    }

    @Test
    fun `no value then required flag is parsed`() {
        val flags = MyTestFlags(arrayOf("--noValueFlag", "--required", "foo", "--noValueFlag2", "--required2", "foo2"))
        flags.use {
            assertThat(required, equalTo("foo"))
        }
        assertThat(flags.io.toString(), equalTo(""))
    }

    @Test
    fun `missing required flag blows`() {
        val flags = MyTestFlags(arrayOf())
        flags.use {
            required
        }
        assertThat(flags.io.toString(), equalTo("Usage: MyTestFlags [commands] [options]\nMissing --required (STRING) flag. Use --help for docs."))
    }

    @Test
    fun `missing defaulted flag is defaulted`() {
        val flags = MyTestFlags(arrayOf())
        flags.use {
            assertThat(defaulted, equalTo("0.0.0"))
        }
        assertThat(flags.io.toString(), equalTo(""))
    }

    @Test
    fun `passing short flag`() {
        val flags = MyTestFlags(arrayOf("-r", "foo"))
        flags.use {
            assertThat(required, equalTo("foo"))
        }
        assertThat(flags.io.toString(), equalTo(""))
    }

    @Test
    fun `passing short switch`() {
        val flags = MyTestFlags(arrayOf("-n", "-r", "foo"))
        flags.use {
            assertThat(noValueFlag, equalTo(true))
        }
        assertThat(flags.io.toString(), equalTo(""))
    }

    @Test
    fun `passing uneven number of fields`() {
        val flags = MyTestFlags(arrayOf("--required", "foo", "other"))
        flags.use {
            assertThat(required, equalTo("foo"))
        }
        assertThat(flags.io.toString(), equalTo(""))
    }

    @Test
    fun `mapped flag`() {
        val flags = MyTestFlags(arrayOf("--mapped", "123"))
        flags.use {
            assertThat(mapped, equalTo(123))
        }
        assertThat(flags.io.toString(), equalTo(""))
    }

    @Test
    fun `illegal value flag`() {
        val flags = MyTestFlags(arrayOf("--mapped", "asd"))
        flags.use {
            mapped
        }
        assertThat(flags.io.toString(), equalTo("Usage: MyTestFlags [commands] [options]\nIllegal --mapped (INT) flag: asd. Use --help for docs."))
    }

    @Test
    fun `asking for help`() {
        assertHelpText(arrayOf("--help"))
        assertHelpText(arrayOf("-h"))
    }

    @Test
    fun `commands are only run when they are passed`() {
        class GrandchildCommand(args: Array<String>) : Bunting(args, io = TestIO())
        class Command(args: Array<String>) : Bunting(args, io = TestIO()) {
            val grandchild by command(::GrandchildCommand)
            val otherGrandchild by command(::GrandchildCommand)
        }

        class Foo(args: Array<String>) : Bunting(args, "description", "foo", TestIO()) {
            val command by command(::Command)
            val command2 by command(::Command)
        }

        val flags = Foo(arrayOf("command"))
        flags.use {
            command.use {
                grandchild.use {
                }
                otherGrandchild.use {
                    throw IllegalArgumentException()
                }
            }
            command2.use {
                throw IllegalArgumentException()
            }
        }

        assertThat(flags.io.toString(), equalTo(""))
    }

    private fun assertHelpText(strings: Array<String>) {
        val flags = MyTestFlags(strings)
        flags.use {
            throw IllegalArgumentException()
        }
        assertThat(flags.io.toString(), equalTo("""Usage: MyTestFlags [commands] [options]
some description of all my commands
[commands]:
  command                               
    This is a command flag
    [subcommands]:
      grandchild                        
    [options]:
      -n, --noDescription               Defaults to "no description default" (STRING)
[options]:
  -a, --anEnum                          Option choice: [a, b]. Defaults to "b" (ANENUM)
  -d, --defaulted                       This is a defaulted flag. Defaults to "0.0.0" (STRING)
  -m, --mapped                          This is a mapped flag (INT)
  -n, --noValueFlag                     This is a no option flag
  -o, --optional                        This is an optional flag (STRING)
  -r, --required                        This is a required flag (STRING)
  -h, --help                            Show this message and exit"""))
    }

    @Test
    fun extensions() {
        class ExtensionFlags(args: Array<String>) : Bunting(args, io = TestIO()) {
            val int by option().int()
            val float by option().float()
            val char by option().char()
            val long by option().long()
            val boolean by option().boolean()
            val uuid by option().uuid()
            val anEnum by option().enum<AnEnum>()
        }

        val flags = ExtensionFlags(
            arrayOf(
                "--int", "123",
                "--char", "t",
                "--float", "1.23",
                "--long", "123",
                "--boolean", "true",
                "--anEnum", "a",
                "--uuid", "00000000-0000-0000-0000-000000000000"
            )
        )
        flags.use {
            assertThat(int, equalTo(123))
            assertThat(char, equalTo('t'))
            assertThat(long, equalTo(123L))
            assertThat(float, equalTo(1.23F))
            assertThat(boolean, equalTo(true))
            assertThat(anEnum, equalTo(AnEnum.a))
            assertThat(uuid, equalTo(UUID(0, 0)))
        }
        assertThat(flags.io.toString(), equalTo(""))
    }
}

private class TestIO : IO {
    private val captured = AtomicReference<String>(null)

    override fun read(): String = TODO("Not yet implemented")

    override fun write(message: String) = captured.set(message)

    override fun toString() = captured.get()?.toString() ?: ""
}
