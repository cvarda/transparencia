package org.cvarda.transparencia.util;

import java.io.PrintStream;
import java.text.DecimalFormat;

public class ConsoleCounter {

	private final int rowWidth;
	private final int each;
	private final PrintStream ps;

	private int counter = 0;
	private int column = 0;
	
	private boolean enabled = true;
	private long start = 0;
	
	public ConsoleCounter(int rowWidth) {
		this(rowWidth, 1, System.out);
	}
	
	public ConsoleCounter(int rowWidth, int each) {
		this(rowWidth, each, System.out);
	}
	
	public ConsoleCounter(int rowWidth, int each, PrintStream ps) {
		this.rowWidth = rowWidth;
		this.each = each;
		this.ps = ps;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public void add() {
		if (this.enabled) {
			this.addIml();
		}
	}
	
	public void done() {
		if (this.enabled) {
			this.doneImpl();
		}
	}
	
	private synchronized void addIml() {
		if (this.start == 0) {
			this.start  = System.currentTimeMillis();
		}
		
		int current = ++counter;
		if (this.each == 1 || (current % each) == 0) {
			if (++column == this.rowWidth) {
				this.ps.println(". " + current);
				this.column = 0;
			} else {
				this.ps.print(".");
			}
		}
	}
	
	private synchronized void doneImpl() {
		double delta = (double) (System.currentTimeMillis() - this.start) / 1000;
		String finishInfo = "[finished in " + new DecimalFormat("#.##").format(delta) + "s]";
		if (column > 0) {
			this.ps.println(" " + this.counter + " " + finishInfo);
		} else {
			this.ps.println(finishInfo);
		}
	}
	
}
