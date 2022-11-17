
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
                ExpressionParser.parseExp(indi.toString())
            }
            indi = ""
        }
    }

    private fun testParser(){
//        readFromTerminalAndParse(0)
//        ExpressionParser.pass1()
//        ExpressionParser.parseExp("3>9!")
//        ExpressionParser.parseExp("0?2:3!")
//        ExpressionParser.parseExp("1+2*3+4!")
//        ExpressionParser.parseExp("1a57 > (-5 -((x+(y-x3<3?x3+4:17-c)))) ? 10+8:20!")
//        ExpressionParser.parseExp("((x+(y-x3<3?x3+4:(17-c)) ? 10+8:20!")
        ExpressionParser.parseExp("((1+(2-3<4?5+6:(7-8)) ? 9+10:20!")
//        ExpressionParser.parseExp("a<b?c+d:e*5!")
//        ExpressionParser.parseExp("?a+b)!")
//        ExpressionParser.parseExp("(-13 aa 2*3)+(5+45)!")       //error!
//        ExpressionParser.parseExp("(23+ -a)*(-3*b+2)*-c!")      //error!
//        test()
    }

    private fun testInterpreter(){
        testParser()
        interpreter.readE(Pass2.symListOut)
    }

    @JvmStatic
    fun main(args: Array<String>) {
        ExpressionParser.reportLevel = 1        // level of detail in the report, can be 0,1,2
//        testParser()
        testInterpreter()
    }
}