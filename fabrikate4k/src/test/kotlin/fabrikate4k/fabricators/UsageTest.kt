package fabrikate4k.fabricators

import fabrikate4k.Fabrikate
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.util.*
import kotlin.random.Random

@Disabled
class UsageTest {
    data class Hobby(val name: String)

    data class Person(
        val firstName: String,
        val lastName: String,
        val age: Int,
        val hobbies: List<Hobby>,
        val birthdate: LocalDate
    )

    @Test
    fun `simple usage example`() {
        println(Fabrikate().random<Person>())
    }

    @Test
    fun `provide own implementation`() {
        val myRandomString: Fabricator<String> = UUID.randomUUID()::toString

        val myRandomInt: Fabricator<Int> = { Random.nextInt(20, 60) }

        val config = FabricatorConfig()
            .register(myRandomString)
            .register(myRandomInt)

        val randomPerson: Person = Fabrikate(config).random()

        println(randomPerson)
    }
}
