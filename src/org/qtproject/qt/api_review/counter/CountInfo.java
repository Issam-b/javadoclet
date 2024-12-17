package org.qtproject.qt.api_review.counter;

/**
 * This class holds step count results.
 */
public class CountInfo {

	/**
	 * Number of lines
	 */
	private int lines;

	/**
	 * Number of valid rows
	 */
	private int steps;

	/**
	 * number of blank lines
	 */
	private int branks;

	/**
	 * A constructor.
	 *
	 * @param lines Number of lines
	 * @param steps Number of valid rows
	 * @param branks number of blank lines
	 */
	public CountInfo(int lines, int steps, int branks) {
		this.lines = lines;
		this.steps = steps;
		this.branks = branks;
	}

	/**
	 * Get the number of rows.
	 *
	 * @return Number of lines
	 */
	public int getLines() {
		return lines;
	}

	/**
	 * Set the number of rows.
	 *
	 * @param lines Number of lines
	 */
	public void setLines(int lines) {
		this.lines = lines;
	}

	/**
	 * Get the number of valid rows.
	 *
	 * @return Number of valid rows
	 */
	public int getSteps() {
		return steps;
	}

	/**
	 * Set the number of valid rows.
	 *
	 * @param steps Number of valid rows
	 */
	public void setSteps(int steps) {
		this.steps = steps;
	}

	/**
	 * Get the number of blank lines.
	 *
	 * @return number of blank lines
	 */
	public int getBranks() {
		return branks;
	}

	/**
	 * Sets the number of blank lines.
	 *
	 * @param branks number of blank lines
	 */
	public void setBranks(int branks) {
		this.branks = branks;
	}
}
