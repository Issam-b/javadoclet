package org.qtproject.qt.api_review.counter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Provides step counting processing. Provides step counting processing.
 */
public class Counter {

	/**
	 * Count the number of steps. Returns null if an exception occurs.
	 *
	 * @param file
	 *            Target file
	 * @return Count information
	 */
	public static CountInfo count(File file) {

		// count execution
		BufferedReader br = null;
		try {

			// Counter initialization
			int lines = 0;
			int steps = 0;
			int blanks = 0;

			// row text column
			String line;

			// Block comment flag initialization
			boolean comment = false;

			// file open
			br = new BufferedReader(new FileReader(file));

			// read all lines
			while ((line = br.readLine()) != null) {

				// Add number of rows
				lines++;

				// blank removal
				line = line.trim();

				// Valid row count
				if (line.length() == 0) {
					blanks++;
				} else {
					if (line.startsWith("/*") && line.endsWith("*/")) {
						comment = false;
					} else if (line.startsWith("/*")) {
						comment = true;
					} else if (line.endsWith("*/")) {
						comment = false;
					} else {
						if (!comment && !line.startsWith("//")) {
							steps++;
						}
					}
				}
			}

			// Return count results
			return new CountInfo(lines, steps, blanks);

		} catch (IOException e) {
		} finally {

			// file close
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
				}
			}
		}

		// If an error occurs
		return null;
	}
}
