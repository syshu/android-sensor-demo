package com.example.sensordemo

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
        assertEquals(4, 2 + 2)
    }

    @Test fun testFindMedian() {
        assertEquals(findMedian(3.0, 4.0, 5.0), 4.0, 0.1)
        assertEquals(findMedian(4.0, 4.0, 5.0), 4.0, 0.1)
        assertEquals(findMedian(3.0, 4.0, 4.0), 4.0, 0.1)
        assertEquals(findMedian(5.0, 4.0, 3.0), 4.0, 0.1)
        assertEquals(findMedian(4.0, 5.0, 3.0), 4.0, 0.1)
    }
}
