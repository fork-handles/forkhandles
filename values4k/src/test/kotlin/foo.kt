import java.io.File

fun main() {

    File("/Users/david/dev/http4k/http4k/versions.properties").readLines().forEach {
        println(it)
    }
}
