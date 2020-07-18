package dev.forkhandles.bunting

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.jupiter.api.Test
import java.util.UUID

class BuntingTest {
    private val io = TestIO()

    enum class AnEnum {
        a, b
    }

    class MyGrandChildFlags(args: Array<String>, io: IO) : Bunting(args, io = io)

    class MyChildFlags(args: Array<String>, io: IO) : Bunting(args, "This is a command flag") {
        val noDescription by option().defaultsTo("no description default")
        val grandchild by command { MyGrandChildFlags(it, io) }
    }

    class MyTestFlags(args: Array<String>, io: IO) : Bunting(args, "some description of all my commands", "MyTestFlags", io = io) {
        val noValueFlag: Boolean by switch("This is a no option flag")
        val optional: String by option("This is an optional flag").required()
        val required: String by option("This is a required flag").required()
        val prompted: String by option("This is a prompted flag").prompted()
        val secret by option("This is a secret flag").int().secret().prompted()
        val defaulted: String by option("This is a defaulted flag").defaultsTo("0.0.0")
        val mapped: Int by option("This is a mapped flag").map { it.toInt() }.required()
        val anEnum: AnEnum by option().enum<AnEnum>().defaultsTo(AnEnum.b)
        val command: MyChildFlags? by command { MyChildFlags(it, io) }
    }

    @Test
    fun `no value flag is parsed`() {
        MyTestFlags(arrayOf("--noValueFlag"), io).use {
            assertThat(noValueFlag, equalTo(true))
        }
        assertThat(io.toString(), equalTo(""))
    }

    @Test
    fun `when we run out of space`() {
        class Foo(args: Array<String>, io: IO) : Bunting(args, "description", "foo", io = io) {
            val aReallyReallyReallyReallyReallyReallyReallyReallyLongName by option("some description").defaultsTo("foobar")
        }

        Foo(arrayOf("--help"), io).use {
            aReallyReallyReallyReallyReallyReallyReallyReallyLongName
        }
        assertThat(io.toString(), equalTo("""Usage: foo [commands] [options]
description
[options]:
  -a, --aReallyReallyReallyReallyReallyReallyReallyReallyLongName    some description. Defaults to "foobar" (STRING)
  -h, --help                            Show this message and exit"""))
    }

    @Test
    fun `required flag is parsed`() {
        MyTestFlags(arrayOf("--required", "foo"), io).use {
            assertThat(required, equalTo("foo"))
        }
        assertThat(io.toString(), equalTo(""))
    }

    @Test
    fun `prompted flag is prompted for`() {
        val promptedIo = TestIO("foobar")
        MyTestFlags(arrayOf(), promptedIo).use {
            assertThat(prompted, equalTo("foobar"))
        }
        assertThat(promptedIo.toString(), equalTo("Enter value for \"This is a prompted flag\": "))
    }

    @Test
    fun `prompted flag is present`() {
        MyTestFlags(arrayOf("-p", "foobar"), io).use {
            assertThat(prompted, equalTo("foobar"))
        }
        assertThat(io.toString(), equalTo(""))
    }

    @Test
    fun `prompted secret flag is prompted for`() {
        val promptedIo = TestIO("1")
        MyTestFlags(arrayOf(), promptedIo).use {
            assertThat(secret, equalTo(1))
        }
        assertThat(promptedIo.toString(), equalTo("Enter value for \"This is a secret flag\": "))
    }

    @Test
    fun `prompted secret flag is present`() {
        MyTestFlags(arrayOf("-s", "1"), io).use {
            assertThat(secret, equalTo(1))
        }
        assertThat(io.toString(), equalTo(""))
    }

    @Test
    fun `illegal prompted secret flag does not leak value`() {
        MyTestFlags(arrayOf("-s", "foobar"), io).use {
            secret
        }
        assertThat(io.toString(), equalTo("Illegal --secret (INT) flag: ******.\n$helpText")) }

    @Test
    fun `no value then required flag is parsed`() {
        MyTestFlags(arrayOf("--noValueFlag", "--required", "foo", "--noValueFlag2", "--required2", "foo2"), io).use {
            assertThat(required, equalTo("foo"))
        }
        assertThat(io.toString(), equalTo(""))
    }

    @Test
    fun `missing required flag blows`() {
        MyTestFlags(arrayOf(), io).use {
            required
        }
        assertThat(io.toString(), equalTo("Missing --required (STRING) flag.\n$helpText"))
    }

    @Test
    fun `missing defaulted flag is defaulted`() {
        MyTestFlags(arrayOf(), io).use {
            assertThat(defaulted, equalTo("0.0.0"))
        }
        assertThat(io.toString(), equalTo(""))
    }

    @Test
    fun `passing short flag`() {
        MyTestFlags(arrayOf("-r", "foo"), io).use {
            assertThat(required, equalTo("foo"))
        }
        assertThat(io.toString(), equalTo(""))
    }

    @Test
    fun `passing short switch`() {
        MyTestFlags(arrayOf("-n", "-r", "foo"), io).use {
            assertThat(noValueFlag, equalTo(true))
        }
        assertThat(io.toString(), equalTo(""))
    }

    @Test
    fun `passing uneven number of fields`() {
        MyTestFlags(arrayOf("--required", "foo", "other"), io).use {
            assertThat(required, equalTo("foo"))
        }
        assertThat(io.toString(), equalTo(""))
    }

    @Test
    fun `mapped flag`() {
        MyTestFlags(arrayOf("--mapped", "123"), io).use {
            assertThat(mapped, equalTo(123))
        }
        assertThat(io.toString(), equalTo(""))
    }

    @Test
    fun `illegal value flag`() {
        MyTestFlags(arrayOf("--mapped", "asd"), io).use {
            mapped
        }
        assertThat(io.toString(), equalTo("Illegal --mapped (INT) flag: asd.\n$helpText"))
    }

    @Test
    fun `asking for help`() {
        assertHelpText(arrayOf("--help"))
        assertHelpText(arrayOf("-h"))
    }

    @Test
    fun `commands are only run when they are passed`() {
        class GrandchildCommand(args: Array<String>, io: IO) : Bunting(args, io = io)
        class Command(args: Array<String>, io: IO) : Bunting(args, io = io) {
            val grandchild by command { GrandchildCommand(it, io) }
            val otherGrandchild by command { GrandchildCommand(it, io) }
        }

        class Foo(args: Array<String>, io: IO) : Bunting(args, "description", "foo") {
            val command by command { Command(it, io) }
            val command2 by command { Command(it, io) }
        }

        Foo(arrayOf("command"), io).use {
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

        assertThat(io.toString(), equalTo(""))
    }

    private fun assertHelpText(strings: Array<String>) {
        val io = TestIO()

        MyTestFlags(strings, io).use {
            throw IllegalArgumentException()
        }

        assertThat(io.toString(), equalTo(helpText))
    }

    @Test
    fun extensions() {
        class ExtensionFlags(args: Array<String>, io: IO) : Bunting(args, io = io) {
            val int by option().int()
            val float by option().float()
            val char by option().char()
            val long by option().long()
            val boolean by option().boolean()
            val uuid by option().uuid()
            val anEnum by option().enum<AnEnum>()
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
            ),
            io
        ).use {
            assertThat(int, equalTo(123))
            assertThat(char, equalTo('t'))
            assertThat(long, equalTo(123L))
            assertThat(float, equalTo(1.23F))
            assertThat(boolean, equalTo(true))
            assertThat(anEnum, equalTo(AnEnum.a))
            assertThat(uuid, equalTo(UUID(0, 0)))
        }
        assertThat(io.toString(), equalTo(""))
    }

    @Test
    fun `illegal extension blows up`() {
        class ExtensionFlags(args: Array<String>, io: IO) : Bunting(args, io = io, baseCommand = "foo") {
            val boolean by option().boolean()
        }

        ExtensionFlags(arrayOf("--boolean", "foobar"), io).use {
            boolean
        }
        assertThat(io.toString(), equalTo("""Illegal --boolean (BOOLEAN) flag: foobar.
Usage: foo [commands] [options]
[options]:
  -b, --boolean                         (BOOLEAN?)
  -h, --help                            Show this message and exit"""))
    }

    private val helpText = """Usage: MyTestFlags [commands] [options]
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
  -p, --prompted                        This is a prompted flag (STRING)
  -r, --required                        This is a required flag (STRING)
  -s, --secret                          This is a secret flag (INT)
  -h, --help                            Show this message and exit"""
}

private class TestIO(vararg answers: String) : IO {

    private val input = answers.toMutableList()

    private val captured = mutableListOf<String>()

    override fun read(masked: Boolean): String = input.removeAt(0)

    override fun write(message: String) {
        if (message.isNotBlank()) captured.add(message)
    }

    override fun toString() = captured.joinToString("\n")
}
