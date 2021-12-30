package json

import utils.StringReader

class JSONArray {
    private val lista: ArrayList<Any?>

    constructor() {
        lista = ArrayList()
    }

    constructor(strarray: String) : this(JSONengine(strarray)) {}

    constructor(readerArray: StringReader) : this(JSONengine(readerArray)) {}

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

    constructor(colecao: Array<*>){
        lista = arrayListOf(colecao)
    }

    fun clear() {
        lista.clear()
    }

    fun size(): Int {
        return lista.size
    }

    fun length(): Int {
        return lista.size
    }

    fun put(obj: Any?): JSONArray {
        lista.add(obj)
        return this
    }

    fun put(obj: Any?, index: Int): JSONArray {
        if (index >= lista.size) throw Exception("Index not found")
        lista[index] = obj
        return this
    }

    fun remove(index: Int): Any? {
        if (index >= 0 && index < lista.size) return lista.removeAt(index)
        return null
    }

    fun opt (index: Int): Any? {
        return if (index in 0..lista.size) lista[index]
        else null
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

    fun getJSONObject(index: Int): JSONObject {
        if (index > lista.size) throw Exception("Index not found")
        val jobject = this[index]
        if (jobject is JSONObject) {
            return jobject
        }
        throw Exception("Value is not a JSONobject")
    }

    fun getJSONArray(index: Int): JSONArray {
        if (index > lista.size) throw Exception("Index not found")
        val jobject = this[index]
        if (jobject is JSONArray) {
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
                is JSONArray -> {
                    results.add(element.toList())
                }
                is JSONObject -> {
                    results.add(element.toMap())
                }
                else -> {
                    results.add(element)
                }
            }
        }
        return results
    }

    operator fun iterator(): Iterator<Any?> {
        return this.lista.iterator()
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