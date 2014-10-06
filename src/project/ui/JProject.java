package project.ui;

import project.logic.Settings;

/**
 * Point-of-entry class for the JProject application
 * @author Gabriel Skoropada
 * @version 1.0 - started on 16/sep/14
 * @see ProjectGUI
 * @see ProjectUI
 */
public class JProject {
	/** Working Settings object */
	private static Settings s = new Settings();
	
	public static void main(String[] args) {
		
		s.read();
		parseArgs(args);
		
		//s.setGUI(false);
		
		if(s.isGUI()) {
			ProjectGUI.start(s);
		} else {
			ProjectUI.start();
		}
	}

	/**
	 * Reads the command line parameters and changes the Settings object accordingly.
	 * @param args a String[] containing the command line arguments separated by a space
	 */
	private static void parseArgs(String[] args) {
		if(args.length > 0) {
			for(int i=0;i<args.length; i++){
				System.out.print(args[i]+":");
				if(args[i].startsWith("g=")) {
					try {
						int x = Integer.parseInt(args[i].substring(2));
						if(x>=0) {
							s.setGUI(true);
						} else {
							s.setGUI(false);
						}
					} catch (Exception e) {
						System.out.println("invalid value " + args[i].substring(2) + " after g parameter");
					}
				}
			}
		}
	}

}
