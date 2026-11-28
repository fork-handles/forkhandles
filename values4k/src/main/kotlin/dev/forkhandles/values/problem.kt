package dev.forkhandles.values

interface Problem {
    val constraint: Constraint
    val message: Message
    val value: String
}

data class ConstraintProblem(
    override val constraint: Constraint,
    override val message: Message,
    override val value: String
) : Problem

data class Problems(
    val causes: List<Problem>
) : Problem {
    override val constraint: Constraint = Constraint.Problems
    override val message: Message = Message(causes.joinToString("; ") { it.message.value })
    override val value: String = causes.joinToString("; ") { it.value }
}

@JvmInline
value class Message(override val value: String) : Value<String>

fun Problem.causes(): List<Problem> = when (this) {
    is Problems -> causes
    else -> listOf(this)
}

class ValidationException(
    val problems: List<Problem>,
    cause: Throwable? = null
) : IllegalArgumentException(problems.joinToString("; ") { it.message.value }, cause)
