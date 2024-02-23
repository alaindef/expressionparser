import Kars.SymType.*
import Kars.Symbol

class Interpreter {
    companion object {
        fun evalE(stack: MutableList<Symbol>): Symbol {
            var res = 0f
            val op = stack.removeLast()
            val v1 = stack.removeLast().content.toFloat()
            val v2 = stack.removeLast().content.toFloat()
            when (op.content) {
                "*" -> res = v2 * v1
                "/" -> res = v2 / v1
                "+" -> res = v2 + v1
                "-" -> res = v2 - v1
                "<" -> res = if (v2 < v1) 1f else 0f
                ">" -> res = if (v2 > v1) 1f else 0f
                "=" -> res = if (v2 == v1) 1f else 0f
                ":" -> {
                    val v3 = stack.removeLast().content.toFloat()
                    res = if (v3 > 0) v2 else v1
                }
            }
            return Symbol(VARI, res.toString(),0)
        }
        fun readE(s: MutableList<Symbol>): MutableList<Symbol> {
            var out = ""
            var sym: Symbol
            val stack: MutableList<Symbol> = mutableListOf(Symbol(VARI, "$",0))
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
            ExpressionParser.reportln("",0)
            return stack
        }

        private fun reportList(s: String, l: MutableList<Symbol>, level: Int) {
            ExpressionParser.report(s, level)
            ExpressionParser.reportln(l, level, 5)
        }
    }
}