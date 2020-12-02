package JSONObject

class JSONengine {
    private val str: StringBuilder
    private var pos: Int
    private val len: Int

    constructor() {
        str = StringBuilder()
        pos = 0
        len = 0
    }

    constructor(string: String) {
        str = StringBuilder(string)
        pos = 0
        len = string.length
    }

    fun nextString(sb: StringBuilder): Int {
        var c: Char
        while (true) {
            c = str[pos]
            when (c) {
                '\u0000', '\n', '\r' -> throw Exception("Malformed String")
                '\\' -> {
                    c = str[++pos]
                    when (c) {
                        'b' -> sb.append('\b')
                        't' -> sb.append('\t')
                        'n' -> sb.append('\n')
                        'r' -> sb.append('\r')
                        'u' -> try {
                            sb.append(str.substring(pos + 1, pos + 5).toInt(16).toChar())
                            pos += 4
                        } catch (e: NumberFormatException) {
                            throw Exception("Illegal escape at $pos")
                        }
                        '\"', '\'', '\\', '/' -> sb.append(c)
                        else -> throw Exception("Illegal escape at $pos")
                    }
                }
                else -> {
                    if (c == '\"') {
                        return pos++
                    }
                    sb.append(c)
                }
            }
            pos++
        }
    }

    fun nextChar(): Char {
        var c: Char
        while (pos < len) {
            c = str[pos++]
            if (c > ' ') {
                return c
            }
        }
        return 0.toChar()
    }

    fun nextAny(): Char {
        var c: Char
        while (pos < len) {
            c = str[pos++]
            if (c >= ' ') {
                return c
            }
        }
        return 0.toChar()
    }

    fun Actualchar(): Char {
        var c: Char
        while (pos < len) {
            c = str[pos]
            if (c > ' ') {
                return c
            }
        }
        return 0.toChar()
    }

    fun back() {
        pos--
    }

    fun nextValue(): Any? {
        var classe = nextChar() // dont accept space char
        return when (classe) {
            '\"' -> {
                val sb = StringBuilder()
                nextString(sb)
                sb.toString()
            }
            '{' -> {
                back()
                JSONobject(this)
            }
            '[' -> {
                back()
                JSONarray(this)
            }
            else -> {
                val s = StringBuilder()
                while (classe >= ' ' && ",:]};".indexOf(classe) < 0) {
                    s.append(classe)
                    classe = nextAny() // accepts space char
                }
                back()
                val valor = s.toString().trim { it <= ' ' }
                if (valor == "") throw Exception("Key value not found")
                if ("true".equals(valor, ignoreCase = true)) {
                    return true
                }
                if ("false".equals(valor, ignoreCase = true)) {
                    return false
                }
                if ("null".equals(valor, ignoreCase = true)) {
                    return null
                }
                classe = valor[0]
                if (classe in '0'..'9' || classe == '-') {  // number
                    try {
                        if (valor.indexOf('.') > -1 || valor.indexOf('e') > -1 || valor.indexOf('E') > -1) {
                            val d = valor.toDouble()
                            if (!d.isInfinite() && !d.isNaN()) {
                                return d
                            }
                        } else {
                            val myLong = valor.toLong()
                            if (valor == myLong.toString()) {
                                return if (myLong == myLong.toInt().toLong()) {
                                    myLong.toInt()
                                } else myLong
                            }
                        }
                    } catch (e: Exception) {
                        /// returns whatever found as a key...
                    }
                }
                valor // try as a string...
            }
        }
    }

    fun size(): Int {
        return len
    }

    companion object {
        fun escapeString(string: String): String {
            var c: Char
            val tam = string.length
            val sb = StringBuilder()
            sb.append('\"')
            for (i in 0 until tam) {
                c = string[i]
                when (c) {
                    '\t', '\"', '\'', '\\', '/' -> {
                        sb.append('\\')
                        sb.append(c)
                    }
                    else -> sb.append(c)
                }
            }
            sb.append('\"')
            return sb.toString()
        }
    }
}