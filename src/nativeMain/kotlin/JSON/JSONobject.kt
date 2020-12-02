package JSONObject


class JSONobject {
    private val mapa: LinkedHashMap<String, Any?>

    constructor() {
        mapa = LinkedHashMap()
    }

    constructor(obj: JSONobject?) {
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
            var nome: String = str.nextValue().toString()
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

    fun put(name: String?, obj: Any?): Boolean {
        if (name == null) return false
        if (mapa.containsKey(name)) {
            mapa[name] = obj
            return true
        }
        mapa.put(name,obj)
        return true
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

    fun getJSONobject(name: String): JSONobject {
        val jobject = this[name]
        if (jobject is JSONobject) {
            return jobject
        }
        throw Exception("value is not a JSONobject")
    }

    fun getJSONarray(name: String): JSONarray {
        val jobject = this[name]
        if (jobject is JSONarray) {
            return jobject
        }
        throw Exception("value is not a JSONarray")
    }

    fun clear() {
        mapa.clear()
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
            if (value is JSONobject) {
                value = value.toMap()
            } else if (value is JSONarray) {
                value = value.toList()
            }
            results[nome] = value
        }
        return results
    }
}