package com.ganbook.utils;

public class LowPriorityThread extends Thread {

	public LowPriorityThread(String threadName) {
		setPriority(Thread.NORM_PRIORITY-2);
		setName(threadName);
	}

}
