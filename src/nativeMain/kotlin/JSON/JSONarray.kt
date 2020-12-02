package JSONObject



class JSONarray {
    private val lista: ArrayList<Any?>

    constructor() {
        lista = ArrayList()
    }

    constructor(strarray: String) : this(JSONengine(strarray)) {}
    constructor(str: JSONengine) {
        lista = ArrayList()
        var classe = str.nextChar()
        if (classe != '[') return else if (str.size() == 2) {
            classe = str.nextChar()
            if (classe == ']') return  // []
            throw Exception("Malformed array")
        }
        while (true) {
            classe = str.nextChar()
            if (classe == ']') break else if (classe == ',') {
                continue
            }
            try {
                str.back()
                val value = str.nextValue()
                lista.add(value)
            } catch (e: Exception) {
                clear()
                return
            }
        }
    }

    fun clear() {
        lista.clear()
    }

    fun size(): Int {
        return lista.size
    }

    fun put(obj: Any?): Boolean {
        return lista.add(obj)
    }

    fun put(obj: Any?, index: Int): Boolean {
        if (index >= lista.size) throw Exception("Index not found")
        lista[index] = obj
        return true
    }

    fun remove(index: Int): Boolean {
        if (index >= lista.size) return false
        lista.removeAt(index)
        return true
    }

    operator fun get(index: Int): Any? {
        if (index > lista.size) throw Exception("Index not found")
        return lista[index]
    }

    fun getString(index: Int): String {
        if (index > lista.size) throw Exception("Index not found")
        val jobject = this[index]
        if (jobject is String) {
            return jobject
        }
        throw Exception("Value is not a String")
    }

    fun getInt(index: Int): Int {
        if (index > lista.size) throw Exception("Index not found")
        val jobject = this[index]
        return if (jobject is Number) {
            jobject.toInt()
        } else try {
            jobject.toString().toInt()
        } catch (e: Exception) {
            throw Exception("Value is not an Integer")
        }
    }

    fun getFloat(index: Int): Float {
        if (index > lista.size) throw Exception("Index not found")
        val jobject = this[index]
        return if (jobject is Number) {
            jobject.toFloat()
        } else try {
            jobject.toString().toFloat()
        } catch (e: Exception) {
            throw Exception("Value is not a Float")
        }
    }

    fun getDouble(index: Int): Double {
        if (index > lista.size) throw Exception("Index not found")
        val jobject = this[index]
        return if (jobject is Number) {
            jobject.toDouble()
        } else try {
            jobject.toString().toDouble()
        } catch (e: Exception) {
            throw Exception("Value is not a Double")
        }
    }

    fun getLong(index: Int): Long {
        if (index > lista.size) throw Exception("Index not found")
        val jobject = this[index]
        return if (jobject is Number) {
            jobject.toLong()
        } else try {
            jobject.toString().toLong()
        } catch (e: Exception) {
            throw Exception("Value is not a Long")
        }
    }

    fun getBoolean(index: Int): Boolean {
        if (index > lista.size) throw Exception("Index not found")
        val jobject = this[index]
        if (jobject == false || jobject is String && jobject
                        .equals("false", ignoreCase = true)) {
            return false
        } else if (jobject == true || jobject is String && jobject
                        .equals("true", ignoreCase = true)) {
            return true
        }
        throw Exception("Value is not an Boolean")
    }

    fun getJSONobject(index: Int): JSONobject {
        if (index > lista.size) throw Exception("Index not found")
        val jobject = this[index]
        if (jobject is JSONobject) {
            return jobject
        }
        throw Exception("Value is not a JSONobject")
    }

    fun getJSONarray(index: Int): JSONarray {
        if (index > lista.size) throw Exception("Index not found")
        val jobject = this[index]
        if (jobject is JSONarray) {
            return jobject
        }
        throw Exception("Value is not a JSONobject")
    }

    fun toList(): List<Any?> {
        val results: MutableList<Any?> = ArrayList(lista.size)
        for (element in lista) {
            when (element) {
                null -> {
                    results.add(null)
                }
                is JSONarray -> {
                    results.add(element.toList())
                }
                is JSONobject -> {
                    results.add(element.toMap())
                }
                else -> {
                    results.add(element)
                }
            }
        }
        return results
    }

    override fun toString(): String {
        val array = StringBuilder()
        array.append("[")
        var continuacao = false
        for (obj in lista) {
            if (continuacao) array.append(",") else continuacao = true
            when (obj) {
                null -> array.append("null")
                is String -> {
                    /*String str = (String) obj;
                    if (str.contains("\""))
                        str = str.replaceAll("\"","\\\\\"");
                    if (str.contains("/"))
                        str = str.replaceAll("/","\\/");
                    array.append("\"" + str + "\"");

                     */
                    array.append(JSONengine.escapeString((obj)))
                }
                else -> array.append(obj.toString())
            }
        }
        array.append("]")
        return array.toString()
    }

}