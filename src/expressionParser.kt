import Kars.KarType.*
import Kars.SymType.*
import Kars.Symbol
import Kars.*
import Kars.Companion.kartyp

/* Backus Naur:expression	::= term	    |   *{OP_3  term}
term		::= factor	    |   *{OP_2  factor}
factor      ::= var         | num                |   bexpression
bexpression ::= LB expression RB
OP_3        ::= + | -
OP_2        ::= * | / | > | < | = | ? | :
LB          ::= (
RB          ::= )
letter      ::= a | b | c | ....
digit       ::= 1 | 2 | 3 | ...
var         ::= letter | {letter | digit}
num         ::= {digit}
*/

class ExpressionParser {
    companion object {
        private var symIn = Symbol(NONE, "")
        private var cursor: Int = 0
        var textIn: String = ""
        var textOut: String = ""
        private var symList = Array(256) { Symbol(EOT, "") }
        var errorLevel = 1
        private const val errorLog: String = "logx.txt"
        private var errorsLogged: Int = 0
        private var errorsPresent: Boolean = false
        fun parseExp(s: String) {
            textIn = "$s!"
            pass1()
            pass2()
        }

        private fun clear() {
            cursor = 0
            errorsLogged = 0
            errorsPresent = false
            textOut = ""
            for (i in 0..255) {
                symList[i] = Symbol(EOT, "")
            }
        }

        fun pass1() {
//            input: textIn, output: symList
            clear()
            cursor = 0
            var symLocation = 0
            errorsPresent = false
            reportln("textIn =  $textIn")
            do {
                symIn = makeSymbol()
                symList[symLocation] = Symbol(symIn.typ, symIn.content)
                symLocation++
            } while (symIn.typ != EOT)

            for (element in symList) {
                if ((element.typ == NONE) or (element.typ == EOT)) {
                    reportln("");break
                } else report("${element.typ} ")
            }
            for (element in symList) {
                if (element.typ == NONE) {
                    reportln("");break
                } else report("${element.content} ")
            }
        }

        fun pass2() {
//            input: symlist, output: RPN
            cursor = 0
            errorsPresent = false
            try {
//                if (comment()) return
                symIn = nextSymbol()
                expression()
            } catch (e: IllegalArgumentException) {
                println("PARSE ERROR: ${e.message}")
                errorsLogged++;
            }
            if (!errorsPresent) println("RPN: $textOut   === $errorsLogged errors logged ===")
        }

        private fun comment(): Boolean {
            val psym: Symbol = nextSymbol()
            if (psym.typ == COMMENT) return true
            cursor = 0                              //do not start line at cursor=1 !
            symIn = Symbol(NONE, "")
            return false
        }

        private fun expression() {
            term()
            while (isa(symIn, OPERATOR_3)) {
                val save = symIn
                symIn = nextSymbol()
                term()
                push(save)
            }
        }

        private fun isa(sym: Symbol, vararg op: SymType): Boolean {
            return (sym.typ in op)
        }

        private fun term() {
            factor()
            while (isa(symIn, OPERATOR_2)) {
                val save = symIn
                symIn = nextSymbol()
                factor()
                push(save)
            }
        }

        private fun factor() {
            //next symbol if success
            if (isa(symIn, VARIABLE, LITERAL)) {
                push(symIn)
                symIn = nextSymbol()
            } else {
                bexpression()
            }
        }


        private fun bexpression() {
            if (symIn.typ == PAIR_START) {
                symIn = nextSymbol()
                expression()
                if (symIn.typ == PAIR_END) {
                    symIn = nextSymbol()
                } else
                    println("PAIR END ERROR")
            }

        }

        private fun nextSymbol(vararg expected: SymType): Symbol {
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
                    symIn.typ = VARIABLE;
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
                TIMES, DIVIDE, GT, LT, EQ, COLON, QUESTION -> {                                       //we have a separator
                    symIn.typ = OPERATOR_2                        //the special character becomes the typ
                    symIn.content = kartyp[c.code].pp
                    cursor++
                }
                PLUS, MINUS -> {                                       //we have a separator
                    symIn.typ = OPERATOR_3                        //the special character becomes the typ
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
            reportln("push >>> ${sym.content}")
        }

        fun report(s: String) {
            if (errorLevel > 0) print(s)
        }

        fun reportln(s: String) {
            if (errorLevel > 0) println(s)
        }
    }
}
