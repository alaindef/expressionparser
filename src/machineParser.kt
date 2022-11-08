import MachineParser.SymbolType.*
import java.io.IOError

/* Backus Naur:
<symbol>	::= <HASHTAG> | <LINEENDKEY> | <PAIR_START> | <PAIR_END> | <STRING> | <ETX> | <STRING>
<line>			::= <commentline> | <linekey> <linebody>
<commentline>	::= <HASHTAG> <anything>
<linekey>		::= <STRING> <LINEENDKEY>
<linebody>		::= <key_value_pair> | <key_value_pair> <linebody>
<key_value_pair>::= <PAIR_START> <key> < value> <PAIR_END>
<key>			::=

<expression> ::= <term> | <term> + <term>
<term> ::= <fac> | <fac> * <fac>
<fac> := sfac | <expresion>
<sfac> ::= <string>
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
        PLUS(200),
        TIMES(201),
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
        var infixString: String = ""
        var rpnString: String = ""
        const val errorLog: String = "logx.txt"
        val test = HASHTAG
        var specials = HashMap<Char, SData>()
        private val separatorNames = HashMap<SymbolType, String>()
        var errorsLogged: Int = 0

        init {
            specials['#'] = SData(HASHTAG, "HASHTAG", Category.SEPARATOR)
            specials['+'] = SData(PLUS, "PLUS", Category.SEPARATOR)
            specials['*'] = SData(LINEKEYEND, "TIMES", Category.SEPARATOR)
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

        fun parse(s: String) {
            cursor = 0
            infixString = s
            errorsPresent = false
            println("_________________________________________________________________________________________________")
            println("input expression $infixString")
            try {
                if (comment()) return
                expression()
            } catch (e: InternalError) {
                println("PARSE ERROR: ")
                errorsLogged++;
            }
            if (!errorsPresent) println("RPN: $rpnString")
            println("$errorsLogged errors logged")
        }


        private fun comment(): Boolean {
            val psym:SymbolStruc = readSymbol(STRING, HASHTAG)
            if (psym.typ == HASHTAG) return true
            cursor = 0                              //do not start line at cursor=1 !
            return false
        }

        private fun expression() {
            term()
            restOfExpression()
        }

        private fun term(){
            rpnString += readSymbol(STRING).content
            rpnString += " "
//            factor()
            restOfTerm()
        }

        private fun restOfExpression(){
            val psym:SymbolStruc = readSymbol(PLUS, ADF_END)
            if (psym.typ != PLUS) return
            expression()
            rpnString += "ADD "
        }

        private fun factor(){
            rpnString += readSymbol(STRING).content
            rpnString += " "
        }
        private fun restOfTerm(){

        }

        @kotlin.jvm.Throws(IOError::class)
        fun readSymbol(vararg expected: SymbolType): SymbolStruc{

            if (cursor >= infixString.length) {
//                println("CURSOR TOO FAR")
                return symIn
            }
            var c = infixString[cursor]
            if (specials.keys.count { it == c } == 0) {         //c not a key, so is part of a string
                symIn.typ = STRING;
                var s = "";
                c = infixString[cursor]
                while (specials.keys.count { it == c } == 0) {  //while c is not a special char
                    s += c;
                    cursor++
                    c = infixString[cursor]
                }
                symIn.content = s
            } else if (specials[c]?.cat == Category.SKIP) {     //special char can be skipped
                cursor++
                readSymbol()                 //readsymbol advances the cursor
            } else {                                            //we have a separator
                symIn.typ = specials[c]!!.typ                 //the special character becomes the typ
                symIn.content = c.toString()
                cursor++
            }
            if (expected.isEmpty()) {
                return symIn       //default, we do not complain
            }
            if (symIn.typ in expected) {
                return symIn
            }            //check on expected char is ok, no complaints
            val expectedList = expected.contentToString()
            errorsPresent = true
            errorsLogged++
            println("SEPARATOR ERROR in line ${DecryptedLine.lineNumber} at cursor $cursor char $c ==> < $expectedList >")
            return symIn
//            throw IllegalArgumentException("separator error: < $expectedList >");
        }
    }
}
