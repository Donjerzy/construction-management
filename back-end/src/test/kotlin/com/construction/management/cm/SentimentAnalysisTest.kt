package com.construction.management.cm

import com.construction.management.cm.ai.SentimentAnalysis
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test


class SentimentAnalysisTest {

    @Test
    fun `Test a positive sentence`() {
        val analyzer = SentimentAnalysis()
        assertEquals( "positive",analyzer.analyzeSentiment("I am happy today"))
    }

    @Test
    fun `Test a negative sentence`() {
        val analyzer = SentimentAnalysis()
        assertEquals( "negative",analyzer.analyzeSentiment("You are useless! Idiot"))
    }


}