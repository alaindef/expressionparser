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
        private var errorsPresent: Boolean = false

        fun parse(s: Array<Symbol>): String {
            clear()
            symList = s
            cursor = 0
            errorsPresent = false
            try {
                symIn = nextSymbol("parse")
                expression()
            } catch (e: IllegalArgumentException) {
                println("PARSE ERROR: ${e.message}")
            }
            if (!errorsPresent)
                println("RPN: $textOut  ")
            return textOut
        }

        private fun expression() {
            term()
            while (isa(symIn, OP_6, ELV_Q, ELV_C)) {
                val save = symIn
                symIn = nextSymbol("expresion", VARI, BEXPS)
                term()
                push(save)
            }
        }

        private fun term() {
            if (isa(symIn, OP_3)) {
                val save = symIn
                symIn = nextSymbol("term", VARI)
                factor()
                val code = 177
                if (save.content == "-") push(Symbol(OP_3, "${code.toChar()}"))
            }
            factor()
            while (isa(symIn, OP_5)) {
                val save = symIn
                symIn = nextSymbol("term", VARI,BEXPS)
                factor()
                push(save)
            }
        }

        private fun factor() {
            //next symbol if success
            when (symIn.typ) {
                VARI, LIT -> {
                    push(symIn)
                    symIn = nextSymbol("factor", OP_5, OP_6, BEXPE, ELV_Q, EOT)
                }
                else -> bExpression()
            }
        }

        private fun bExpression() {
            if (isa(symIn, BEXPS)) {
                symIn = nextSymbol("bExpression", VARI, BEXPS, OP_3)
                expression()
                    symIn = nextSymbol("bExpression", VARI, OP_6, BEXPS, BEXPE, ELV_Q, ELV_C,  EOT)
            }
        }

        private fun nextSymbol1(vararg expected: SymType): Symbol {
            val next = symList[cursor]
            cursor++
//            if (next.typ in expected)
            return next
        }


        private fun nextSymbol(from: String, vararg expected: SymType): Symbol {
            val next = symList[cursor]
            cursor++
            if (expected.isEmpty()) return next
            if (next.typ in expected) return next
            errorsPresent = true
            throw java.lang.IllegalArgumentException(
                "from <$from> at cursor=${--cursor} symbol={${next.content}, ${next.typ}} " +
                        "NOT IN ${expected.contentToString()}"
            )
            return next
        }

        fun push(sym: Symbol) {
            textOut += " ${sym.content}"
            reportln("push >>> ${sym.content}", 2)
        }

        private fun clear() {
            cursor = 0
            errorsPresent = false
            textOut = ""
            for (i in 0..255) {
                symList[i] = Symbol(EOT, "")
            }
        }
    }
}