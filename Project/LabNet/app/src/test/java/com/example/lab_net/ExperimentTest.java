package com.example.lab_net;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test class for Experiment class.
 *
 * @author Vidhi Patel
 */
public class ExperimentTest {

    @Test
    public void getExperimentId() {
        Experiment experiment = new Experiment("testing123","title test",
                "test description","testOwner","Edmonton",
                10,"Measurement","yes");
        assertEquals("testing123",experiment.getExperimentId());
    }

    @Test
    public void setExperimentId() {
        Experiment experiment = new Experiment("testing123","title test",
                "test description","testOwner","Edmonton",
                10,"Measurement","yes");
        experiment.setExperimentId("456testing");
        assertEquals("456testing",experiment.getExperimentId());

    }

    @Test
    public void getTitle() {
        Experiment experiment = new Experiment("testing123","title test",
                "test description","testOwner","Edmonton",
                10,"Measurement","yes");
        assertEquals("title test",experiment.getTitle());
    }

    @Test
    public void setTitle() {
        Experiment experiment = new Experiment("testing123","title test",
                "test description","testOwner","Edmonton",
                10,"Measurement","yes");
        experiment.setTitle("second title");
        assertEquals("second title", experiment.getTitle());
    }

    @Test
    public void getDescription() {
        Experiment experiment = new Experiment("testing123","title test",
                "test description","testOwner","Edmonton",
                10,"Measurement","yes");
        assertEquals("test description", experiment.getDescription());
    }

    @Test
    public void setDescription() {
        Experiment experiment = new Experiment("testing123","title test",
                "test description","testOwner","Edmonton",
                10,"Measurement","yes");
        experiment.setDescription("second description");
        assertEquals("second description",experiment.getDescription());
    }

    @Test
    public void getRegion() {
        Experiment experiment = new Experiment("testing123","title test",
                "test description","testOwner","Edmonton",
                10,"Measurement","yes");
        assertEquals("Edmonton",experiment.getRegion());
    }

    @Test
    public void setRegion() {
        Experiment experiment = new Experiment("testing123","title test",
                "test description","testOwner","Edmonton",
                10,"Measurement","yes");
        experiment.setRegion("alberta");
        assertEquals("alberta",experiment.getRegion());
    }

    @Test
    public void getOwner() {
        Experiment experiment = new Experiment("testing123","title test",
                "test description","testOwner","Edmonton",
                10,"Measurement","yes");
        assertEquals("testOwner",experiment.getOwner());
    }

    @Test
    public void setOwner() {
        Experiment experiment = new Experiment("testing123","title test",
                "test description","testOwner","Edmonton",
                10,"Measurement","yes");
        experiment.setOwner("bob");
        assertEquals("bob", experiment.getOwner());
    }

    @Test
    public void getMinTrials() {
        Experiment experiment = new Experiment("testing123","title test",
                "test description","testOwner","Edmonton",
                10,"Measurement","yes");
        assertEquals(10,experiment.getMinTrials());
    }

    @Test
    public void setMinTrials() {
        Experiment experiment = new Experiment("testing123","title test",
                "test description","testOwner","Edmonton",
                10,"Measurement","yes");
        experiment.setMinTrials(5);
        assertEquals(5,experiment.getMinTrials());
    }
}