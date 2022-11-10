import Kars.KarType.*
import Kars.SymType.*
import Kars.Symbol
import Kars.*
import Kars.Companion.kartyp

/* Backus Naur:
<expression> ::= <term> | <term> + <term>
<term> ::= <fac> | <fac> * <fac>
<fac> := sfac | <expresion>
<sfac> ::= <string>
*/
data class Symbol1(var typ: SymType = SymType.NONE, var content: String)


class ExpressionParser {
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
        private var symList = Array(256) { Symbol(NONE, "") }
        fun parseExp(s: String) {
            textIn = s
            pass1()
            pass2()
        }

        private fun pass1() {
//            input: textIn, output: symList
            cursor = 0
            var symLocation = 0
            errorsPresent = false
            println("textIn =  $textIn")
            do {
                symIn = makeSymbol()
                symList[symLocation] = Symbol(symIn.typ, symIn.content)
                symLocation++
            } while (symIn.typ != EOT)
            for (element in symList) {if (element.typ == NONE) {println();break} else print("${element.typ} ")}
            for (element in symList) {if (element.typ == NONE) {println();break} else print("${element.content} ")}
        }

        private fun pass2() {
            cursor = 0
            errorsPresent = false

            println("_________________________________________________________________________________________________")
            try {
//                if (comment()) return
                symIn = getSymbol()
                expression()
            } catch (e: IllegalArgumentException) {
                println("PARSE ERROR: ${e.message}")
                errorsLogged++;
            }
            if (!errorsPresent) println("RPN: $textOut\n=== $errorsLogged errors logged ===")
        }

        private fun comment(): Boolean {
            val psym: Symbol = getSymbol()
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
//            if (symIn.typ == PAIR_END) restOfExpression()
            val save = symIn
            if (symIn.typ != OPERATOR_T) return
            symIn = getSymbol(OPERATOR_T, EOT, PAIR_END)
            term()
            push(save)
            restOfExpression()
        }

        private fun restOfExpression1() {
            if (symIn.typ == PAIR_END) restOfExpression1()
            val psym: Symbol = getSymbol(OPERATOR_T, EOT, PAIR_END)
            if (psym.typ != OPERATOR_T) return
            val mem = psym.content
            term()
            textOut += "$mem "
            restOfExpression1()
        }

        private fun factor() {
//            if (symIn.typ == EOT) return
//            if (symIn.typ == VAR) {
//                push(symIn)
//                symIn = getSymbol(OPERATOR,EOT)
//            }

            push(symIn)
            symIn = getSymbol()
        }

        private fun restOfTerm() {
            val save = symIn
            if (symIn.typ != OPERATOR_F) return
            symIn = getSymbol(OPERATOR_F, EOT, PAIR_END)
            factor()
            push(save)
            restOfTerm()


        }

        private fun getSymbol(vararg expected: SymType): Symbol {
//            returns the next symbol
            cursor++
            return symList[cursor - 1]
        }

        private fun makeSymbol(vararg expected: SymType): Symbol {
            if (cursor > 6) {
                val xx = 1
            }
            if (cursor >= textIn.length) return symIn
            var c = textIn[cursor]
            val tup = kartyp(c)
            when (kartyp(c)) {
                KAR -> {                                        //c is a KAR, so we're building a string
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
                TIMES -> {                                       //we have a separator
                    symIn.typ = OPERATOR_T                        //the special character becomes the typ
                    symIn.content = kartyp[c.code].pp
                    cursor++
                }
                PLUS -> {                                       //we have a separator
                    symIn.typ = OPERATOR_F                        //the special character becomes the typ
                    symIn.content = kartyp[c.code].pp
                    cursor++
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
                        "     char=$c, symbol=${symIn.content}   ==> < ${expected.contentToString()} >\n" +
                        "     RPN= $textOut"
            );
            return symIn
        }

        fun push(sym: Symbol) {
            textOut += " ${sym.content}"
            println("push >>> ${sym.content}")
        }
    }
}
