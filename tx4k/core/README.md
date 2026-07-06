# Tx4k – Typesafe, programmatic transaction boundaries

## Concepts

_Transactor_ : an object that orchestrates transactions.  It creates a transactional resource, starts a transaction, wraps the transactional resource in an abstraction defined by the application, passes the abstraction to the application for the application to perform the transaction, and commits the transaction when the application is done or rolls back the transaction if an exception is thrown.

_Resource_ : an object that can be used in a transaction.  E.g. a JDBC connection, or an in-memory implementation of serialisable transactions.

_API_ : the abstraction that the application wraps around the resource, and is used by the application code that implements transactions.

