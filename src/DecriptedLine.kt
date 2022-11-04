import java.awt.SystemColor.text
import java.util.*

class DecriptedLine {

    var tileIndex = 0
    var text: String = ""
    private var newLeft = 0

    constructor(context: Int) : super(
    )

    fun map(key: String, value: String): String {
        val res:  String = "teut"
        text = key
        return (text + res)
    }



    fun sent(par: Array<Int>) {

    }

    companion object {
        private fun randomizeArray(len: Int): IntArray {
            val rgen = Random() // Random number generator
            val array = IntArray(len)
            for (i in 0 until len) array[i] = i
            for (i in 0 until len) {
                val randomPosition = rgen.nextInt(len)
                val temp = array[i]
                array[i] = array[randomPosition]
                array[randomPosition] = temp
            }
            return array
        }
    }
}