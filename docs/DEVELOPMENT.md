# Development

## How to make a release

Ensure CHANGELOG.md documents the changes going into the new release.

Run:

    ```
    ./release.sh <version>
    ```

Use *four-digit* semver for version numbers.

The GitHub Actions workflow will publish the release to Maven Central.
