package com.stefang.app.feature.currency

import org.junit.Test

class MyTest {
    @Test
    fun test() {
        val test = PaypayTest()
        test.findAllUniqueTriplets(
            intArrayOf(-1,0,1,2,-1,-4)
        ).forEach {
            println(it.contentToString())
        }
    }
}

class PaypayTest {
    /*
    Given an array nums of n integers,
    are there elements a, b, c in nums such that a + b + c = 0?
    Find all unique triplets in the array which gives the sum of zero.
    e.g.
    Input: [-1,0,1,2,-1,-4]
    Output: [[-1,-1,2],[-1,0,1]]

    If input empty array, output will return empty array.
    Input: []
    Output: []
     */
    fun findAllUniqueTriplets(nums: IntArray): List<IntArray> {
        val result = mutableListOf<IntArray>()
        nums.sort()

        for (a in nums.indices) {
            var b = a + 1
            var c = nums.size - 1
            while (b < c) {
                val sum = nums[a] + nums[b] + nums[c]
                if (sum == 0) {
                    result.add(intArrayOf(nums[a], nums[b], nums[c]))
                    while (b < c && nums[b] != nums[b-1]) {
                        b++
                    }
                    b++
                    c--
                } else if (sum < 0) {
                    b++
                } else {
                    c--
                }
            }
        }

        return result
    }
}
