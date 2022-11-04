import java.io.File

class MachineParser {
    constructor() {
        specials['#'] = SData(SymbolType.HASHTAG, "HASHTAG", Category.SEPARATOR)
        specials[':'] = SData(SymbolType.LINEKEYEND, "LINEKEYEND", Category.SEPARATOR)
        specials['('] = SData(SymbolType.PAIR_START, "PAIR_START", Category.SEPARATOR)
        specials[')'] = SData(SymbolType.PAIR_END, "PAIR_END", Category.SEPARATOR)
        specials[' '] = SData(SymbolType.BLANK, "BLANK", Category.SKIP)
        specials['\t'] = SData(SymbolType.TAB, "TAB", Category.SKIP)
        specials['\u0002'] =
            SData(SymbolType.STRING, "STRING", Category.NOTHING)    // ADF !cpp dummy for all string chars
//        specials['\2']  = SData(SymbolType.STRING,		"STRING",       Category.NOTHING)	// ADF !cpp dummy for all string chars
        specials['\u0000'] = SData(SymbolType.ETX, "ETX", Category.SEPARATOR) //ADF !cpp
//        specials['\0']  = SData(SymbolType.ETX,			"ETX" ,         Category.SEPARATOR) //ADF !cpp
        specials['\r'] = SData(SymbolType.RET, "RET", Category.SEPARATOR)

        separatorNames[SymbolType.HASHTAG] = Category.SEPARATOR
//        for ((key, value ) in specials) {separatorNames[key] = value.name}
    }

    private enum class SymbolTypeDel(val typnum: Int) {
        NONE(2),
        BLANK(3),
        TAB(4),
        STRING(5),
        ETX(6),
        RET(7),
        HASHTAG(8),
        LINEKEYEND(9),
        PAIR_START(10),
        PAIR_END(11)
    }

    enum class SymbolType {
        NONE,
        BLANK,
        TAB,
        STRING,
        ETX,
        RET,
        HASHTAG,
        LINEKEYEND,
        PAIR_START,
        PAIR_END
    }

    val separatorNames = HashMap<SymbolType, String>()
    var errorsLogged: Int = 0

    enum class Category { SEPARATOR, SKIP, NOTHING }
    class SData(val typ: SymbolType, val name: String, val cat: Category) {}

    private var specials = HashMap<Char, SData>()
    private var spec = HashMap<Char, Int>()

    //    specials['#'] = SData(HASHTAG,"HASHTAG", SEPARATOR)
    class Symbol private constructor(typ: SymbolType, content: String)

    var sym: Symbol? = null
    var cursor: Int = 0
    var line: String = ""

    fun comment(): Boolean {
        return true
    }

    public fun getSpecials(): HashMap<Char, SData> {
        return this.specials
    }
    fun lineKey() {}
    fun lineBody() {}
    fun keyValuePair() {}
    fun readSymbol(expected: SymbolType) {}   // void read_symbol(symbol_type expected = NONE);
    fun logError() {}
    fun lParseLine(s: String, line_number: Int) {
        val cursor = 0
        val line = s
    }


    companion object {
        fun getThings() {
            for (sym in SymbolType.values()) {
                println("${sym.ordinal} = ${sym.name}")
            }
            File("logx.txt").writeText("awel, goegedaan\n")
            File("logx.txt").appendText("awel, nog beter!\n")
        }
        val teut1 = 23
    }
}

private operator fun <K, V> HashMap<K, V>.set(s: MachineParser.SymbolType, value: MachineParser.Category) {

}
