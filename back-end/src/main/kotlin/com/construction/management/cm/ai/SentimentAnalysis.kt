package com.construction.management.cm.ai

import com.construction.management.cm.dto.SentimentScore
import com.construction.management.cm.employee.EmployeeMorale
import org.springframework.stereotype.Service
import Afinn


/**
 * Intended use is to analyse employee comments, then assign a morale to the employee
 *
 * How the sentiment analysis works.
 * Based on AFINN 111 (List of English words rated for valence).
 * Words given a positive or negative rating on scale of (-5 -> 5).
 * To classify a sentence. The AFINN 111 list is read into a dictionary/map of word to score. A sentence is cleaned by removing punctuations, then transforming all words to lowercase. Each word of the sentence is looked up in the dictionary and the value is added to a total.
 * If total score is above 1 it is positive, between -1 and 1 neutral else negative.
 * To get the employee morale, all comments across assigned tasks are retrieved. And each comment is classified in the categories above. A count of each is kept ie total positive, total negative and total neutral.
 * A ratio is the derived based on the totals. If positive is above 0.6 High morale, if negative is greater than 0.4 Low morale else Medium
 */

@Service
class SentimentAnalysis {

    fun analyzeSentiment(text: String): String {
        val sentimentLib = Afinn()
        return sentimentLib.scoreToWords(text)
    }

    fun aggregateSentimentScores(comments: List<String>): SentimentScore {
        val score = SentimentScore(
            neutral = 0,
            positive = 0,
            negative = 0
        )
        comments.forEach { comment ->
            when(analyzeSentiment(comment)) {
                "neutral" -> score.neutral += 1
                "positive" -> score.positive += 1
                "negative" -> score.negative += 1
            }
        }
        return score
    }

    fun classifyEmployeeMorale(score: SentimentScore): String {
        val total = score.positive + score.neutral + score.negative
        if (total == 0) return EmployeeMorale.MEDIUM.value // Default to medium if no comments

        val positiveRatio = score.positive / total.toFloat()
        val negativeRatio = score.negative / total.toFloat()

        return when {
            positiveRatio > 0.6 -> EmployeeMorale.HIGH.value
            negativeRatio > 0.4 -> EmployeeMorale.LOW.value
            else -> EmployeeMorale.MEDIUM.value
        }
    }

    fun generateMorale(comments: List<String>): String {
        val aggregatedScore = aggregateSentimentScores(comments)
        return classifyEmployeeMorale(aggregatedScore)
    }

}