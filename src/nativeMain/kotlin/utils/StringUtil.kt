package utils

object StringUtil {

    private val digits = charArrayOf(
        '0', '1', '2', '3', '4', '5',
        '6', '7', '8', '9', 'a', 'b',
        'c', 'd', 'e', 'f', 'g', 'h',
        'i', 'j', 'k', 'l', 'm', 'n',
        'o', 'p', 'q', 'r', 's', 't',
        'u', 'v', 'w', 'x', 'y', 'z'
    )

    fun Int.toHexString(): String{
        val buf = CharArray(4)
        formatUnsignedInt (this,4,buf,0,4)
        return buf.concatToString()
    }

    fun Byte.toHexString(): String{
        val buf = CharArray(2)
        formatUnsignedInt (this.toInt(),4,buf,0,2)
        return buf.concatToString()
    }

    fun formatUnsignedInt(valor: Int, shift: Int, buf: CharArray, offset: Int, len: Int): Int {
        var value = valor
        var charPos = len
        val radix = 1 shl shift
        val mask = radix - 1
        do {
            buf[offset + --charPos] = digits[value and mask]
            value = value ushr shift
        } while (charPos > 0)
        return charPos
    }
}