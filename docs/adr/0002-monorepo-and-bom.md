# 2. Monorepo and BOM

Date: 2020-07-01

## Status

Accepted

## Context

Do we have lots of little xxx4k libraries in their own repositories?  Or have a monorepo: one big project with each xxx4k library in a subdirectory.  

The former decouples release cadences.

The latter makes it easier to maintain a single BOM for publishing to Maven Central, perform integration testing when libraries depend on one another, and use a consistent version number across all libraries.

## Decision

We will have a monorepo.

## Consequences

Do all sub-libraries have their own version number, or is there a common version number for all libraries?
