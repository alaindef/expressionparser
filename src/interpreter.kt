import Kars.SymType.*
import Kars.Symbol

class interpreter {
    companion object {
        private var symIn = Symbol(NONE, "")
        private var cursor: Int = 0
        var textIn: String = ""
        var textOut: String = ""
        private var symList = Array(Kars.expressionSize) { Symbol(EOT, "") }
        var reportLevel = 1
        private var errorsPresent: Boolean = false
        fun evalE(stack: MutableList<Symbol>): Symbol {
            var res: Int = 0
            val op = stack.removeLast()
            val v1 = stack.removeLast().content.toInt()
            val v2 = stack.removeLast().content.toInt()
            when (op.content) {
                "*" -> res = v1 * v2
                "/" -> res = v1 / v2
                "+" -> res = v1 + v2
                "-" -> res = v1 - v2
                "<" -> res = if (v2 < v1) 1 else 0
                ">" -> res = if (v2 > v1) 1 else 0
                "=" -> res = if (v2 == v1) 1 else 0
                ":" -> {
                    val v3 = stack.removeLast().content.toInt()
                    res = if (v3 > 0) v2 else v1
                }
            }
            return Symbol(VARI, res.toString())
        }

        fun readE(s: MutableList<Symbol>): MutableList<Symbol> {
            var out = ""
            var sym: Symbol
            val stack: MutableList<Symbol> = mutableListOf(Symbol(VARI, "$"))
            ExpressionParser.reportln(s, 2, 5)
            for (element in s) {
                if (element.typ != ELV_Q) {
                    stack.add(element)
                    if (element.typ != VARI) {
                        stack.add(evalE(stack))
                        reportList("after: ", stack, 2)
                    }
                }
            }
            ExpressionParser.reportln("Calculator result = ${stack[1].content}", 0)
            return stack
        }

        private fun reportList(s: String, l: MutableList<Symbol>, level: Int) {
            ExpressionParser.report(s, level)
            ExpressionParser.reportln(l, level, 5)
        }
    }
}