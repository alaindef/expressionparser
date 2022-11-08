import MachineParser.SymbolType.*
import java.io.File
import java.time.LocalDate
import java.time.LocalDateTime

/* Backus Naur:
<symbol>	::= <HASHTAG> | <LINEENDKEY> | <PAIR_START> | <PAIR_END> | <STRING> | <ETX> | <STRING>
<line>			::= <commentline> | <linekey> <linebody>
<commentline>	::= <HASHTAG> <anything>
<linekey>		::= <STRING> <LINEENDKEY>
<linebody>		::= <key_value_pair> | <key_value_pair> <linebody>
<key_value_pair>::= <PAIR_START> <key> < value> <PAIR_END>
<key>			::=
*/

class DecryptedLine {
    companion object {
        var lineNumber: Int = 0
        var linekey: String? = null
        var key_value_pairs: kotlin.collections.MutableMap<String, String>? =
            kotlin.collections.HashMap<String, String>()
    }
}

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
        PAIR_END(256),
        ADF_END(512)
    }

    enum class Category { SEPARATOR, SKIP, NOTHING }

    data class SymbolStruc(
        var typ: MachineParser.SymbolType = MachineParser.SymbolType.NONE,
        var content: String
    )

    data class SData(val typ: SymbolType, val name: String, val cat: Category) {}

    companion object {

        private var symIn = SymbolStruc(NONE, "")
        var cursor: Int = 0
        var errorsPresent: Boolean = false
        var inputLine: String = ""
        const val errorLog: String = "logx.txt"
        val test = HASHTAG
        var specials = HashMap<Char, SData>()
        private val separatorNames = HashMap<SymbolType, String>()
        var errorsLogged: Int = 0

        init {
            specials['#'] = SData(HASHTAG, "HASHTAG", Category.SEPARATOR)
            specials[':'] = SData(LINEKEYEND, "LINEKEYEND", Category.SEPARATOR)
            specials['('] = SData(PAIR_START, "PAIR_START", Category.SEPARATOR)
            specials[')'] = SData(PAIR_END, "PAIR_END", Category.SEPARATOR)
            specials[' '] = SData(BLANK, "BLANK", Category.SKIP)
            specials['\t'] = SData(TAB, "TAB", Category.SKIP)
            specials['\u0002'] = SData(STRING, "STRING", Category.NOTHING)//ADF !cpp
            specials['\u0000'] = SData(ETX, "ETX", Category.SEPARATOR) //ADF !cpp
            specials['\r'] = SData(RET, "RET", Category.SEPARATOR)
            specials['!'] = SData(ADF_END, "RET", Category.SEPARATOR)

            for (k in specials.keys) {
                separatorNames[specials[k]!!.typ] = specials[k]!!.cat.toString()
            }
        }

        fun parseLine(s: String, lineNumber: Int) {
            cursor = 0
            inputLine = s
            errorsPresent = false
            DecryptedLine.lineNumber = lineNumber
            DecryptedLine.linekey = ""
            DecryptedLine.key_value_pairs?.clear()
            println("_________________________________________________________________________________________________")
            println("line $lineNumber $inputLine")
            try {
                if (comment()) return
                linekey()
                linebody()
            } catch (e: InternalError) {
                println("ERROR AT LINE $lineNumber: ")
                errorsLogged++;
            }
            if (!errorsPresent) println("DECRIPTION RESULT for line ${DecryptedLine.lineNumber} ==> ${DecryptedLine.linekey} ${DecryptedLine.key_value_pairs} ")
            println("$errorsLogged errors logged")
        }


        fun comment(): Boolean {

            readSymbol(STRING, HASHTAG)
            if (symIn.typ == HASHTAG) return true
            cursor = 0                              //do not start line at cursor=1 !
            return false
        }

        private fun linekey() {
            readSymbol(STRING);
            DecryptedLine.linekey = symIn?.content;
            readSymbol(LINEKEYEND);                    //expected symbol LINEKEYEND
        }

        private fun linebody() {
            if (keyValuePair())
                linebody()
        }

        private fun keyValuePair(): Boolean {
            readSymbol(RET, ETX, PAIR_START, ADF_END)
            if ((symIn.typ == ETX) or (symIn.typ == RET) or (symIn.typ == ADF_END)) return false;            //end of line reached
            readSymbol(STRING)
            var mapKey = symIn?.content                         //key
            readSymbol(STRING)
            var mapValue = symIn?.content                       //first value
            readSymbol(STRING, PAIR_END)
            while (symIn!!.typ == STRING) {
                mapValue = mapValue + " " + symIn!!.content;    //remaining values for this key
                readSymbol()                                    //do not check symbol as it can be ambiguous
            }
            DecryptedLine.key_value_pairs?.put(mapKey, mapValue)
//            println("keyvaluepair $mapKey  $mapValue")
            return true;
        }

        private fun readSymbol(vararg expected: SymbolType) {
            if (cursor >= inputLine.length) {
//                println("CURSOR TOO FAR")
                return
            }
            var c = inputLine[cursor]
            if (specials.keys.count { it == c } == 0) {         //c not a key, so is part of a string
                symIn!!.typ = STRING;
                var s = "";
                c = inputLine[cursor]
                while (specials.keys.count { it == c } == 0) {  //while c is not a special char
                    s += c;
                    cursor++
                    c = inputLine[cursor]
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
                return        //default, we do not complain
            }
            if (symIn.typ in expected) {
                return
            }            //check on expected char is ok, no complaints
            val expectedList = expected.contentToString()
            errorsPresent = true
            errorsLogged++
            println("SEPARATOR ERROR in line ${DecryptedLine.lineNumber} at cursor $cursor char $c ==> < $expectedList >")
            return
//            throw IllegalArgumentException("separator error: < $expectedList >");
        }
    }
}
