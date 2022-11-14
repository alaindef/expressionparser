import Kars.KarType.*
import Kars.SymType.*
import Kars.Symbol
import Kars.*
import Kars.Companion.kartyp
import ExpressionParser.Companion.report
import ExpressionParser.Companion.reportln
import Kars.Companion.isa
import Kars.Companion.isaC

class Pass1 {
    companion object {
        private var symIn = Symbol(NONE, "")
        private var cursor: Int = 0
        var textIn: String = ""
        private var symLocation = 0
        private var symList = Array(256) { Symbol(EOT, "") }
        private const val errorLog: String = "logx.txt"
        private var errorsLogged: Int = 0
        private var errorsPresent: Boolean = false
        private var c: Char = '\u0000'

        fun parse(s: String): Array<Symbol> {
            clear()
            textIn = s
            cursor = 0
            errorsPresent = false
            reportln("textIn =  $textIn", 0)
            do {
                symIn = makeSymbol()
                if (isa(symIn, ELVIS_C)) {                                       // substitute '?(' for '?'
                    symList[symLocation] = Symbol(PAIR_END, PAR_RIGHT.pp)
                    symLocation++
                }
                symList[symLocation] = Symbol(symIn.typ, symIn.content)
                symLocation++
                if (isa(symIn, ELVIS_Q)) {                                       // substitute '?(' for '?'
                    symList[symLocation] = Symbol(PAIR_START, PAR_LEFT.pp)
                    symLocation++
                }
            } while (symIn.typ != EOT)

            reportln(symList,1)
            return symList
        }

        private fun makeSymbol(vararg expected: SymType): Symbol {
            if (cursor > 6) {           //for debugging
                val xx = 1
            }
            if (cursor >= textIn.length) return symIn
            c = textIn[cursor]
            if (isaC(c, BLANK, TAB)) {
                cursor++
                return makeSymbol()                                //getsymbol advances the cursor
            }
            val tup = kartyp[c.code]
            symIn.content = tup.pp
            when (tup) {
                LETTER, DIGIT -> {                              //c is a KAR, so we're building a string
                    var s = "";
                    while (isaC(c, LETTER, DIGIT)) {
                        s += c;
                        cursor++
                        c = textIn[cursor]
                    }
                    symIn.typ = VARIABLE;
                    symIn.content = s
                    cursor--
                }
                EXCLAM, ETX, LF, CR, OTHER -> {
                    symIn.typ = EOT
                    return symIn
                }
                PAR_LEFT -> {
                    symIn.typ = PAIR_START                      //the special character becomes the typ
                }
                PAR_RIGHT -> {
                    symIn.typ = PAIR_END                        //the special character becomes the typ
                }
                TIMES, DIVIDE, GT, LT, EQ -> {
                    if (symLocation > 0) {                      //we have a separator
                        symIn.typ = OPERATOR_5                  //the special character becomes the typ
                    }
                }
                QUESTION -> {
                    symIn.typ = ELVIS_Q                  //the special character becomes the typ
                }
                COLON -> {
                    symIn.typ = ELVIS_C                  //the special character becomes the typ
                }
                PLUS, MINUS -> {
                    if (symLocation == 0) {
                        symIn.typ = OPERATOR_3
                    } else if (isaC(textIn[cursor - 1], TIMES, DIVIDE, PLUS, MINUS, PAR_LEFT, QUESTION)) {
                        // we are not adding to the previous, this is a unary operator
                        symIn.typ = OPERATOR_3
                    } else symIn.typ = OPERATOR_6
                }
                TEST -> {                             //c is a KAR, so we're building a string
                    var s = "";
                    s += c;
                    cursor++
                    c = textIn[cursor]
                    while (isaC(c, LETTER, DIGIT)) {
                        s += c;
                        cursor++
                        c = textIn[cursor]
                    }
                    symIn.typ = VARIABLE;
                    symIn.content = s
                }
                else -> {                                       //we have a separator
                    symIn.typ = NONE                      //the special character becomes the typ
                }
            }
            cursor++
            if (expected.isEmpty()) return symIn            //default, we do not complain
            if (symIn.typ in expected) return symIn         //check on expected char is ok, no complaints
            val expectedList = expected.contentToString()
            errorsPresent = true
            errorsLogged++
            throw IllegalArgumentException(
                "SEPARATOR in line at cursor " +
                        "$cursor < ${textIn.substring(0, cursor)} > \n" +
                        "     char=$c, symbol=${symIn.content}   ==> < ${expected.contentToString()} >\n"
            );
            return symIn
        }

        private fun treatKar() {                                        //c is a KAR, so we're building a string
            symIn.typ = VARIABLE;
            var s = "";
            c = textIn[cursor]
            s += c;
            cursor++
            c = textIn[cursor]
            while (isaC(c, LETTER, DIGIT)) {
                s += c;
                cursor++
                c = textIn[cursor]
            }
            symIn.content = s
        }

        private fun clear() {
            cursor = 0
            errorsLogged = 0
            errorsPresent = false
            textIn = ""
            symLocation = 0
            symList = Array(256) { Symbol(EOT, "") }
            for (i in 0..255) {
                symList[i] = Symbol(EOT, "")
            }
        }
    }
}