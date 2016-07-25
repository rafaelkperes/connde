/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.citopt.sensmonqtt.device;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bson.types.ObjectId;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author rafaelkperes
 */
public class SensorTest {

    private Device device;
    private Set<Pin> pinSet;
    private Type type;
    private ObjectId id;

    public SensorTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        id = new ObjectId();
        device = new Device("AA:BB:CC:DD");
        type = new Type();
        type.setId(id);
        pinSet = new HashSet<>();
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getType method, of class Device.
     */
    @Test
    public void testGetType() {
        System.out.println("getType");
        Sensor instance = new Sensor(device, pinSet, type);
        Type expResult = new Type();
        expResult.setId(new ObjectId(id.toByteArray()));
        Type result = instance.getType();
        assertEquals(expResult, result);
    }

    /**
     * Test of getMacAddress method, of class Device.
     */
    @Test
    public void testGetDevice() {
        System.out.println("getMacAddress");
        Sensor instance = new Sensor(device, pinSet, type);
        Device expResult = new Device("AA:BB:CC:DD");
        Device result = instance.getDevice();
        assertEquals(expResult, result);
    }

    /**
     * Test of getPinSet method, of class Device.
     */
    @Test
    public void testGetPinSet() {
        System.out.println("getPinSet");
        pinSet.add(new Pin("A0", "0"));
        Sensor instance = new Sensor(device, pinSet, type);
        Set<Pin> expResult = new HashSet<>();
        expResult.add(new Pin("A0", "0"));
        Set<Pin> result = instance.getPinSet();
        assertEquals(expResult, result);
                
        pinSet.add(new Pin("A1", "1"));
        expResult.add(new Pin("A1", "1"));
        Sensor anotherInstance = new Sensor(device, pinSet, type);
        result = anotherInstance.getPinSet();
        assertEquals(expResult, result);
    }

    /**
     * Test of getStatus method, of class Device.
     */
    @Test
    public void testGetStatus() {
        System.out.println("getStatus");
        Sensor instance = new Sensor(device, pinSet, type);
        Sensor.Status expResult = Sensor.Status.INACTIVE;
        Sensor.Status result = instance.getStatus();
        assertEquals(expResult, result);

        instance = new Sensor(device, pinSet, type, Sensor.Status.INACTIVE);
        expResult = Sensor.Status.INACTIVE;
        result = instance.getStatus();
        assertEquals(expResult, result);

        instance = new Sensor(device, pinSet, type, Sensor.Status.ACTIVE);
        expResult = Sensor.Status.ACTIVE;
        result = instance.getStatus();
        assertEquals(expResult, result);
    }

    /**
     * Test of getNetworkStatus method, of class Device.
     */
    @Test
    public void testGetNetStatus() {
        System.out.println("getNetworkStatus");
        Sensor instance = new Sensor(device, pinSet, type);
        Sensor.NetworkStatus expResult = Sensor.NetworkStatus.UNREACHABLE;
        Sensor.NetworkStatus result = instance.getNetworkStatus();
        assertEquals(expResult, result);

        instance = new Sensor(device, pinSet, type, Sensor.Status.ACTIVE);
        expResult = Sensor.NetworkStatus.UNREACHABLE;
        result = instance.getNetworkStatus();
        assertEquals(expResult, result);
    }

    /**
     * Test of setStatus method, of class Device.
     */
    @Test
    public void testSetStatus() {
        System.out.println("setStatus");
        Sensor.Status status = Sensor.Status.ACTIVE;
        Sensor instance = new Sensor(device, pinSet, type);
        instance.setStatus(status);
        assertEquals(instance.getStatus(), status);

        status = Sensor.Status.INACTIVE;
        instance.setStatus(status);
        assertEquals(instance.getStatus(), status);
        // should always be unreachable as set to INACTIVE
        assertEquals(instance.getNetworkStatus(), Sensor.NetworkStatus.UNREACHABLE);
    }

    /**
     * Test of setNetworkStatus method, of class Device.
     */
    @Test
    public void testSetNetworkStatus() {
        System.out.println("setNetworkStatus");
        Sensor.NetworkStatus netStatus = Sensor.NetworkStatus.REACHABLE;
        Sensor instance = new Sensor(device, pinSet, type, Sensor.Status.INACTIVE);
        try {
            instance.setNetworkStatus(netStatus);
            fail("Should throw IllegalArgumentException.");
        } catch (IllegalArgumentException ex) {            
        }
        
        netStatus = Sensor.NetworkStatus.REACHABLE;
        instance = new Sensor(device, pinSet, type, Sensor.Status.ACTIVE);
        instance.setNetworkStatus(netStatus);
        assertEquals(netStatus, instance.getNetworkStatus());        
    }
    
//    @Test
//    public void testGetId() {
//        System.out.println("getId");
//        Device instance = new Device(device, pinSet, type);
//        ObjectId expResult = new ObjectId(device + pinSet.toString());
//        ObjectId result = instance.getId();
//        assertEquals(expResult, result);
//    }

}