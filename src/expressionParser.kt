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

        fun reportln(list: Array<Symbol>, level: Int) {
            for (element in list) {
                if ((element.typ == NONE) or (element.typ == EOT)) {
                    reportln("", 0)
                    break
                } else report("${element.typ.toString().padEnd(11)} ", 1)
            }
            for (element in list) {
                if (element.typ == NONE) {
                    reportln("", 1)
                    break
                } else report("${element.content.toString().padEnd(11)} ", 1)
            }
            reportln("", 1)
        }
    }
}
