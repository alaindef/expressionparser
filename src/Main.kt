import javax.crypto.Mac

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

    @JvmStatic
    fun main(args: Array<String>) {
//        MachineParser.getThings()
        println("\nparseline:" )
        MachineParser.parseLine("sha:(kie valju)",17)
        println("decrypted: ${DecryptedLine.linekey}")
    }
}
//#Instructor Station (01)
//c56e5ba5fa0cd875e0e6093772d34b8901:(description laptop_guy)				(name Guy Talemans)			(customerID 2)