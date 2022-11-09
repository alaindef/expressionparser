import Kars.KarType.*
import Kars.SymType.*
import Kars.*
import Kars.Companion.kartyp

/* Backus Naur:
<expression> ::= <term> | <term> + <term>
<term> ::= <fac> | <fac> * <fac>
<fac> := sfac | <expresion>
<sfac> ::= <string>
*/

class MachineParser {
    constructor()

    data class SData(val typ: KarType, val name: String, val cat: Category) {}

    companion object {
        private var symIn = Symbol(NONE, "")
        var cursor: Int = 0
        var errorsPresent: Boolean = false
        var textIn: String = ""
        var textOut: String = ""
        const val errorLog: String = "logx.txt"
        private val separatorNames = HashMap<KarType, String>()
        var errorsLogged: Int = 0
        fun parseLine(s: String) {
            cursor = 0
            textIn = s
            errorsPresent = false
            println("_________________________________________________________________________________________________")
            println("textIn =  $textIn")
            try {
                if (comment()) return
                expression()
            } catch (e: IllegalArgumentException) {
                println("PARSE ERROR: ${e.message}")
                errorsLogged++;
            }
            if (!errorsPresent) println("RPN: $textOut")
            println("$errorsLogged errors logged")
        }

        private fun comment(): Boolean {
            val psym: Symbol = readSymbol()
            if (psym.typ == COMMENT) return true
            cursor = 0                              //do not start line at cursor=1 !
            symIn = Symbol(NONE, "")
            return false
        }

        private fun expression() {
            term()
            restOfExpression()
        }

        private fun term() {
            factor()
            restOfTerm()
        }

        private fun restOfExpression() {
            val psym: Symbol = readSymbol(OPERATOR,EOT)
            if (psym.typ != OPERATOR) return                            //rewind cursor!
            val mem = psym.content
            term()
            textOut += "$mem "
            restOfExpression()
        }

        private fun factor() {
            textOut += readSymbol().content
            textOut += " "
        }

        private fun restOfTerm() {

        }

        @kotlin.jvm.Throws(IllegalArgumentException::class)
        fun readSymbol(vararg expected: SymType): Symbol {

            if (cursor >= textIn.length) return symIn
            var c = textIn[cursor]
            val tup = kartyp(c)
            when (kartyp(c)) {
                KAR -> {                            //c is a KAR, so we're building a string
                    symIn.typ = VAR;
                    var s = "";
                    c = textIn[cursor]
                    while (kartyp(c) == KAR) {
                        s += c;
                        cursor++
                        c = textIn[cursor]
                    }
                    symIn.content = s

                }
                BLANK, TAB -> {  //special char can be skipped
                    cursor++
                    readSymbol()                                //readsymbol advances the cursor
                }
                EXCLAM, ETX, LF, CR, OTHER  -> return symIn
                else -> {                                     //we have a separator
                    symIn.typ = OPERATOR                        //the special character becomes the typ
                    symIn.content = kartyp[c.code].pp
                    cursor++

                }
            }
            if (expected.isEmpty()) return symIn            //default, we do not complain
            if (symIn.typ in expected) return symIn         //check on expected char is ok, no complaints
            val expectedList = expected.contentToString()
            errorsPresent = true
            errorsLogged++
            throw IllegalArgumentException("SEPARATOR in line at cursor $cursor char $c ==> < $expectedList >");
            return symIn
        }
    }
}
