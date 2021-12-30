package json

import utils.StringReader

class JSONObject {

    public class Null{

        fun clone(): Any {
            return this
        }
        override fun equals(other: Any?): Boolean {
            return other == null || other === this
        }
        override fun hashCode(): Int {
            return 0
        }
        override fun toString(): String {
            return "null"
        }
    }

    private val mapa: LinkedHashMap<String, Any?>

    constructor() {
        mapa = LinkedHashMap()
    }

    constructor(obj: JSONObject?) {
        if (obj == null) {
            mapa = LinkedHashMap()
        } else {
            mapa = LinkedHashMap(obj.size())
            try {
                for (nome in obj.mapa.keys) {
                    //if (nome == null) {
                    //    throw Exception("Null key.")
                    //}
                    val value = obj.mapa[nome]
                    mapa[nome] = value
                }
            } catch (e: Exception) {
                mapa.clear()
            }
        }
    }

    constructor(objfromstr: String) : this(JSONengine(objfromstr)) {}

    constructor(objFromReader: StringReader) : this(JSONengine(objFromReader)) {}

    constructor(str: JSONengine) {
        mapa = LinkedHashMap()
        var classe: Char = str.nextChar()
        if (classe != '{') return else if (str.size() == 2) {
            classe = str.nextChar()
            if (classe == '}') return  // {}
            throw Exception("Molformed JSONobject") //??
        }
        while (true) {
            classe = str.nextChar()
            if (classe == '}') break else if (classe == ',' || classe == ';') {
                continue
            }
            str.back()
            val nome: String = str.nextValue().toString()
            classe = str.nextChar()
            if (classe != ':') {
                clear() /// not a valid element
                return
            }
            try {
                val valor: Any? = str.nextValue()
                if (mapa.containsKey(nome)) {
                    clear()
                    return
                }
                put(nome, valor)
            } catch (e: Exception) {
                clear()
                return
            }
        }
    }

    fun put(name: String?, obj: Any?): JSONObject? {
        if (name == null) return null
        if (mapa.containsKey(name)) {
            mapa[name] = obj
            return this
        }
        mapa[name] = obj
        return this
    }

    fun accumulate(key: String?, value: Any?): JSONObject? {
        //JSONObject.testValidity(value)
        if (key == null) return null
        when (val obj = opt(key)) {
            null -> {
                put(
                    key,
                    if (value is JSONArray) JSONArray().put(value) else value
                )
            }
            is JSONArray -> {
                obj.put(value)
            }
            else -> {
                put(key, JSONArray().put(obj).put(value))
            }
        }
        return this
    }

    fun remove(nome: String): Boolean {
        if (mapa.containsKey(nome)) {
            mapa.remove(nome)
            return true
        }
        return false
    }

    operator fun get(nome: String): Any? {
        if (mapa.containsKey(nome)) {
            return mapa[nome]
        }
        throw Exception("Key name not found")
    }

    fun opt(nome: String): Any? {
        return if (mapa.containsKey(nome)) {
            mapa[nome]
        } else null
    }

    @Throws(Exception::class)
    fun getString(name: String): String {
        val jobject = this[name]
        if (jobject is String) {
            return jobject
        }
        throw Exception("value is not a String")
    }

    fun optString (name: String): String {
        val entry = opt(name)
        return entry?.toString() ?: ""
    }

    fun optBoolean(key: String?): Boolean {
        return optBoolean(key, false)
    }

    fun optBoolean(key: String?, defaultValue: Boolean): Boolean {
        val value = opt(key!!)
        if (Null() == value) {
            return defaultValue
        }
        return if (value is Boolean) {
            value
        } else try {
            // we'll use the get anyway because it does string conversion.
            getBoolean(key)
        } catch (e: Exception) {
            defaultValue
        }
    }

    fun optNumber(key: String): Number? {
        return this.optNumber(key, null)
    }

    fun optNumber(key: String, defaultValue: Number?): Number? {
        val value = opt(key)
        if (Null() == value) {
            return defaultValue
        }
        return if (value is Number) {
            value
        } else try {
            stringToNumber(value.toString())
        } catch (e: Exception) {
            defaultValue
        }
    }

    fun optInt(key: String): Int {
        return this.optInt(key, 0)
    }

    fun optInt(key: String, defaultValue: Int): Int {
        val valor: Number = this.optNumber(key, null) ?: return defaultValue
        return valor.toInt()
    }

    fun optDouble(key: String): Double {
        return optDouble(key, Double.NaN)
    }

    fun optDouble(key: String, defaultValue: Double): Double {
        val value = this.optNumber(key) ?: return defaultValue
        // if (Double.isNaN(doubleValue) || Double.isInfinite(doubleValue)) {
        // return defaultValue;
        // }
        return value.toDouble()
    }

    fun optJSONArray(key: String): JSONArray? {
        val o = opt(key)
        return if (o is JSONArray) o else null
    }

    fun optJSONObject(key: String): JSONObject? {
        val o = opt(key)
        return if (o is JSONObject) o else null
    }

    @Throws(Exception::class)
    fun getInt(name: String): Int {
        val jobject = this[name]
        return if (jobject is Number) {
            jobject.toInt()
        } else try {
            jobject.toString().toInt()
        } catch (e: Exception) {
            throw Exception("value is not an Integer")
        }
    }

    @Throws(Exception::class)
    fun getFloat(name: String): Float {
        val jobject = this[name]
        return if (jobject is Number) {
            jobject.toFloat()
        } else try {
            jobject.toString().toFloat()
        } catch (e: Exception) {
            throw Exception("value is not a float")
        }
    }

    @Throws(Exception::class)
    fun getDouble(name: String): Double {
        val jobject = this[name]
        return if (jobject is Number) {
            jobject.toDouble()
        } else try {
            jobject.toString().toDouble()
        } catch (e: Exception) {
            throw Exception("value is not a double")
        }
    }

    fun getLong(name: String): Long {
        val jobject = this[name]
        return if (jobject is Number) {
            jobject.toLong()
        } else try {
            jobject.toString().toLong()
        } catch (e: Exception) {
            throw Exception("value is not a Long")
        }
    }

    fun getBoolean(name: String): Boolean {
        val jobject = this[name]
        if (jobject == false || jobject is String && jobject
                .equals("false", ignoreCase = true)) {
            return false
        } else if (jobject == true || jobject is String && jobject
                .equals("true", ignoreCase = true)) {
            return true
        }
        throw Exception("value is not a boolean")
    }

    fun getJSONObject(name: String): JSONObject {
        val jobject = this[name]
        if (jobject is JSONObject) {
            return jobject
        }
        throw Exception("value is not a JSONobject")
    }

    fun getJSONArray(name: String): JSONArray {
        val jobject = this[name]
        if (jobject is JSONArray) {
            return jobject
        }
        throw Exception("value is not a JSONarray")
    }

    fun stringToValue(string: String): Any? {
        if ("" == string) {
            return string
        }

        // check JSON key words true/false/null
        if ("true".equals(string, ignoreCase = true)) {
            return true
        }
        if ("false".equals(string, ignoreCase = true)) {
            return false
        }
        if ("null".equals(string, ignoreCase = true)) {
            return Null()
        }

        /*
         * If it might be a number, try converting it. If a number cannot be
         * produced, then the value will just be a string.
         */
        val initial = string[0]
        if (initial in '0'..'9' || initial == '-') {
            try {
                return stringToNumber(string)
            } catch (ignore: Exception) {
            }
        }
        return string
    }

    private fun isDecimalNotation(valor: String): Boolean {
        return valor.indexOf('.') > -1 || valor.indexOf('e') > -1 || valor.indexOf('E') > -1 || "-0" == valor
    }

    @Throws(NumberFormatException::class)
    private fun stringToNumber(valor: String): Number {
        val initial = valor[0]
        if (initial in '0'..'9' || initial == '-') {
            // decimal representation
            if (isDecimalNotation(valor)) {
                try {
                    val d: Double = valor.toDouble()
                    if (d.isNaN() || d.isInfinite()) {
                        throw NumberFormatException("val [$valor] is not a valid number.")
                    }
                    return d
                } catch (ignore: NumberFormatException) {
                    throw NumberFormatException("val [$valor] is not a valid number.")
                }
            }
            // block items like 00 01 etc. Java number parsers treat these as Octal.
            if (initial == '0' && valor.length > 1) {
                val at1 = valor[1]
                if (at1 in '0'..'9') {
                    throw NumberFormatException("val [$valor] is not a valid number.")
                }
            } else if (initial == '-' && valor.length > 2) {
                val at1 = valor[1]
                val at2 = valor[2]
                if (at1 == '0' && at2 >= '0' && at2 <= '9') {
                    throw NumberFormatException("val [$valor] is not a valid number.")
                }
            }
            val l: Long = valor.toLong()
            if (l <= Int.MAX_VALUE && l >= Int.MIN_VALUE) {
                return valor.toInt()
            }
            return l
        }
        throw NumberFormatException("val [$valor] is not a valid number.")
    }

    fun clear() {
        mapa.clear()
    }

    fun length(): Int {
        return this.mapa.size
    }

    fun keySet(): Set<String>{
        return this.mapa.keys.toSet()
    }

    fun keys(): Iterator<String> {
        return keySet().iterator()
    }

    override fun toString(): String {
        val str = StringBuilder()
        str.append("{")
        var started = false
        for (nome in mapa.keys) {
            if (started) str.append(",") else started = true
            str.append("\"")
            str.append(nome)
            str.append("\":")
            val obj = mapa[nome]
            if (obj == null) str.append("null") else if (obj is String) {
                str.append(JSONengine.escapeString(obj))
            } else str.append(obj.toString())
        }
        str.append("}")
        return str.toString()
    }

    fun size(): Int {
        return mapa.size
    }

    fun toMap(): Map<String, Any?> {
        val results: MutableMap<String, Any?> = HashMap()
        for (nome in mapa.keys) {
            var value = mapa[nome]
            if (value is JSONObject) {
                value = value.toMap()
            } else if (value is JSONArray) {
                value = value.toList()
            }
            results[nome] = value
        }
        return results
    }
}