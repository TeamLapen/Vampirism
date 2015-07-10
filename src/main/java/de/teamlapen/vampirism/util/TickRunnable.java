package de.teamlapen.vampirism.util;

/**
 * Simple interface for tasks which should be run over a small amount of ticks
 */
public interface TickRunnable {
	public boolean shouldContinue();
	public void onTick();
}
