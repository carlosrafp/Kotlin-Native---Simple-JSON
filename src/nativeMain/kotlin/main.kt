import JSONObject.JSONarray
import JSONObject.JSONobject

fun main() {
    // JSON test
    val jsontest = JSONobject("{msg:\"Hello, Kotlin/Native!\"}")
    jsontest.put("msg2","Hello again")
    jsontest.put("array", JSONarray("[1,2,3,4,5]"))
    println(jsontest.getString("msg"))  //  println(jsontest.getString("msg"))
    println(jsontest.getString("msg2")) //  Hello again
    val jArray = jsontest.get("array") as JSONarray // can be replaced with jsontest.getJSONarray("array")
    val valueFromArray = jsontest.getJSONarray("array").getInt(1)
    println(jArray)  // [1,2,3,4,5]
    println(valueFromArray)  // 2
    println(jsontest)  // {"msg":"Hello, Kotlin\/Native!","msg2":"Hello again","array":[1,2,3,4,5]}
}