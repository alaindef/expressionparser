object Main {


    const val msgIn: String = "msgin.txt"

    private fun test() {
        var a = 0
        a = -3 + 1 - 2
        println(a)
    }

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
//        ExpressionParser.parseExp("-1+4!", 0)
//        ExpressionParser.parseExp("1*2+(5-6)!",0)
        ExpressionParser.parseExp("(23+ -a)*(-3*b+2)*-c!", 0)
//        test()


    }
}