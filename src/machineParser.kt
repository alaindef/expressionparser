import java.io.File
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.math.log2


class MachineParser {
    constructor()

    enum class SymbolType(val symCode: Int) {
        NONE(0),
        BLANK(1),
        TAB(2),
        STRING(4),
        ETX(8),
        RET(16),
        HASHTAG(32),
        LINEKEYEND(64),
        PAIR_START(128),
        PAIR_END(256)
    }

    enum class Category { SEPARATOR, SKIP, NOTHING }
    class SData(val typ: SymbolType, val name: String, val cat: Category) {}


    fun comment(): Boolean {
        return true
    }

    public fun getSpecials(): HashMap<Char, SData> {
        return specials
    }

    fun logError() {}
    fun lParseLine(s: String, line_number: Int) {
        val cursor = 0
        val line = s
    }


    companion object {

        private var symIn = SymbolStruc(SymbolType.NONE, "")
        var cursor: Int = 0
        var line: String = ""
        val errorLog: String = "logx.txt"
        val test = SymbolType.HASHTAG
        var specials = HashMap<Char, SData>()
        private val separatorNames = HashMap<SymbolType, String>()
        var errorsLogged: Int = 0

        init {
            specials['#'] = SData(SymbolType.HASHTAG, "HASHTAG", Category.SEPARATOR)
            specials[':'] = SData(SymbolType.LINEKEYEND, "LINEKEYEND", Category.SEPARATOR)
            specials['('] = SData(SymbolType.PAIR_START, "PAIR_START", Category.SEPARATOR)
            specials[')'] = SData(SymbolType.PAIR_END, "PAIR_END", Category.SEPARATOR)
            specials[' '] = SData(SymbolType.BLANK, "BLANK", Category.SKIP)
            specials['\t'] = SData(SymbolType.TAB, "TAB", Category.SKIP)
            specials['\u0002'] = SData(SymbolType.STRING, "STRING", Category.NOTHING)//ADF !cpp
            specials['\u0000'] = SData(SymbolType.ETX, "ETX", Category.SEPARATOR) //ADF !cpp
            specials['\r'] = SData(SymbolType.RET, "RET", Category.SEPARATOR)

            for (k in specials.keys) {
                separatorNames[specials[k]!!.typ] = specials[k]!!.cat.toString()
//                println("sep:  " + k + " " + separatorNames[specials[k]!!.typ] + " " + specials[k]!!.cat)
            }
        }

        fun getThings() {
            val current = LocalDate.now()
            val currentTime = LocalDateTime.now()
            val pp = "logx.txt"
            File(errorLog).writeText("awel, goegedaan, vandaag $current\n")
            File(errorLog).appendText("awel, nog beter! op tijdstip $currentTime\n")
            val a = specials.keys.count { it == '1' }
        }

        fun vari(vararg xs: Int) {
            if (3 in xs) println("ok") else println("NOK")
            val qq = xs.contentToString()
            println("xs is $qq")
            return
        }

        fun parseLine(s: String, lineNumber: Int) {
            cursor = 0;
            line = s;
            DecryptedLine.lineNumber = lineNumber;
            DecryptedLine.linekey = "";
            DecryptedLine.key_value_pairs?.clear();
            try {
                if (comment()) return;
                linekey();
                linebody();
            } catch (e: InternalError) {
                println("ERROR AT LINE $lineNumber: e.what()")
                errorsLogged++;
            }
        }

        fun comment(): Boolean {

            readSymbol(SymbolType.STRING, SymbolType.HASHTAG)
            if (symIn.typ == SymbolType.HASHTAG) return true
            cursor = 0                              //do not start line at cursor=1 !
            return false
        }

        private fun linekey() {
            readSymbol(SymbolType.STRING);
            DecryptedLine.linekey = symIn?.content;
            println("from linekey: decrypted: ${DecryptedLine.linekey}  ${DecryptedLine.key_value_pairs}")
            readSymbol(SymbolType.LINEKEYEND);                    //expected symbol LINEKEYEND
        }

        private fun linebody() {
            if (keyValuePair())
                println("from linekey: decrypted: ${DecryptedLine.linekey}  ${DecryptedLine.key_value_pairs}")
            linebody()
            println("from linekey: decrypted: ${DecryptedLine.linekey}  ${DecryptedLine.key_value_pairs}")
        }

        private fun keyValuePair(): Boolean {
            readSymbol(SymbolType.RET, SymbolType.ETX, SymbolType.PAIR_START)
            if ((symIn.typ == SymbolType.ETX) or (symIn.typ == SymbolType.RET)) return false;			//end of line reached
            readSymbol(SymbolType.STRING)
            val mapKey = symIn?.content                         //key
            readSymbol(SymbolType.STRING)
            var mapValue = symIn?.content                       //first value
            readSymbol(SymbolType.STRING, SymbolType.PAIR_END)
            while (symIn!!.typ == SymbolType.STRING) {
                mapValue = mapValue + " " + symIn!!.content;    //remaining values for this key
                readSymbol()							        //do not check symbol as it can be ambiguous
            }
//            DecryptedLine.key_value_pairs?.set(mapKey, mapValue)
            println("keyvaluepair $mapKey  $mapValue")
//            DecryptedLine.key_value_pairs?.set(mapKey, mapValue);
            return true;
        }

        private fun readSymbol(vararg expected: SymbolType) {
            var c = line[cursor]
            if (specials.keys.count { it == c } == 0) {         //c not a key, so is part of a string
                symIn!!.typ = SymbolType.STRING;
                var s = "";
                c = line[cursor]
                while (specials.keys.count { it == c } == 0) {  //while c is not a special char
                    s += c;
                    cursor++
                    c = line[cursor]
                }
                symIn!!.content = s
            } else if (specials[c]?.cat == Category.SKIP) {     //special char can be skipped
                cursor++
                readSymbol()                 //readsymbol advances the cursor
            } else {                                            //we have a separator
                symIn!!.typ = specials[c]!!.typ                 //the special character becomes the typ
                symIn!!.content = c.toString()
                cursor++
            }
            if (expected.isEmpty()) {
                println("expected is empty")
                return        //default, we do not complain
            }
            if (symIn.typ in expected) {
                val symTyp = symIn.typ
                println("symIn.typ $symTyp is in expected")
                return
            }            //check on expected char is ok, no complaints
            val expectedList = expected.contentToString()
            println("separator error: < $expectedList >")
//            throw IllegalArgumentException("separator error: <" +  ">");
        }
    }
}

private fun <K, V> Map<K, V>.set(key: V?, value: V?) {

}

//private operator fun <K, V> HashMap<K, V>.set(s: MachineParser.SymbolType, value: MachineParser.Category) {
//
//}
