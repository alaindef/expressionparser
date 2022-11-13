object Main {


    const val msgIn: String = "msgin.txt"

    private fun readFromTerminalAndParse(reportLevelIn:Int) {
        var reportLevelIn = 0
        var indi = ""
        println("input an expression or\n! to toggle report level or\n.. to quit")
        while (true) {
            indi = readLine() + '\n'
            if (".." in indi) {
                println("you stopped me.\nThanks for that, because I'm getting tired of you!")
                return
            }
            if ('!' in indi) {
                reportLevelIn = 1 - reportLevelIn
            }else {
                ExpressionParser.parseExp(indi.toString(), reportLevelIn)
            }
            indi = ""
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
//        readFromTerminalAndParse(0)
//        ExpressionParser.textIn ="(13 + 2*3)+(5+45)!"
//        ExpressionParser.pass1()
//        readFromTerminalAndParse(0)
//        ExpressionParser.parseExp("a+1!", 0)
//        ExpressionParser.parseExp("1a57 > 5 ?10+8:20!", 1)
        ExpressionParser.parseExp("abc/?-x+b23)!", 1)
//        ExpressionParser.parseExp("?a+b)!",1)
//        ExpressionParser.parseExp("(23+ -a)*(-3*b+2)*-c!", 0)     //error!
//        test()
    }
}