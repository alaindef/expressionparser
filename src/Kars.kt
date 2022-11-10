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
        PLUS("[+]"),
        GT("[>]"),
        LT("[<]"),
        COLON("[:]"),
//        QUESTION("[?]"),
        QUESTION(""),
        KAR("C"),
        CR("CR"),
        OTHER("OTHER"),
        EXCLAM("THE_END")
    }

    enum class SymType {
        COMMENT,
        VAR,
        OPERATOR_F,                  //fac   <> fac
        OPERATOR_T,                  //term  <> term
        PAIR_START,
        PAIR_END,
        EOT,
        NONE;
        companion object {         //https://itecnote.com/tecnote/kotlin-how-to-create-an-enum-from-an-int-in-kotlin/
            fun fromInt(value: Int) = SymType.values() }
    }

    enum class Category { SEPARATOR, SKIP, NOTHING }
    data class Symbol(var typ: SymType = SymType.NONE, var content: String)

    companion object {
        val karname: CharArray = CharArray(256)
        var kartyp = Array(256) { OTHER }
//        val karcat: Array(256) {}

        init {
            for (i in 48..57) kartyp[i] = KAR
            for (i in 65..90) kartyp[i] = KAR
            for (i in 97..122) kartyp[i] = KAR
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
            kartyp[58] = COLON
            kartyp[60] = LT
            kartyp[62] = GT
            kartyp[63] = QUESTION
        }

        fun kartyp(char: Char): KarType {
            return kartyp[char.code]
        }
    }
}