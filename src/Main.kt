import java.io.File
import java.io.InputStream
import ExpressionParser

object Main {


    const val msgIn: String = "msgin.txt"

    private fun readFromTerminalAndParse(){
        ExpressionParser.errorLevel = 0
        var indi = ""
        println("input an expression or\n! to increase report level or\n.. to quit")
        while (true) {
            indi = readLine() + '\n'
            if (".." in indi) {
                println("you stopped me.\nThanks for that, because I'm getting tired of you!")
                return
            }
            if ('!' in indi) {ExpressionParser.errorLevel = 1}
            ExpressionParser.parseExp(indi.toString())
            indi =""
        }
    }
    @JvmStatic
    fun main(args: Array<String>) {
//        ExpressionParser.textIn ="(13 + 2*3)+(5+45)!"
//        ExpressionParser.pass1()
readFromTerminalAndParse()
//        ExpressionParser.parseExp("1+2*(3+4)*(1+2)")
//        ExpressionParser.parseExp("1*2?3/4")
//        ExpressionParser.parseExp("(23+1)*(3*3+2)")


    }
}