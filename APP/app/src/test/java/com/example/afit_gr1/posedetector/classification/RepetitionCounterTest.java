package com.example.afit_gr1.posedetector.classification;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class RepetitionCounterTest {

    private RepetitionCounter repetitionCounter;
    private ClassificationResult mockResult;

    @Before
    public void setUp() {
        // Initialize counter with default enter threshold (6.0) and exit threshold (4.0)
        repetitionCounter = new RepetitionCounter("squats");
        mockResult = Mockito.mock(ClassificationResult.class);
    }

    @Test
    public void testInitialState() {
        assertEquals("squats", repetitionCounter.getClassName());
        assertEquals(0, repetitionCounter.getNumRepeats());
    }

    @Test
    public void testAddClassificationResult_CompleteRepetition() {
        // Step 1: Low confidence - pose not entered
        when(mockResult.getClassConfidence("squats")).thenReturn(2.0f);
        assertEquals(0, repetitionCounter.addClassificationResult(mockResult));

        // Step 2: High confidence - pose entered (> 6.0)
        when(mockResult.getClassConfidence("squats")).thenReturn(7.5f);
        assertEquals(0, repetitionCounter.addClassificationResult(mockResult));

        // Step 3: Low confidence - pose exited (< 4.0). Repetition should increment to 1.
        when(mockResult.getClassConfidence("squats")).thenReturn(3.0f);
        assertEquals(1, repetitionCounter.addClassificationResult(mockResult));
    }

    @Test
    public void testAddClassificationResult_IncompleteRepetition() {
        // High confidence - pose entered (> 6.0)
        when(mockResult.getClassConfidence("squats")).thenReturn(8.0f);
        assertEquals(0, repetitionCounter.addClassificationResult(mockResult));

        // Medium confidence - pose still entered, but not exited (>= 4.0)
        when(mockResult.getClassConfidence("squats")).thenReturn(5.0f);
        assertEquals(0, repetitionCounter.addClassificationResult(mockResult));

        // High confidence again
        when(mockResult.getClassConfidence("squats")).thenReturn(9.0f);
        assertEquals(0, repetitionCounter.addClassificationResult(mockResult));
    }

    @Test
    public void testAddClassificationResult_MultipleRepetitions() {
        // Rep 1
        when(mockResult.getClassConfidence("squats")).thenReturn(7.0f);
        repetitionCounter.addClassificationResult(mockResult);
        when(mockResult.getClassConfidence("squats")).thenReturn(2.0f);
        assertEquals(1, repetitionCounter.addClassificationResult(mockResult));

        // Rep 2
        when(mockResult.getClassConfidence("squats")).thenReturn(8.0f);
        repetitionCounter.addClassificationResult(mockResult);
        when(mockResult.getClassConfidence("squats")).thenReturn(3.0f);
        assertEquals(2, repetitionCounter.addClassificationResult(mockResult));
    }
}
