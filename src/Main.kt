import java.io.File
import java.io.InputStream
import MachineParser.*
import MachineParser.Companion.cursor
import MachineParser.Companion.parseLine

object Main {


    const val msgIn: String = "msgin.txt"
    fun parseFile(){
        val inputStream: InputStream = File("msgin.txt").inputStream()
        val lineList = mutableListOf<String>()

        inputStream.bufferedReader().forEachLine { lineList.add(it) }
        var lineNum = 0
        lineList.forEach{
            parseLine(it)
            cursor = 0
            lineNum++
        }
    }
    @JvmStatic
    fun main(args: Array<String>) {

//        val msg: InputStream = File(this.msgIn).inputStream()
//        parseFile()

        MachineParser.parseLine("a > 2 ? 10 : 20\n")
//        MachineParser.parseLine("13+2*3+5!")
//        MachineParser.parseLine("13*333!")
//        MachineParser.parse("#commetaar dd")
//        MachineParser.parseLine("sha:(kie valju) (kar rotzak) (kleur blauw)!",17)
    }
}