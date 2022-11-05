import java.awt.SystemColor.text
import java.util.*
import kotlin.collections.HashMap

class DecryptedLine {
    companion object {
        var lineNumber: Int = 0
        var linekey: String? = null
        var key_value_pairs: kotlin.collections.HashMap<String,String>? = null
    }
}