package com.seezoon.eagle.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.SmartLifecycle;

import com.seezoon.eagle.core.utils.NDCUtils;

public class SpringSmartLifecycle implements SmartLifecycle{

	private static Logger logger = LoggerFactory.getLogger(SpringSmartLifecycle.class);
	private boolean isRun = false;
	@Override
	public void start() {
		NDCUtils.push();
		isRun = true;
	}

	@Override
	public void stop() {
		NDCUtils.clear();
	}

	@Override
	public boolean isRunning() {
		return isRun;
	}

	@Override
	public int getPhase() {
		return 0;
	}

	@Override
	public boolean isAutoStartup() {
		return true;
	}

	@Override
	public void stop(Runnable callback) {
		
	}

}
