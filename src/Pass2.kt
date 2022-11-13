import Kars.SymType.*
import Kars.Symbol
import Kars.*
import Kars.Companion.isa
import ExpressionParser.Companion.reportln

class Pass2 {

    companion object {
        private var symIn = Symbol(NONE, "")
        private var cursor: Int = 0
        var textOut: String = ""
        private var symList = Array(256) { Symbol(EOT, "") }
        private const val errorLog: String = "logx.txt"
        private var errorsLogged: Int = 0
        private var errorsPresent: Boolean = false

        fun parse(s:Array<Symbol>): String {
//            input: symlist, output: RPN
            clear()
            symList = s
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
            val neut=0
            println("from pass2 once")
            println("from pass2 twice $neut")
            if (!errorsPresent) println("RPN: $textOut   === $errorsLogged errors logged ===")
            return textOut
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


        fun push(sym: Symbol) {
            textOut += " ${sym.content}"
            reportln("push >>> ${sym.content}", 1)
        }

        private fun clear() {
            cursor = 0
            errorsLogged = 0
            errorsPresent = false
            textOut =""
            for (i in 0..255) {
                symList[i] = Symbol(EOT, "")
            }
        }

    }
}