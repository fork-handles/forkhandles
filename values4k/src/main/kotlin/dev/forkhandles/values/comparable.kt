package dev.forkhandles.values

interface ComparableValue<DOMAIN, PRIMITIVE> :
    Value<PRIMITIVE>,
    Comparable<DOMAIN>
    where
        DOMAIN : Value<PRIMITIVE>,
        PRIMITIVE : Comparable<PRIMITIVE>
{
    override fun compareTo(other: DOMAIN) = value.compareTo(other.value)
}

abstract class AbstractComparableValue<DOMAIN, PRIMITIVE>(value: PRIMITIVE) :
    AbstractValue<PRIMITIVE>(value),
    ComparableValue<DOMAIN, PRIMITIVE>
    where
        DOMAIN : Value<PRIMITIVE>,
        PRIMITIVE : Comparable<PRIMITIVE>


fun <PRIMITIVE, DOMAIN> compareByValue(): Comparator<DOMAIN>
    where
        PRIMITIVE : Comparable<PRIMITIVE>,
        DOMAIN : Value<PRIMITIVE>
    = compareByValue(naturalOrder())

fun <PRIMITIVE, DOMAIN> compareByValue(order: Comparator<PRIMITIVE>): Comparator<DOMAIN>
    where
        PRIMITIVE : Comparable<PRIMITIVE>,
        DOMAIN : Value<PRIMITIVE>
    = compareBy(comparator = order, selector = { it.value })
