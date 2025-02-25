# Fs4k - Super-simple file tree manipulation DSL in Kotlin.

<a href="https://mvnrepository.com/artifact/dev.forkhandles"><img alt="Download" src="https://img.shields.io/maven-central/v/dev.forkhandles/forkhandles-bom"></a>
[![.github/workflows/build.yaml](https://github.com/fork-handles/forkhandles/actions/workflows/build.yaml/badge.svg)](https://github.com/fork-handles/forkhandles/actions/workflows/build.yaml)

<a href="http//www.apache.org/licenses/LICENSE-2.0"><img alt="GitHub license" src="https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat"></a>
<a href="https://codebeat.co/projects/github-com-fork-handles-forkhandles-trunk"><img alt="codebeat badge" src="https://codebeat.co/badges/5b369ed4-af27-46f4-ad9c-a307d900617e"></a>

File system manipulation in Kotlin with a pluggable file system abstraction. You can set the content of files and directories, and the file system to use.

To create a file system (automatically):

```kotlin
val directory = dir("path", Automatic) {
    text("plainfile.txt") {
        content = "hello"
    }
    binary("binary.png") {
        content = "goodbye".byteInputStream()
    }

    dir("directory") {
        text("file2.html") {
            content = "<html/>"
        }
    }
}

// directory is created automatically
directory.delete()
```

You can manipulate files in a nested way to only create/delete the parts you need:

```kotlin
dir("parent").dir("child").text("file.txt").delete()
```

### Supported FS types:

- Disk
