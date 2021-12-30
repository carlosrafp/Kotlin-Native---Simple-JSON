package json

import utils.StringReader
import utils.StringUtil.toHexString

class JSONengine {
    private val str: StringReader
    private val EOF = 0.toChar()

    constructor() {
        str = StringReader()
    }

    constructor(string: String) {
        str = StringReader(string)
    }

    constructor(reader: StringReader){
        str = reader
    }

    fun nextString(sb: StringBuilder): Int {
        var c: Char
        while (true) {
            c = str.nextChar()
            when (c) {
                '\u0000', '\n', '\r' -> throw Exception("Malformed String")
                '\\' -> {
                    c = str.nextChar()
                    when (c) {
                        'b' -> sb.append('\b')
                        't' -> sb.append('\t')
                        'n' -> sb.append('\n')
                        'r' -> sb.append('\r')
                        'u' -> try {
                            sb.append(str.readString(4).toInt(16).toChar())
                            //pos += 4
                        } catch (e: NumberFormatException) {
                            throw Exception("Illegal escape at ${str.getPos()-1}")
                        }
                        '\"', '\'', '\\', '/' -> sb.append(c)
                        else -> throw Exception("Illegal escape at ${str.getPos()-1}")
                    }
                }
                else -> {
                    if (c == '\"') {
                        //return pos++
                        return str.getPos()
                    }
                    sb.append(c)
                }
            }
            //pos++
        }
    }

    fun nextChar(): Char {
        var c: Char
        while (true) {
            c = str.nextChar()
            if (c == EOF) break
            if (c > ' ') {
                return c
            }
        }
        return EOF
    }

    fun nextAny(): Char {
        var c: Char
        while (true) {
            c = str.nextChar()
            if (c == EOF) break
            if (c >= ' ') {
                return c
            }
        }
        return EOF
    }

    fun Actualchar(): Char {
        var c: Char
        while (true) {
            str.mark(1)
            c = str.nextChar()
            if (c == EOF) break
            if (c > ' ') {
                return c
            }
        }
        return EOF
    }

    fun back() {
        str.seek(str.getPos() - 1)
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
                JSONObject(this)
            }
            '[' -> {
                back()
                JSONArray(this)
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
        return str.size()
    }

    companion object {
        fun escapeString(string: String): String {
            var c: Char = 0.toChar()
            var b: Char
            val tam = string.length
            val sb = StringBuilder()
            sb.append('\"')
            for (i in 0 until tam) {
                b = c
                c = string[i]
                when (c) {
                    '\\', '\"' -> {
                        sb.append('\\')
                        sb.append(c)
                    }
                    '/' ->{
                        if (b == '<') {
                            sb.append('\\')
                        }
                        sb.append(c)
                    }
                    '\b' -> sb.append("\\b")
                    '\t' -> sb.append("\\t")
                    '\n' -> sb.append("\\n")
                    '\u000C' -> sb.append("\\f")
                    '\r' -> sb.append("\\r")
                    else -> {
                        if (c < ' ' || c in '\u0080'..'\u00a0'
                            || c in '\u2000'..'\u2100'
                        ) {
                            sb.append("\\u")
                            val hexString = c.toInt().toHexString()
                            sb.append(hexString)
                        } else {
                            sb.append(c)
                        }
                    }
                }
            }
            sb.append('\"')
            return sb.toString()
        }
    }
}