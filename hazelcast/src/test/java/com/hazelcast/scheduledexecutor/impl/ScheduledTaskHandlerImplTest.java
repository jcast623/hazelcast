/*
 * Copyright (c) 2008, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hazelcast.scheduledexecutor.impl;

import com.hazelcast.nio.Address;
import com.hazelcast.scheduledexecutor.ScheduledTaskHandler;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

@RunWith(HazelcastParallelClassRunner.class)
@Category({QuickTest.class, ParallelTest.class})
public class ScheduledTaskHandlerImplTest {

    @Test(expected = NullPointerException.class)
    public void of_withNull()
            throws Exception {
        ScheduledTaskHandler.of(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void of_withWrongURN()
            throws Exception {
        ScheduledTaskHandler.of("iamwrong");
    }

    @Test(expected = IllegalArgumentException.class)
    public void of_withWrongBase()
            throws Exception {
        ScheduledTaskHandler.of("wrongbase:-\u00000\u0000Scheduler\u0000Task");
    }

    @Test(expected = IllegalArgumentException.class)
    public void of_withWrongParts()
            throws Exception {
        ScheduledTaskHandler.of("urn:hzScheduledTaskHandler:-\u00000\u0000Scheduler");
    }

    @Test
    public void of_withValidPartition()
            throws Exception {
        ScheduledTaskHandler handler = ScheduledTaskHandler.of("urn:hzScheduledTaskHandler:-\u00000\u0000Scheduler\u0000Task");
        assertTrue(handler.isAssignedToPartition());
        assertEquals(0, handler.getPartitionId());
        assertEquals(null, handler.getAddress());
        assertEquals("Scheduler", handler.getSchedulerName());
        assertEquals("Task", handler.getTaskName());
    }

    @Test
    public void of_withValidAddress()
            throws Exception {
        Address addr = new Address("127.0.0.1", 0);
        ScheduledTaskHandler handler = ScheduledTaskHandler.of("urn:hzScheduledTaskHandler:127.0.0.1:0 -1 Scheduler Task");
        assertTrue(handler.isAssignedToMember());
        assertEquals(-1, handler.getPartitionId());
        assertEquals(addr, handler.getAddress());
        assertEquals("Scheduler", handler.getSchedulerName());
        assertEquals("Task", handler.getTaskName());
    }

    @Test(expected = IllegalArgumentException.class)
    public void of_withInvalidAddress()
            throws Exception {
        ScheduledTaskHandler.of("urn:hzScheduledTaskHandler:foobar:0 -1 Scheduler Task");
    }

    @Test
    public void of_toURN()
            throws Exception {
        String initialURN = "urn:hzScheduledTaskHandler:127.0.0.1:0 -1 Scheduler Task";
        assertEquals(initialURN, ScheduledTaskHandler.of(initialURN).toUrn());
    }

    @Test
    public void of_equality()
            throws Exception {
        String initialURN = "urn:hzScheduledTaskHandler:127.0.0.1:0 -1 Scheduler Task";
        assertEquals(ScheduledTaskHandler.of(initialURN), ScheduledTaskHandler.of(initialURN));
    }

    @Test
    public void of_equalitySameRef()
            throws Exception {
        String initialURN = "urn:hzScheduledTaskHandler:127.0.0.1:0 -1 Scheduler Task";
        ScheduledTaskHandler handler = ScheduledTaskHandler.of(initialURN);
        assertEquals(handler, handler);
    }

    @Test
    public void of_equalityDifferentAddress()
            throws Exception {
        String urnA = "urn:hzScheduledTaskHandler:127.0.0.1:0 -1 Scheduler Task";
        String urnB = "urn:hzScheduledTaskHandler:127.0.0.1:1 -1 Scheduler Task";
        assertNotEquals(ScheduledTaskHandler.of(urnA), ScheduledTaskHandler.of(urnB));
    }

    @Test
    public void of_equalityDifferentTypes()
            throws Exception {
        String urnA = "urn:hzScheduledTaskHandler:127.0.0.1:0 -1 Scheduler Task";
        String urnB = "urn:hzScheduledTaskHandler:- 2 Scheduler Task";
        assertNotEquals(ScheduledTaskHandler.of(urnA), ScheduledTaskHandler.of(urnB));
    }

    @Test
    public void of_equalityDifferentSchedulers()
            throws Exception {
        String urnA = "urn:hzScheduledTaskHandler:127.0.0.1:0 -1 Scheduler Task";
        String urnB = "urn:hzScheduledTaskHandler:127.0.0.1:0 -1 Scheduler2 Task";
        assertNotEquals(ScheduledTaskHandler.of(urnA), ScheduledTaskHandler.of(urnB));
    }

    @Test
    public void of_equalityDifferentTasks()
            throws Exception {
        String urnA = "urn:hzScheduledTaskHandler:127.0.0.1:0 -1 Scheduler Task";
        String urnB = "urn:hzScheduledTaskHandler:127.0.0.1:0 -1 Scheduler Task2";
        assertNotEquals(ScheduledTaskHandler.of(urnA), ScheduledTaskHandler.of(urnB));
    }

    @Test
    public void of_equalityNull()
            throws Exception {
        String urnA = "urn:hzScheduledTaskHandler:127.0.0.1:0 -1 Scheduler Task";
        assertNotEquals(null, ScheduledTaskHandler.of(urnA));
    }

    @Test
    public void of_addressConstructor()
            throws Exception {
        Address addr = new Address("127.0.0.1", 0);
        ScheduledTaskHandler handler = ScheduledTaskHandlerImpl.of(addr, "Scheduler", "Task");

        String expectedURN = "urn:hzScheduledTaskHandler:127.0.0.1:0 -1 Scheduler Task";

        assertTrue(handler.isAssignedToMember());
        assertEquals(-1, handler.getPartitionId());
        assertEquals(addr, handler.getAddress());
        assertEquals("Scheduler", handler.getSchedulerName());
        assertEquals("Task", handler.getTaskName());
        assertEquals(expectedURN, handler.toUrn());
    }

    @Test
    public void of_partitionConstructor()
            throws Exception {
        ScheduledTaskHandler handler = ScheduledTaskHandlerImpl.of(2, "Scheduler1", "Task1");

        String expectedURN = "urn:hzScheduledTaskHandler:- 2 Scheduler1 Task1";

        assertTrue(handler.isAssignedToPartition());
        assertEquals(2, handler.getPartitionId());
        assertEquals(null, handler.getAddress());
        assertEquals("Scheduler1", handler.getSchedulerName());
        assertEquals("Task1", handler.getTaskName());
        assertEquals(expectedURN, handler.toUrn());
    }
}