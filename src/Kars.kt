import Kars.KarType.*

data class Kars(val kar: Char) {
    enum class KarType(val pp: String) {
        ETX("ETX"),
        LF("LF"),
        TAB("TAB"),
        BLANK("BLANK"),
        HASHT("HASHT"),
        PAR_L("PAR_L"),
        PAR_R("PAR_R"),
        TIMES("*"),
        DIV("/"),
        PLUS("+"),
        MINUS("-"),
        EQ("="),
        GT(">"),
        LT("<"),
        COLON(":"),
        QUEST("?"),   // we ignore this operator, becase ':' takes care of ternary, else QUESTION("[?]"),
        LETT("L"),
        DIGIT("D"),
        CR("CR"),
        OTHER("OTHER"),
        EXCLA("!END!"),
        TEST("TEST")
    }

    enum class SymType {
        // https://en.cppreference.com/w/cpp/language/operator_precedence#cite_note-2
        // of https://en.wikipedia.org/wiki/Order_of_operations
        COMMENT,
        VARI,
        LIT,            //literal
        OP_3,           // CHS "change sign "
        OP_5,           // * or /, according to precedence order
        OP_6,           // + or -
        OP_16,          // like elvis
        ELV_Q,
        ELV_C,
        BEXPS,
        BEXPE,
        EOT,
        NONE;

        companion object {         //https://itecnote.com/tecnote/kotlin-how-to-create-an-enum-from-an-int-in-kotlin/
            fun fromInt(value: Int) = SymType.values()
        }
    }

    enum class Category { SEPARATOR, SKIP, NOTHING }
    data class Symbol(var typ: SymType = SymType.NONE,
                      var content: String,
                      var cursor: Int)

    companion object {
        val expressionSize = 64
        var kartyp = Array(256) { OTHER }

        init {
            for (i in 48..57) kartyp[i] = DIGIT
            for (i in 65..90) kartyp[i] = LETT
            for (i in 97..122) kartyp[i] = LETT
            kartyp[95] = LETT
            kartyp[0] = ETX
            kartyp[10] = LF
            kartyp[11] = TAB
            kartyp[13] = CR
            kartyp[32] = BLANK
            kartyp[33] = EXCLA
            kartyp[35] = HASHT
            kartyp[40] = PAR_L
            kartyp[41] = PAR_R
            kartyp[42] = TIMES
            kartyp[43] = PLUS
            kartyp[45] = MINUS
            kartyp[47] = DIV
            kartyp[58] = COLON
            kartyp[60] = LT
            kartyp[61] = EQ
            kartyp[62] = GT
            kartyp[63] = QUEST
        }

        fun isa(sym: Symbol, vararg op: SymType): Boolean {
            return (sym.typ in op)
        }

        fun isaC(char: Char, vararg op: KarType): Boolean {
            return (kartyp[char.code] in op)
        }
    }
}