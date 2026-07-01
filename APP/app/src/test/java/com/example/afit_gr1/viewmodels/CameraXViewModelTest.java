package com.example.afit_gr1.viewmodels;

import android.app.Application;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;

import com.example.afit_gr1.data.PostureResult;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 33)
public class CameraXViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private Application mockApplication;

    @Mock
    private Observer<Map<String, PostureResult>> postureObserver;

    @Mock
    private Observer<Boolean> triggerObserver;

    private CameraXViewModel viewModel;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        viewModel = new CameraXViewModel(mockApplication);
    }

    @Test
    public void testInitialTriggerClassificationValue() {
        // triggerClassification should initially be false
        Boolean initialValue = viewModel.getTriggerClassification().getValue();
        assertFalse(initialValue != null ? initialValue : true);
    }

    @Test
    public void testSetTriggerClassification() {
        viewModel.getTriggerClassification().observeForever(triggerObserver);
        
        viewModel.getTriggerClassification().setValue(true);
        
        verify(triggerObserver).onChanged(true);
        assertTrue(viewModel.getTriggerClassification().getValue());
    }

    @Test
    public void testSetPostureLiveData() {
        viewModel.getPostureLiveData().observeForever(postureObserver);

        Map<String, PostureResult> postureMap = new HashMap<>();
        postureMap.put("squats", new PostureResult());

        viewModel.getPostureLiveData().setValue(postureMap);

        verify(postureObserver).onChanged(postureMap);
        assertEquals(postureMap, viewModel.getPostureLiveData().getValue());
    }
}
