import json.JSONArray
import json.JSONObject

fun main() {
    // JSON test
    val jsontest = JSONObject("{msg:\"Hello, Kotlin/Native!\"}")
    jsontest.put("msg2","Hello again")
    jsontest.put("array", JSONArray("[1,2,3,4,5]"))
    println(jsontest.getString("msg"))  //  println(jsontest.getString("msg"))
    println(jsontest.getString("msg2")) //  Hello again
    val jArray = jsontest.getJSONArray("array")
    val valueFromArray = jsontest.getJSONArray("array").getInt(1)
    println(jArray)  // [1,2,3,4,5]
    println(valueFromArray)  // 2
    println(jsontest)  // {"msg":"Hello, Kotlin\/Native!","msg2":"Hello again","array":[1,2,3,4,5]}
}