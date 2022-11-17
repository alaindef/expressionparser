import Kars.SymType.*
import Kars.Symbol

class ExpressionParser {
    companion object {
        private var symIn = Symbol(NONE, "")
        private var cursor: Int = 0
        var textIn: String = ""
        var textOut: String = ""
        private var symList = Array(256) { Symbol(EOT, "") }
        var reportLevel = 1
        private const val errorLog: String = "logx.txt"
        private var errorsPresent: Boolean = false
        fun parseExp(s: String) {
            textIn = "$s!"
            symList = Pass1.parse(s)
            textOut = Pass2.parse(symList)
            println()
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
                if (element.typ == NONE) {
                    reportln("", level)
                    break
                } else report("${element.content.toString().padEnd(padding)} ", 1)
            }
            reportln("", 1)
        }
    }
}
