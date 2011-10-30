/*******************************************************************************
 * Copyright 2011 Adrian Cristian Ionescu
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package ro.zg.mdb.test.robo;

import java.util.HashSet;
import java.util.Set;

import ro.zg.mdb.core.meta.SchemaManager;
import ro.zg.mdb.persistence.MemoryPersistenceManager;
import ro.zg.util.statistics.Monitor;

public class RoboSimulation {
    private static MemoryPersistenceManager mpm = new MemoryPersistenceManager();
    private SchemaManager dao;
    private Set<RobotManager> robots = new HashSet<RobotManager>();
    private int maxSlotPos = 100;
    private int minSlotPos = 0;
    private int robotsPerType = 100;
    private int maxTurns = 100;
    private SlotManager slotManager;

    public RoboSimulation(SchemaManager dao) {
	super();
	this.dao = dao;
    }

    public void test1() throws InterruptedException {
	maxSlotPos = 100;
	minSlotPos = 0;
	robotsPerType = 100;
	maxTurns = 100;

	slotManager = new SlotManager(dao);
	Robot r = null;
	for (int i = 0; i < robotsPerType; i++) {
	    r = new SlotCreatorRobot(getRandomPos());
	    startRobot(r);
	    r = new SlotUpdaterRobot(getRandomPos());
	    startRobot(r);
	    r = new SlotReaderRobot(getRandomPos());
	    startRobot(r);
	    r = new SlotDestroyerRobot(getRandomPos());
	    startRobot(r);
	}

	System.out.println("running...");
	for (RobotManager rm : robots) {
	    rm.join();
	}
    }
    
    public void test2() throws InterruptedException {
	maxSlotPos = 1;
	minSlotPos = 0;
	robotsPerType = 100;
	maxTurns = 100;

	slotManager = new SlotManager(dao);
	Robot r = null;
	for (int i = 0; i < robotsPerType; i++) {
	    r = new SlotCreatorRobot(getRandomPos(),false);
	    startRobot(r);
	    r = new SlotUpdaterRobot(getRandomPos(),false);
	    startRobot(r);
	    r = new SlotReaderRobot(getRandomPos(),false);
	    startRobot(r);
	    r = new SlotDestroyerRobot(getRandomPos(),false);
	    startRobot(r);
	}

	System.out.println("running...");
	for (RobotManager rm : robots) {
	    rm.join();
	}
    }

    private int getRandomPos() {
	return minSlotPos + (int) (Math.random() * (maxSlotPos - minSlotPos));
    }

    private void startRobot(Robot r) throws InterruptedException {
	RobotManager rm = new RobotManager(r);
	robots.add(rm);
	rm.start();
    }

    private synchronized void printError(Exception e) {
	e.printStackTrace();
	mpm.print(System.out);
	System.exit(100);
    }

    private class RobotManager extends Thread {
	private Robot robot;

	public RobotManager(Robot robot) {
	    super();
	    this.robot = robot;
	}

	public void run() {
	    int turn = 0;
	    while (turn++ < maxTurns) {
		Monitor m = Monitor.getMonitor("Robot");
		long counterId = m.startCounter();
		try {
		    robot.executeTurn(new RobotContext(slotManager));
		} catch (Exception e) {
		    printError(e);
		}
		m.stopCounter(counterId);
		if (robot.getPosition() < minSlotPos) {
		    robot.setPosition(maxSlotPos);
		}
		if (robot.getPosition() > maxSlotPos) {
		    robot.setPosition(minSlotPos);
		}
		Thread.yield();
	    }
	}
    }

    public static void main(String[] args) throws Exception {
	SchemaManager sm = new SchemaManager("RoboSim", mpm);
	RoboSimulation sim = new RoboSimulation(sm);
	sim.test1();
//	sim.test2();
	mpm.print(System.out);
	sim.slotManager.printStats(System.out);
	Monitor.getMonitor("Robot").printStats(System.out);

	// sm.createCommand(Slot.class).insert(new Slot(567,'T')).execute();
	// long updated = sm.createCommand(Slot.class).update(new Slot(567, 'P')).where().field("position").eq(567)
	// .execute();
	// System.out.println(updated);
	// mpm.print(System.out);
    }
}
