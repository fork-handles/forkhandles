package dev.forkhandles.result4k

import dev.forkhandles.result4k.PetError.OwnerIsBanned
import dev.forkhandles.result4k.PetError.PetNotForAdoption
import dev.forkhandles.result4k.PetError.PetNotFound

// Define some models
typealias HumanId = Int
typealias PetId = Int

data class Pet(val id: PetId, val name: String)
data class Adoption(val humanId: HumanId, val pet: Pet)

// Define some potential errors (a sealed interface works well too!)
enum class PetError { PetNotFound, PetNotForAdoption, OwnerIsBanned }

class PetStoreExample(
    val pets: Set<Pet>,
    val banned: Set<HumanId>,
    val adoptions: MutableSet<Adoption>
) {
    /**
     * @return `Success<Pet>` if found. Otherwise, return `Failure<PetError>`.
     */
    fun findPet(name: String): Result<Pet, PetError> =
        pets.find { it.name == name }.asResultOr { PetNotFound }

    /**
     * @return `Success<Adoption>` if adoption is successful. Otherwise, return `Failure<PetError>`.
     */
    fun adopt(humanId: HumanId, petId: PetId): Result<Adoption, PetError> {
        if (humanId in banned) return Failure(OwnerIsBanned)
        if (adoptions.any { it.pet.id == petId }) return Failure(PetNotForAdoption)
        val pet = pets.find { it.id == petId } ?: return Failure(PetNotFound)

        val adoption = Adoption(humanId, pet)
        adoptions += adoption

        return Success(adoption)
    }

    fun brag(adoption: Adoption) {
        println("I just adopted ${adoption.pet.name}!")
    }
}

/**
 * Compose a complex function which will attempt to:
 * 1. Find a pet by name
 * 2. If successful, try to adopt the pet
 * 3. If successful, brag about it
 *
 * If any of the functions fails, the subsequent ones will not execute, and the failure will be returned instead.
 */
fun PetStoreExample.adoptByNameAndBrag(humanId: HumanId, name: String): Result<Adoption, PetError> =
    findPet(name)
        .flatMap { pet -> adopt(humanId, pet.id) }
        .peek { adoption -> brag(adoption) } // Bragging returns Unit, so we use "peek" to ignore the Success value

fun main() {
    val human1 = 9
    val human2 = 8

    val pet1 = Pet(1, "Kratos")
    val pet2 = Pet(2, "Freya")
    val pet3 = Pet(3, "Snowball")

    val service = PetStoreExample(
        pets = setOf(pet1, pet2, pet3),
        banned = setOf(human1),
        adoptions = mutableSetOf(Adoption(human2, pet1))
    )

    // Try out findPet
    println(service.findPet("Athena")) // Failure(reason=PetNotFound)
    println(service.findPet("Kratos")) // Success(value=Pet(id = 1, name = Kratos))

    // Try out adopt
    println(service.adopt(human1, pet1.id)) // Failure(reason=OwnerIsBanned)
    println(service.adopt(human2, pet1.id)) // Failure(reason=PetNotForAdoption)
    println(service.adopt(human2, pet2.id)) // Success(value=Adoption(humanId=8, pet=Pet(id=2, name=Freya)))

    // Try out adoptByNameAndBrag
    println(service.adoptByNameAndBrag(human2, "Athena")) // Failure(reason=PetNotFound)
    println(service.adoptByNameAndBrag(human2, "Kratos")) // Failure(reason=PetNotForAdoption)
    println(service.adoptByNameAndBrag(human2, "Snowball")) // I just adopted Snowball!  Success(value=Adoption(humanId=8, pet=Pet(id=3, name=Snowball)))
}
