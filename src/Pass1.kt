import Kars.KarType.*
import Kars.SymType.*
import Kars.Symbol
import Kars.*
import Kars.Companion.kartyp
import ExpressionParser.Companion.report
import ExpressionParser.Companion.reportln

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
//            input: textIn, output: symList
            clear()
            textIn = s
            cursor = 0
            errorsPresent = false
            reportln("textIn =  $textIn", 0)
            do {
                symIn = makeSymbol()
                symList[symLocation] = Symbol(symIn.typ, symIn.content)
                symLocation++
            } while (symIn.typ != EOT)

            for (element in symList) {
                if ((element.typ == NONE) or (element.typ == EOT)) {
                    ExpressionParser.reportln("", 0)
                    break
                } else report("${element.typ} ", 0)
            }
            for (element in symList) {
                if (element.typ == NONE) {
                    reportln("", 1)
                    break
                } else report("${element.content} ", 0)
            }
            return symList
        }

        private fun makeSymbol(vararg expected: SymType): Symbol {
            if (cursor > 6) {           //for debugging
                val xx = 1
            }
            if (cursor >= textIn.length) return symIn
            c = textIn[cursor]
            val tup = kartyp(c)
            when (kartyp(c)) {
                KAR -> treatKar()
                BLANK, TAB -> {  //special char can be skipped
                    cursor++
                    return makeSymbol()                                //getsymbol advances the cursor
                }
                EXCLAM, ETX, LF, CR, OTHER -> {
                    symIn.typ = EOT
                    symIn.content = kartyp[c.code].pp
                    return symIn
                }
                PAR_LEFT -> {
                    symIn.typ = PAIR_START                      //the special character becomes the typ
                    symIn.content = kartyp[c.code].pp
                    cursor++
                }
                PAR_RIGHT -> {
                    symIn.typ = PAIR_END                        //the special character becomes the typ
                    symIn.content = kartyp[c.code].pp
                    cursor++
                }
                TIMES, DIVIDE, GT, LT, EQ, COLON, QUESTION -> {
                    if (symLocation > 0) {
                        //we have a separator
                        symIn.typ = OPERATOR_2                        //the special character becomes the typ
                        symIn.content = kartyp[c.code].pp
                        cursor++
                    }
                }
                PLUS, MINUS -> {
                    if (symLocation > 0) {if (symList[symLocation - 1].typ == VARIABLE) {//we have a separator
                        symIn.typ = OPERATOR_3                        //the special character becomes the typ
                        symIn.content = kartyp[c.code].pp
                        cursor++
                    } else treatKar() }
                    else treatKar()
                }
                else -> {                                       //we have a separator
                    symIn.typ = NONE                      //the special character becomes the typ
                    symIn.content = kartyp[c.code].pp
                    cursor++

                }
            }
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
            while (kartyp(c) == KAR) {
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