package dev.forkhandles.bunting

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.jupiter.api.Test
import java.util.UUID

class BuntingTest {
    private val testIo = TestIO()

    enum class AnEnum {
        a, b
    }

    class MyGrandChildFlags(args: Array<String>, io: IO) : Bunting(args, io = io, baseCommand = "MyTestFlags")

    class MyChildFlags(args: Array<String>, io: IO) : Bunting(args, "This is a command flag", "MyTestFlags", io) {
        val noDescription by option().defaultsTo("no description default")
        val grandchild by command { MyGrandChildFlags(it, io) }
    }

    class MyTestFlags(args: Array<String>, io: IO) : Bunting(args, "some description of all my commands", "MyTestFlags", io = io) {
        val switch: Boolean by switch("This is a switch")
        val optional: String by option("This is an optional flag").required()
        val required: String by option("This is a required flag").required()
        val prompted: String by option("This is a prompted flag").prompted()
        val secret by option("This is a secret flag").int().secret().prompted()
        val defaulted: String by option("This is a defaulted flag").defaultsTo("0.0.0")
        val mapped: Int by option("This is a mapped flag").map { it.toInt() }.required()
        val anEnum: AnEnum by option().enum<AnEnum>().defaultsTo(AnEnum.b)
        val command: MyChildFlags? by command { MyChildFlags(it, this.io) }
    }

    @Test
    fun `switch is parsed`() {
        MyTestFlags(arrayOf("--switch"), testIo).use {
            assertThat(switch, equalTo(true))
        }
        assertThat(testIo.toString(), equalTo(""))
    }

    @Test
    fun `when we run out of space`() {
        class Foo(args: Array<String>, io: IO) : Bunting(args, "description", "foo", io = io) {
            val aReallyReallyReallyReallyReallyReallyReallyReallyLongName by option("some description").defaultsTo("foobar")
        }

        Foo(arrayOf("--help"), testIo).use {
            aReallyReallyReallyReallyReallyReallyReallyReallyLongName
        }
        assertThat(testIo.toString(), equalTo("""Usage: foo [commands] [options]
description
[options]:
  -a, --aReallyReallyReallyReallyReallyReallyReallyReallyLongName    some description. Defaults to "foobar" (STRING)
  -h, --help                            Show this message and exit"""))
    }

    @Test
    fun `required flag is parsed`() {
        MyTestFlags(arrayOf("--required", "foo"), testIo).use {
            assertThat(required, equalTo("foo"))
        }
        assertThat(testIo.toString(), equalTo(""))
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
        MyTestFlags(arrayOf("-p", "foobar"), testIo).use {
            assertThat(prompted, equalTo("foobar"))
        }
        assertThat(testIo.toString(), equalTo(""))
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
        MyTestFlags(arrayOf("-s", "1"), testIo).use {
            assertThat(secret, equalTo(1))
        }
        assertThat(testIo.toString(), equalTo(""))
    }

    @Test
    fun `illegal prompted secret flag does not leak value`() {
        MyTestFlags(arrayOf("-s", "foobar"), testIo).use {
            secret
        }
        assertThat(testIo.toString(), equalTo("Usage: MyTestFlags [commands] [options]\n" +
            "Illegal --secret (INT) flag: ******. Use --help for docs.")) }

    @Test
    fun `no value then required flag is parsed`() {
        MyTestFlags(arrayOf("--switch", "--required", "foo", "--switch      2", "--required2", "foo2"), testIo).use {
            assertThat(required, equalTo("foo"))
        }
        assertThat(testIo.toString(), equalTo(""))
    }

    @Test
    fun `missing required flag rejected`() {
        MyTestFlags(arrayOf(), testIo).use {
            required
        }
        assertThat(testIo.toString(), equalTo("""Usage: MyTestFlags [commands] [options]
Missing --required (STRING) flag. Use --help for docs."""))
    }

    @Test
    fun `unknown command is rejected`() {
        MyTestFlags(arrayOf("foobar"), testIo).use {
            throw IllegalArgumentException()
        }
        assertThat(testIo.toString(), equalTo("Usage: MyTestFlags [commands] [options]\n" +
            "Unknown command foobar. Use --help for docs."))
    }

    @Test
    fun `unknown subcommand is rejected`() {
        MyTestFlags(arrayOf("command", "foobar", "-n", "noDescription"), testIo).use {
            command.use {
                throw IllegalArgumentException()
            }
        }
        assertThat(testIo.toString(), equalTo("Usage: MyTestFlags [commands] [options]\n" +
            "Unknown command foobar. Use --help for docs."))
    }

    @Test
    fun `missing defaulted flag is defaulted`() {
        MyTestFlags(arrayOf(), testIo).use {
            assertThat(defaulted, equalTo("0.0.0"))
        }
        assertThat(testIo.toString(), equalTo(""))
    }

    @Test
    fun `passing short flag`() {
        MyTestFlags(arrayOf("-r", "foo"), testIo).use {
            assertThat(required, equalTo("foo"))
        }
        assertThat(testIo.toString(), equalTo(""))
    }

    @Test
    fun `passing short switch`() {
        MyTestFlags(arrayOf("-s", "-r", "foo"), testIo).use {
            assertThat(switch, equalTo(true))
        }
        assertThat(testIo.toString(), equalTo(""))
    }

    @Test
    fun `passing uneven number of fields`() {
        MyTestFlags(arrayOf("--required", "foo", "other"), testIo).use {
            assertThat(required, equalTo("foo"))
        }
        assertThat(testIo.toString(), equalTo(""))
    }

    @Test
    fun `mapped flag`() {
        MyTestFlags(arrayOf("--mapped", "123"), testIo).use {
            assertThat(mapped, equalTo(123))
        }
        assertThat(testIo.toString(), equalTo(""))
    }

    @Test
    fun `illegal value flag`() {
        MyTestFlags(arrayOf("--mapped", "asd"), testIo).use {
            mapped
        }
        assertThat(testIo.toString(), equalTo("Usage: MyTestFlags [commands] [options]\n" +
            "Illegal --mapped (INT) flag: asd. Use --help for docs."))
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

        Foo(arrayOf("command"), testIo).use {
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

        assertThat(testIo.toString(), equalTo(""))
    }

    @Test
    fun `failure in command`() {
        class Command(args: Array<String>, io: IO) : Bunting(args, "command description", "base", io = io) {
            val required by option("required").required()
        }

        class Foo(args: Array<String>, io: IO) : Bunting(args, "top description", "foo", io = io) {
            val command by command { Command(it, io) }
        }

        Foo(arrayOf("command"), testIo).use {
            command.use {
                required
            }
        }

        assertThat(testIo.toString(), equalTo("""Usage: base [commands] [options]
Missing --required (STRING) flag. Use --help for docs."""))
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
            testIo
        ).use {
            assertThat(int, equalTo(123))
            assertThat(char, equalTo('t'))
            assertThat(long, equalTo(123L))
            assertThat(float, equalTo(1.23F))
            assertThat(boolean, equalTo(true))
            assertThat(anEnum, equalTo(AnEnum.a))
            assertThat(uuid, equalTo(UUID(0, 0)))
        }
        assertThat(testIo.toString(), equalTo(""))
    }

    @Test
    fun `illegal extension blows up`() {
        class ExtensionFlags(args: Array<String>, io: IO) : Bunting(args, io = io, baseCommand = "foo") {
            val boolean by option().boolean()
        }

        ExtensionFlags(arrayOf("--boolean", "foobar"), testIo).use {
            boolean
        }
        assertThat(testIo.toString(), equalTo("""Usage: foo [commands] [options]
Illegal --boolean (BOOLEAN) flag: foobar. Use --help for docs."""))
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
  -o, --optional                        This is an optional flag (STRING)
  -p, --prompted                        This is a prompted flag (STRING)
  -r, --required                        This is a required flag (STRING)
  -s, --secret                          This is a secret flag (INT)
  -s, --switch                          This is a switch
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
