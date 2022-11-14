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

        fun parse(s: Array<Symbol>): String {
            clear()
            symList = s
            cursor = 0
            errorsPresent = false
            try {
                symIn = nextSymbol()
                expression()
            } catch (e: IllegalArgumentException) {
                println("PARSE ERROR: ${e.message}")
                errorsLogged++;
            }
            if (!errorsPresent)
                println("RPN: $textOut   === $errorsLogged errors logged ===")
            return textOut
        }

        private fun expression() {
            term()
            while (isa(symIn, OPERATOR_6, ELVIS_Q, ELVIS_C)) {
                val save = symIn
                symIn = nextSymbol()
                term()
                push(save)
            }
        }

        private fun term() {
            if (isa(symIn, OPERATOR_3)) {
                val save = symIn
                symIn = nextSymbol()
                factor()
                val code = 177
                if (save.content == "[-]") push(Symbol(OPERATOR_3, "${code.toChar()}"))
            }
            factor()
            while (isa(symIn, OPERATOR_5)) {
                val save = symIn
                symIn = nextSymbol()
                factor()
                push(save)
            }
        }

        private fun factor() {
            //next symbol if success
            when (symIn.typ) {
                VARIABLE, LITERAL -> {
                    push(symIn)
                    symIn = nextSymbol()
                }
                else -> bExpression()
            }
        }

        private fun bExpression() {
            if (isa(symIn, PAIR_START, ELVIS_Q)) {
                symIn = nextSymbol()
                expression()
                if (isa(symIn, PAIR_END, ELVIS_C)) {
                    symIn = nextSymbol()
                } else
                    println("PAIR END ERROR")
            }
        }

        private fun nextSymbol(vararg expected: SymType): Symbol {
            val next = symList[cursor]
            cursor++
//            if (next.typ in expected)
            return next
        }

        fun push(sym: Symbol) {
            textOut += " ${sym.content}"
            reportln("push >>> ${sym.content}", 2)
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
    }
}