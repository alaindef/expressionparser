import Kars.SymType.*
import Kars.Symbol

/* Backus Naur:
expression	::= term	    |   *{OP_3  term}
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
        var reportLevel = 1
        private const val errorLog: String = "logx.txt"
        private var errorsLogged: Int = 0
        private var errorsPresent: Boolean = false
        fun parseExp(s: String, reportLevelIn: Int) {
            reportLevel = reportLevelIn
            textIn = "$s!"
            symList = Pass1.parse(s)
            textOut = Pass2.parse(symList)

        }


        fun report(s: String, level: Int) {
            if (level <= reportLevel) print(s)
        }

        fun reportln(s: String, level: Int) {
            if (level <= reportLevel) println(s)
        }
    }
}
