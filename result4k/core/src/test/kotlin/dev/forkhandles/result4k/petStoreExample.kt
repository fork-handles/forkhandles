package dev.forkhandles.result4k

// define some models
typealias HumanId = Int
typealias PetId = Int
data class Pet(val id: PetId, val name: String)
data class Adoption(val humanId: HumanId, val pet: Pet)

// define some potential errors (a sealed interface works well too!)
enum class PetError { PetNotFound, PetNotForAdoption, OwnerBlacklisted }

class PetStoreExample(
    val pets: Set<Pet>,
    val blacklist: Set<HumanId>,
    val adoptions: MutableSet<Adoption>
) {
    /**
     * Return Success<Pet> if found. Otherwise, return Failure<PetError>
     */
    fun findPet(name: String): Result<Pet, PetError> = pets
        .find { it.name == name }
        .asResultOr { PetError.PetNotFound } // if find returns null, convert to a Failure

    /**
     * Return Success<Adoption> if adoption is successful.  Otherwise, return Failure<PetError>
     */
    fun adopt(humanId: HumanId, petId: PetId): Result<Adoption, PetError> {
        // perform some pre-validation; explicitly return failure if they fail
        if (humanId in blacklist) return Failure(PetError.OwnerBlacklisted)
        if (adoptions.any { it.pet.id == petId }) return Failure(PetError.PetNotForAdoption)

        return pets
            .find { it.id == petId }
            .asResultOr { PetError.PetNotFound }  // convert to failure if pet not found
            .map { pet -> Adoption(humanId, pet) } // if pet found, convert to Adoption and return
            .peek { adoption -> adoptions += adoption } // Perform a side-effect with the success value
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
 * If any of the functions fails, the subsequent ones will not execute, and the failure will be returned instead
 */
fun PetStoreExample.adoptByNameAndBrag(humanId: HumanId, name: String): Result<Adoption, PetError> = findPet(name)
    .flatMap { pet -> adopt(humanId, pet.id) }
    .peek { adoption -> brag(adoption) } // bragging returns Unit, so we use "peek" to ignore the Success value

fun main() {
    val human1 = 9
    val human2 = 8

    val pet1 = Pet(1, "Kratos")
    val pet2 = Pet(2, "Freya")
    val pet3 = Pet(3, "Snowball")

    val service = PetStoreExample(
        pets = setOf(pet1, pet2, pet3),
        blacklist = setOf(human1),
        adoptions = mutableSetOf(Adoption(human2, pet1))
    )

    // Try out findPet
    println(service.findPet("Athena")) // Failure(reason=PetError.NotFound)
    println(service.findPet("Kratos")) // Success(value=Pet(id = 1, name = Kratos))

    // Try out adopt
    println(service.adopt(human1, pet1.id)) // Failure(reason=OwnerBlacklisted)
    println(service.adopt(human2, pet1.id)) // Failure(reason=PetNotForAdoption)
    println(service.adopt(human2, pet2.id)) // Success(value=Adoption(humanId=8, pet=Pet(id=2, name=Freya)))

    // Try out adoptByNameAndBrag
    println(service.adoptByNameAndBrag(human2, "Athena")) // Failure(reason=PetNotFound)
    println(service.adoptByNameAndBrag(human2, "Kratos")) // Failure(reason=PetNotForAdoption)
    println(service.adoptByNameAndBrag(human2, "Snowball")) // I just adopted Snowball!  Success(value=Adoption(humanId=8, pet=Pet(id=3, name=Snowball)))
}
