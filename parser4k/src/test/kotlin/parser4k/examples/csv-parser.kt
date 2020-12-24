package parser4k.examples

import org.junit.jupiter.api.Test
import parser4k.*
import parser4k.commonparsers.joinedWith

/**
 * Based on https://tools.ietf.org/html/rfc4180
 */
object CsvParser : OneOfExtensions {
    private val crlf = str("\r\n")
    private val textData =
        '\u0020'..'\u0021' or '\u0023'..'\u002b' or '\u002d'..'\u007e'

    private val escapedField =
        inOrder(
            str("\""),
            zeroOrMore(textData or oneOf(',', '\r', '\n') or str("\"\"")),
            str("\"")
        ).skipWrapper().map { it.joinToString("") }

    private val nonEscapedField =
        zeroOrMore(textData).map { it.joinToString("") }

    private val field = oneOf(escapedField, nonEscapedField)
    private val header: Parser<List<String>> = field.joinedWith(",")
    private val record: Parser<List<String>> = field.joinedWith(",")

    private val file: Parser<Csv> =
        inOrder(
            optional(inOrder(header, crlf).skipLast()),
            record.joinedWith(crlf),
            optional(crlf)
        ).map { (header, records, _) -> Csv(header, records) }

    fun parse(s: String) = s.parseWith(file)
}

data class Csv(val header: List<String>?, val records: List<List<String>>) {
    constructor(records: List<List<String>>) : this(null, records)
}

class CsvParserTests {
    @Test fun `empty line should return empty list`() {
        CsvParser.parse("") shouldEqual Csv(listOf(emptyList()))
    }

    @Test fun `single field should return list of one`() {
        CsvParser.parse("field") shouldEqual Csv(listOf(listOf("field")))
    }

    @Test fun `two fields should return list of both`() {
        CsvParser.parse("field0,field1") shouldEqual Csv(listOf(listOf("field0", "field1")))
    }

    @Test fun `single field with quoting should return list of one`() {
        CsvParser.parse(""""field"""") shouldEqual Csv(listOf(listOf("field")))
    }

    @Test fun `two fields with quoting should return list of both`() {
        CsvParser.parse(""""field0","field1"""") shouldEqual Csv(listOf(listOf("field0", "field1")))
    }

    @Test fun `two fields with and without quoting should return list of both`() {
        CsvParser.parse("""field0,"field1"""") shouldEqual Csv(listOf(listOf("field0", "field1")))
    }

    @Test fun `single field quoted field with comma`() {
        CsvParser.parse(""""fie,ld"""") shouldEqual Csv(listOf(listOf("fie,ld")))
    }

    @Test fun `line with one double quote`() {
        { CsvParser.parse(""""""") } shouldFailWith { it is InputIsNotConsumed }
    }

    @Test fun `line with two double quotes`() {
        CsvParser.parse("""""""") shouldEqual Csv(listOf(listOf("")))
    }

    @Test fun `line like the one in the test`() {
        CsvParser.parse("""","""") shouldEqual Csv(listOf(listOf(",")))
    }

    @Test fun `quoted field followed by unquoted`() {
        CsvParser.parse(""""field1",field2""") shouldEqual Csv(listOf(listOf("field1", "field2")))
    }

    @Test fun `comma at the end of the line`() {
        { CsvParser.parse("field1,") } shouldFailWith { it is InputIsNotConsumed }
    }
}
