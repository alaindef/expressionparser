object Main {
    /* Backus Naur:
    <symbol>	::= <HASHTAG> | <LINEENDKEY> | <PAIR_START> | <PAIR_END> | <STRING> | <ETX> | <STRING>
    <line>			::= <commentline> | <linekey> <linebody>
    <commentline>	::= <HASHTAG> <anything>
    <linekey>		::= <STRING> <LINEENDKEY>
    <linebody>		::= <key_value_pair> | <key_value_pair> <linebody>
    <key_value_pair>::= <PAIR_START> <key> < value> <PAIR_END>
    <key>			::=
    */
    private fun init() {
        println("ikke ook")

        return
    }

    @JvmStatic
    fun main(args: Array<String>) {
        MachineParser.getThings()
        val ppp: MachineParser = MachineParser()
        val mp  = DecriptedLine(555)
        val paar : IntArray = intArrayOf(2,3)
        println("\nHello world!")
//        MachineParser.getThings()

        val teut = MachineParser()

        val aaa = teut.getSpecials()
        println(aaa[':']?.name)
        println("teut = ${MachineParser.teut1}")
    }
}