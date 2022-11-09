import Kars.KarType.*

data class Kars(val kar: Char) {

    enum class KarType() {
        ETX,
        LF,
        TAB,
        BLANK,
        HASHTAG,
        PAR_LEFT,
        PAR_RIGHT,
        TIMES,
        PLUS,
        KAR,
        CR,
        OTHER,
        ADF_END
    }

    enum class SymType {
        COMMENT,
        VAR,
        OPERATOR,
        PAIR_START,
        PAIR_END,
        EOT,
        NONE
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
            kartyp[33] = ADF_END
            kartyp[35] = HASHTAG
            kartyp[40] = PAR_LEFT
            kartyp[41] = PAR_RIGHT
            kartyp[42] = TIMES
            kartyp[43] = PLUS
        }

        fun kartyp(char: Char): KarType {
            return kartyp[char.code]
        }
    }
}