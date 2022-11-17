import Kars.SymType.*
import Kars.Symbol

class ExpressionParser {
    companion object {
        private var symIn = Symbol(NONE, "")
        private var cursor: Int = 0
        var textIn: String = ""
        var textOut: String = ""
        private var symList = Array(Kars.expressionSize) { Symbol(EOT, "") }
        var reportLevel = 1
        private const val errorLog: String = "logx.txt"
        private var errorsPresent: Boolean = false
        fun parseExp(s: String): String {
            textIn = "$s!"
            symList = Pass1.parse(s)
            textOut = Pass2.parse(symList)
//            println()
            return textOut
        }

        fun report(s: String, level: Int) {
            if (level <= reportLevel) print(s)
        }

        fun reportln(s: String, level: Int) {
            if (level <= reportLevel) println(s)
        }


        fun reportln(list: Array<Symbol>, level: Int) {
            var index = 0
            val padding = 5
            for (element in list) {
                if ((element.typ == NONE) or (element.typ == EOT)) {
                    reportln("", level)
                    break
                } else report("${index++.toString().padEnd(padding)} ", 1)
            }
            for (element in list) {
                if ((element.typ == NONE) or (element.typ == EOT)) {
                    reportln("", level)
                    break
                } else report("${element.typ.toString().padEnd(padding)} ", 1)
            }

            for (element in list) {
                if ((element.typ == NONE) or (element.typ == EOT)) {
//                    reportln("", level)
                    break
                } else report("${element.content.toString().padEnd(padding)} ", 1)
            }
            reportln("", 1)
        }
        fun reportln(list: MutableList<Symbol>, level: Int, padding: Int) {
            for (element in list) {
                if ((element.typ == NONE) or (element.typ == EOT)) {
                    ExpressionParser.reportln("", level)
                    break
                } else {
                    val ss = "${element.content.toString().padEnd(padding)}"
                    ExpressionParser.report(ss, level)
                }
            }
            ExpressionParser.reportln("", level)
        }

    }
}
