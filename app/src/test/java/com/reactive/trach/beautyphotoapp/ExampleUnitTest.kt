package com.reactive.trach.beautyphotoapp

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        // Write your code here
        val s = arrayListOf("trach", "vinh", "trach", "vinh", "trach")

        val map = HashMap<String, Int>()
        val result = ArrayList<String>()
        s.forEach {
            if(map.containsKey(it)){
                result.add("$it${map[it]!!}")
                map[it] = map[it]!! + 1
            }else{
                result.add(it)
                map[it] = 1
            }
        }

        assertEquals(result, arrayOf(""))
    }
}
