import Kars.KarType.*
import Kars.SymType.*
import Kars.Symbol
import Kars.*
import Kars.Companion.kartyp
import ExpressionParser.Companion.reportln
import Kars.Companion.expressionSize
import Kars.Companion.isa
import Kars.Companion.isaC

class Pass1 {
    companion object {
        private var symIn = Symbol(NONE, "",0)
        private var isFirstSymbol=true
        private var cursor: Int = 0
        var textIn: String = ""

        //        private var symList = Array(expressionSize) { Symbol(EOT, "") }
        private var symList: MutableList<Symbol> = mutableListOf()
        private var errorsPresent: Boolean = false
        private var c: Char = '\u0000'

        fun parse(s: String): MutableList<Symbol> {
            clear()
            textIn = s
            cursor = 0
            errorsPresent = false
            reportln("textIn =  $textIn", 0)
            do {
                symIn = makeSymbol()
                if (isa(symIn, ELV_C)) {                                       // substitute '?(' for '?'
                    symList.add(Symbol(BEXPE, PAR_R.pp, cursor))
                }
                symList.add(Symbol(symIn.typ, symIn.content, cursor))
                if (isa(symIn, ELV_Q)) {                                       // substitute '?(' for '?'
                    symList.add(Symbol(BEXPS, PAR_L.pp, cursor))
                }
            } while (symIn.typ != EOT)

            reportln(symList, 1, 5)
            return symList
        }

        private fun makeSymbol(vararg expected: SymType): Symbol {
            if (cursor >= textIn.length) return symIn
            c = textIn[cursor]
            if (isaC(c, BLANK, TAB)) {
                cursor++
                return makeSymbol()                                //getsymbol advances the cursor
            }
            val tup = kartyp[c.code]
            symIn.content = tup.pp
            when (tup) {
                LETT, DIGIT -> {                              //c is a KAR, so we're building a string
                    var s = "";
                    while (isaC(c, LETT, DIGIT)) {
                        s += c;
                        cursor++
                        c = textIn[cursor]
                    }
                    symIn.typ = VARI;
                    symIn.content = s
                    cursor--
                }
                EXCLA, ETX, LF, CR, OTHER -> {
                    symIn.typ = EOT
                    return symIn
                }
                PAR_L -> {
                    symIn.typ = BEXPS                      //the special character becomes the typ
                }
                PAR_R -> {
                    symIn.typ = BEXPE                        //the special character becomes the typ
                }
                TIMES, DIV, GT, LT, EQ -> {
                    if (! (isFirstSymbol)) {                      //we have a separator
                        symIn.typ = OP_5                  //the special character becomes the typ
                    }
                }
                QUEST -> {
                    symIn.typ = ELV_Q                  //the special character becomes the typ
                }
                COLON -> {
                    symIn.typ = ELV_C                  //the special character becomes the typ
                }
                PLUS, MINUS -> {
                    if (isFirstSymbol) {
                        symIn.typ = OP_3
                    } else if (isaC(textIn[cursor - 1], TIMES, DIV, PLUS, MINUS, PAR_L, QUEST)) {
                        // we are not adding to the previous, this is a unary operator
                        symIn.typ = OP_3
                    } else symIn.typ = OP_6
                }
                TEST -> {                             //c is a KAR, so we're building a string
                    var s = "";
                    s += c;
                    cursor++
                    c = textIn[cursor]
                    while (isaC(c, LETT, DIGIT)) {
                        s += c;
                        cursor++
                        c = textIn[cursor]
                    }
                    symIn.typ = VARI;
                    symIn.content = s
                }
                else -> {                                       //we have a separator
                    symIn.typ = NONE                      //the special character becomes the typ
                }
            }
            isFirstSymbol = false
            cursor++
            if (expected.isEmpty()) return symIn            //default, we do not complain
            if (symIn.typ in expected) return symIn         //check on expected char is ok, no complaints
            val expectedList = expected.contentToString()
            errorsPresent = true
            throw IllegalArgumentException(
                "SEPARATOR in line at cursor " +
                        "$cursor < ${textIn.substring(0, cursor)} > \n" +
                        "     char=$c, symbol=${symIn.content}   ==> < ${expected.contentToString()} >\n"
            );
            return symIn
        }

        private fun treatKar() {                                        //c is a KAR, so we're building a string
            symIn.typ = VARI;
            var s = "";
            c = textIn[cursor]
            s += c;
            cursor++
            c = textIn[cursor]
            while (isaC(c, LETT, DIGIT)) {
                s += c;
                cursor++
                c = textIn[cursor]
            }
            symIn.content = s
        }

        private fun clear() {
            cursor = 0
            errorsPresent = false
            textIn = ""
            symList.clear()
        }
    }
}
