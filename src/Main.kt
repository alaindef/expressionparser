import javax.crypto.Mac
import java.io.File
import java.io.InputStream

object Main {


    const val msgIn: String = "msgin.txt"
    fun parseFile(){
        val inputStream: InputStream = File("msgin.txt").inputStream()
        val lineList = mutableListOf<String>()

        inputStream.bufferedReader().forEachLine { lineList.add(it) }
        var lineNum = 0
        var curs = 0
        lineList.forEach{
            MachineParser.parseLine(it, lineNum)
            MachineParser.cursor = 0
            lineNum++
        }
    }
    @JvmStatic
    fun main(args: Array<String>) {

//        val msg: InputStream = File(this.msgIn).inputStream()
        parseFile()

//        MachineParser.parseLine("====!",17)
//        MachineParser.parseLine(">>>> 254!",17)
//        MachineParser.parseLine("sha:(kie valju) (kar rotzak) (kleur blauw)!",17)
//        println("the end: decrypted = ${DecryptedLine.linekey} ${DecryptedLine.lineNumber} ${DecryptedLine.key_value_pairs} ")
    }
}