import Kars.KarType.*

data class Kars(val kar: Char) {

    enum class KarType(val pp: String) {
        ETX("ETX"),
        LF("LF"),
        TAB("TAB"),
        BLANK("BLANK"),
        HASHTAG("HASH"),
        PAR_LEFT("PAR_LEFT"),
        PAR_RIGHT("PAR_RIGHT"),
        TIMES("[*]"),
        DIVIDE("[/]"),
        PLUS("[+]"),
        MINUS("[-]"),
        EQ("[=]"),
        GT("[>]"),
        LT("[<]"),
        COLON("[:]"),
        QUESTION("[?]"),

        //        QUESTION(""),
        LETTER("L"),
        DIGIT("D"),
        CR("CR"),
        OTHER("OTHER"),
        EXCLAM("THE_END"),
        TEST("TEST")
    }

    enum class SymType {
        COMMENT,
        VARIABLE,
        LITERAL,            //literal
        OPERATOR_3,         // https://en.cppreference.com/w/cpp/language/operator_precedence#cite_note-2

        // of https://en.wikipedia.org/wiki/Order_of_operations
        OPERATOR_5,         // * or /, according to precedence order
        OPERATOR_6,         // + or -
        OPERATOR_16,        // like elvis
        ELVIS_Q,
        ELVIS_C,
        PAIR_START,
        PAIR_END,
        EOT,
        NONE;

        companion object {         //https://itecnote.com/tecnote/kotlin-how-to-create-an-enum-from-an-int-in-kotlin/
            fun fromInt(value: Int) = SymType.values()
        }
    }

    enum class Category { SEPARATOR, SKIP, NOTHING }
    data class Symbol(var typ: SymType = SymType.NONE, var content: String)

    companion object {
        val karname: CharArray = CharArray(256)
        var kartyp = Array(256) { OTHER }

        init {
            for (i in 48..57) kartyp[i] = DIGIT
            for (i in 65..90) kartyp[i] = LETTER
            for (i in 97..122) kartyp[i] = LETTER
            kartyp[0] = ETX
            kartyp[10] = LF
            kartyp[11] = TAB
            kartyp[13] = CR
            kartyp[32] = BLANK
            kartyp[33] = EXCLAM
            kartyp[35] = HASHTAG
            kartyp[40] = PAR_LEFT
            kartyp[41] = PAR_RIGHT
            kartyp[42] = TIMES
            kartyp[43] = PLUS
            kartyp[45] = MINUS
            kartyp[47] = DIVIDE
            kartyp[58] = COLON
            kartyp[60] = LT
            kartyp[61] = EQ
            kartyp[62] = GT
            kartyp[63] = QUESTION
        }

        fun isa(sym: Symbol, vararg op: SymType): Boolean {
            return (sym.typ in op)
        }

        fun isaC(char: Char, vararg op: KarType): Boolean {
            return (kartyp[char.code] in op)
        }
    }
}