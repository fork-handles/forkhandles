# Tuples4k

Tuples with up to (currently) eight elements, and convenient operations for working with them.

Tuple2 and Tuple3 are typealiases for Pair and Triple respectively.  Other tuple types are data classes with elements named `val1`, `val2`, etc.

Tuples are constructed with the `tuple` function.

## Operations

`A + TupleN<X,Y,...> : TupleM<A,X,Y,...>` – add an element at the front of a tuple

`TupleN<A,B,...> + X : TupleM<A,B,...X>` – add an element at the end of a tuple

`TupleN<A,B,...> + TupleM<X,Y,...> : TupleP<A,B,...,X,Y,...>` – append two tuples

`TupleN<T,T,...>.toList(): List<T>` – Convert a tuple to a list

`TupleN<T1?,T2?,...>.allNonNull(): TupleN<T1,T2,...>?` – convert a tuple with nullable elements to a nullable tuple with non-nullable elements
