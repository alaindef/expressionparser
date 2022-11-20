
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
                reportLevelIn = if (reportLevelIn>0) 0 else 1
            }else {
                Interpreter.readE(Pass2.parse(Pass1.parse(indi)))
            }
            indi = ""
        }
    }

    private fun interpreter(){
//        readFromTerminalAndParse(0)
        Interpreter.readE(Pass2.parse(Pass1.parse("11+2!")))
//        Interpreter.readE(Pass2.parse(Pass1.parse("1<2?3:4!")))
//        Interpreter.readE(Pass2.parse(Pass1.parse("2+(1-(3<4 ?3+4:(17-5))!")))
//        Interpreter.readE(Pass2.parse(Pass1.parse("(1 + (2-3<4?5+6:(7-8) ? 9+10:20!")))      //error parenth
//        Interpreter.readE(Pass2.parse(Pass1.parse("(1 + (2-3<4?5+6:(7-8) ? 9+10:20!")))      //error parenth
//        Interpreter.readE(Pass2.parse(Pass1.parse("(2!")))                                  //error parenth
//        Pass2.parse(Pass1.parse("RPM1_IND_FAIL > 5!"))
//        Pass2.parse(Pass1.parse("1a57 > (-5 -((x+(y-x3<3?x3+4:17-c)))) ? 10+8:20!"))
//        Pass2.parse(Pass1.parse("(-13 aa 2*3)+(5+45)!"))
//        Pass2.parse(Pass1.parse("(23+ -a)*(-3*b+2)*-c!"))                                   // error
    }

    @JvmStatic
    fun main(args: Array<String>) {
        ExpressionParser.reportLevel = 0        // level of detail in the report, can be 0,1,2
        interpreter()
    }
}