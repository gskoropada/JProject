package project.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import project.logic.*;
/**
 * ProjectGUI provides a user interface to manage the projects' information.
 * It uses SWING components.
 * The GUI is organized in three areas:
 * <ul><li>Ongoing Projects Table
 * <li>Finished Projects Table
 * <li>User options</ul>
 * <h2>Project tables</h2>
 * The project tables display the information of the working Portfolio object in a tabular format and
 * provide editing and selecting functionality.
 * The data entered in the tables is validates by the custom cell editors defined in this class.
 * <h2>User options</h2>
 * The user option buttons provide functionalities to add a new project, delete the selected projects, save the current
 * data set, restore to the default data set and exiting the application.<br>
 * When adding a new project the application prompts for the data using a NewProjectDialog object
 * @author Gabriel Skoropada
 * @version 1.0
 * @see ProjectUI
 * @see Portfolio
 * @see Selection
 * @see Project
 * @see OngoingProject
 * @see FinishedProject
 */
public class ProjectGUI {

	/** true if there are no unsaved changes, false if changes had been made and not saved */
	private static boolean saved=true;
	/** Portfolio object to handle the application data */
	private static Portfolio portfolio = new Portfolio();
	/** Selection object to keep track of which projects have been selected by the user */
	private static Selection sel = new Selection();
	/** Default date format String */
	public static final String DATE_FORMAT="dd/MM/yyyy";
	/** SimpleDateFormat object initialized with the default date format */
	private static SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
	/** String containing the name of the working file */
	private static final String WORKING_FILE = "portfolio.obj";
	/** String containing the name of the default dataset file */
	private static final String DEFAULTS_FILE = "defaults.obj";
	
	/** JFrame object that acts as the main window of the application */
    private final static JFrame frame = new JFrame("JProject v2.1");
    /** JTable object to display the project information in tabular format */
    private static JTable opTable, fpTable;
    /** JLabel object to display the OngoingProject count */
   	private static JLabel ongoingProjectsLabel;
   	/** JLabel object to display the FinishedProject count */
	private static JLabel finishedProjectsLabel;
		
	public static void main (String[] args) {
		portfolio.init(WORKING_FILE);
		sel.init(portfolio);
		
		splashScreen();
		mainWindow();
		
	}
	
	/**
	 * Displays the main application windows.
	 */
	public static void mainWindow() {
		
		//Create and set up the window.
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setPreferredSize(new Dimension(1024,600));
        
        JLabel topLabel = new JLabel("JProject v.2.1");
        JButton btnAdd = new JButton("Add new project");
        	btnAdd.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if(addNewProject()) {
						System.out.println("Added");
					} else {
						System.out.println("Cancelled");
					}
				}
        		
        	});
        	
        JButton btnDel = new JButton("Delete selected");
        btnDel.addActionListener(new ActionListener() {
        	/* Asks for confirmation and deletes the selected project from the working Portfolio object. */
			@Override
			public void actionPerformed(ActionEvent e) {
				int choice = JOptionPane.showConfirmDialog(frame, 
						"Do you want to delete the selected projects?\nThis action cannot be undone!", 
						"Delete Selected", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
				if(choice == 0){
					System.out.println("Delete");
					String[] selected = sel.getSelected(); 
					for(int i=0;i<selected.length;i++) {
					
						int index = portfolio.findByCode(selected[i]);
						portfolio.remove(index);
						sel.remove(index);
					
					}
					
				}
				
				fpTable.setModel((TableModel) new ProjectTableModel(portfolio.getFinishedProjects()));
				opTable.setModel((TableModel) new ProjectTableModel(portfolio.getOngoingProjects()));
				toggleSaved(false);
			}
        	
        });
        	
        JPanel projectLists = new JPanel();
        projectLists.setLayout(new BoxLayout(projectLists, BoxLayout.PAGE_AXIS));
        
        	opTable = new JTable((TableModel) new ProjectTableModel(portfolio.getOngoingProjects()));
        	fpTable = new JTable((TableModel) new ProjectTableModel(portfolio.getFinishedProjects()));
        
           	opTable.setDefaultRenderer(Date.class, new DateRenderer());
        	opTable.setDefaultEditor(Date.class, new DateEditor());
        	opTable.setDefaultRenderer(Integer.class, new CompletionRenderer());
        	opTable.setDefaultRenderer(Double.class, new CurrencyRenderer());
        	
        	DefaultTableCellRenderer codeRenderer = new DefaultTableCellRenderer();
        		codeRenderer.setToolTipText("Select a project and right click "
        				+ "on the project code to convert it to "
        				+ "a finished project.");
        	
        	opTable.getColumnModel().getColumn(0).setCellRenderer(codeRenderer);
        	
        	for(int i=0; i<ProjectTableModel.ONGOING_COL_WIDTHS.length;i++) {
				opTable.getColumnModel().getColumn(i).setPreferredWidth(ProjectTableModel.ONGOING_COL_WIDTHS[i]);
			}
        	
        	fpTable.setDefaultRenderer(Date.class, new DateRenderer());
        	fpTable.setDefaultEditor(Date.class, new DateEditor());
        	fpTable.setDefaultRenderer(Double.class, new CurrencyRenderer());
        	
        	for(int i=0; i<ProjectTableModel.FINISHED_COL_WIDTHS.length;i++) {
				fpTable.getColumnModel().getColumn(i).setPreferredWidth(ProjectTableModel.FINISHED_COL_WIDTHS[i]);
			}
        	
        	/* Captures mouse clicks on the table and asks for confirmation to change OngoingProject 
        	 * to FinishedProject if the click is a right click on the first column of the table */
        	opTable.addMouseListener(new MouseAdapter() {
        		  public void mouseClicked(MouseEvent e) {
        		    if (e.getButton() == MouseEvent.BUTTON3) {
        		      JTable target = (JTable)e.getSource();
        		      int row = target.getSelectedRow();
        		      int column = target.getSelectedColumn();
        		      if(column==0) {
        		    	  String msg = "Do you want to change this project status to finished?\n";
        		    	  msg += target.getValueAt(row,0) + "\n";
        		    	  msg += target.getValueAt(row, 1);
        		    	  
        		    	  int choice = JOptionPane.showConfirmDialog(frame, msg, "Change project status", JOptionPane.YES_NO_OPTION);
        		    	  
        		    	  if(choice == 0) {
        		    		System.out.println("changed");
        		    		int index = portfolio.findByCode((String)target.getValueAt(row,0));
        		    		Project p = portfolio.get(index);
        		    		portfolio.replaceProject(new FinishedProject(p), index);
        		    		opTable.setModel((TableModel) new ProjectTableModel(portfolio.getOngoingProjects()));
	      					fpTable.setModel((TableModel) new ProjectTableModel(portfolio.getFinishedProjects()));
	      					updateCounters();
	      					toggleSaved(false);
        		    	  }
        		      }
        		    }
        		  }
        		});
        	
        	ongoingProjectsLabel = new JLabel();
        	finishedProjectsLabel = new JLabel();
        	JScrollPane ongoingProjects = new JScrollPane(opTable);
        	JScrollPane finishedProjects = new JScrollPane(fpTable);
        	
        	updateCounters();
        	
        	projectLists.add(ongoingProjectsLabel);
        	projectLists.add(ongoingProjects);
        	projectLists.add(finishedProjectsLabel);
        	projectLists.add(finishedProjects);

               
        JPanel menuOptions = new JPanel();
        menuOptions.setLayout(new GridLayout(1,0));
        
        	JButton btnSave = new JButton("Save");
        	btnSave.addActionListener(new ActionListener() {
        		/* Saves the current working Portfolio state to the working file */
				@Override
				public void actionPerformed(ActionEvent e) {
					toggleSaved(portfolio.save(WORKING_FILE));
				}
        		
        	});
        	
        	JButton btnRestore = new JButton("Restore");
        	btnRestore.addActionListener(new ActionListener() {
        		/* Initializes the working portfolio with data from the default dataset file */
				@Override
				public void actionPerformed(ActionEvent e) {
					int choice = JOptionPane.showConfirmDialog(frame, "Do you want to restore to the default data set?",
							"Restore Defaults", JOptionPane.YES_NO_OPTION);
					if(choice == 0) {
						portfolio.init(DEFAULTS_FILE);
						opTable.setModel((TableModel) new ProjectTableModel(portfolio.getOngoingProjects()));
						fpTable.setModel((TableModel) new ProjectTableModel(portfolio.getFinishedProjects()));
						sel.init(portfolio);
						updateCounters();
						toggleSaved(false);
					}
				}
        		
        	});
        	JButton btnQuit = new JButton("Quit");
        	btnQuit.addActionListener(new ActionListener(){
        	    /* Checks if there are unsaved changes and asks the user if he/she would like to save the data */
        		public void actionPerformed(ActionEvent e)
        	    {
        	    	checkSaveOnExit();
        	    }
        	});
        	
        	menuOptions.add(btnAdd);
        	menuOptions.add(btnDel);
        	menuOptions.add(btnSave);
        	menuOptions.add(btnRestore);
        	menuOptions.add(btnQuit);
        
        frame.add(topLabel, BorderLayout.PAGE_START);
        frame.add(menuOptions, BorderLayout.PAGE_END);
        frame.add(projectLists, BorderLayout.CENTER);
        
        
        frame.addWindowListener(new WindowListener() {

			@Override
			public void windowOpened(WindowEvent e) {
							
			}
			/* Checks if there are unsaved changes and asks the user if he/she would like to save the data */
			@Override
			public void windowClosing(WindowEvent e) {
				checkSaveOnExit();
			}

			@Override
			public void windowClosed(WindowEvent e) {
				
				
			}

			@Override
			public void windowIconified(WindowEvent e) {
	
				
			}

			@Override
			public void windowDeiconified(WindowEvent e) {
	
				
			}

			@Override
			public void windowActivated(WindowEvent e) {

				
			}

			@Override
			public void windowDeactivated(WindowEvent e) {

				
			}
        	
        });
        	 
        //Display the window.
	    frame.pack();
	    frame.setVisible(true);
	}
	/**
	 * Asks the user for confirmation if there are any unsaved changes on exit.
	 */
	private static void checkSaveOnExit() {
		if(!saved) {
    		int option = JOptionPane.showOptionDialog(frame,
    				"There are unsaved changes\nDo you want to save them now?",
    				"Unsaved changes",JOptionPane.YES_NO_CANCEL_OPTION,
    				JOptionPane.WARNING_MESSAGE, null, null, 0);
    		switch(option) {
    		case 0:
    			portfolio.save(WORKING_FILE);
    		case 1:
    			System.exit(0);
    		default:
    			break;        	    		
    		}
    		System.out.print("unsaved changes " + option);
    	} else {
    		System.exit(0);
    	}
	}
	
	/**
	 * Displays the application splash screen.
	 */
	private static void splashScreen() {
		
		String message = "Welcome to JProject v2.1\nby Gabriel Skoropada";
		
		JOptionPane.showMessageDialog(null, message, "JProject v2.1", JOptionPane.NO_OPTION, null);
		
	}
	
	/**
	 * Toggles the application saved status.
	 * @param s is a Boolean value indicating what the saved status should be.
	 */
	private static void toggleSaved(boolean s) {
		if(!s) {
			frame.setTitle("JProject v2.1 ** Unsaved Changes **");
		} else {
			frame.setTitle("JProject v2.1");
		}
		saved = s;
	}
	
	/**
	 * Displays a dialog allowing to enter new projects information
	 * @return false if the user cancels the action.
	 */
	private static boolean addNewProject() {
		System.out.println("Add new project");
		String[] options = new String[] {"Ongoing","Finished","Cancel"};
		int choice = JOptionPane.showOptionDialog(frame, "Select new project type" , "Add new Project", 
				JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[2]);
		
		NewProjectDialog dialog;
		
		switch(choice) {
		case 0:
			dialog = new NewProjectDialog("o");
			dialog.setVisible(true);
			break;
		case 1:
			dialog = new NewProjectDialog("f");
			dialog.setVisible(true);
			break;
		default:
			return false;
		}
		
		return true;
	}
	
	/**
	 * Updates the project counters on the main window.
	 */
	private static void updateCounters() {
		ongoingProjectsLabel.setText("Ongoing Projects (" + portfolio.getOngoingCount() + ")");
		finishedProjectsLabel.setText("Finished Projects (" + portfolio.getFinishedCount() + ")");
	}
	
	/**
	 * Defines a custom dialog showing fields to allow entering new projects information
	 * @author Gabriel Skoropada
	 * @version 1.0
	 * @see JFrame
	 * @see Project
	 * @see OngoingProject
	 * @see FinishedProject
	 */
	private static class NewProjectDialog extends JFrame {
		
		private static final long serialVersionUID = 1L;
		
		private static String pType;
		private static SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
		
		//Common Project components
		private static JTextField pCode = new JTextField();
		private static JLabel msgCode = new JLabel();
		private static JTextField pName = new JTextField();
		private static JLabel msgName = new JLabel();
		private static JTextField pClient = new JTextField();
		private static JLabel msgClient = new JLabel();
		private static JFormattedTextField pSDate = new JFormattedTextField(dateFormat);
		private static JLabel msgSDate = new JLabel();

		
		//Ongoing Project components
		private static JFormattedTextField pDeadline = new JFormattedTextField(dateFormat);
		private static JLabel msgDeadline = new JLabel();
		private static JTextField pBudget = new JTextField("0");
		private static JLabel msgBudget = new JLabel();
		private static JTextField pCompletion = new JTextField("0");			
		private static JLabel msgCompletion = new JLabel();
		
		//Finished Project components
		private static JFormattedTextField pEndDate = new JFormattedTextField(dateFormat);
		private static JLabel msgEndDate = new JLabel(DATE_FORMAT);	
		private static JTextField pTotalCost = new JTextField();
		private static JLabel msgTotalCost = new JLabel();
		
		/**
		 * Creates a new dialog with fields suiting the specified project type.
		 * @param pt a String specifying the project type. "o" for OngoingProject or "f" for FinishedProject
		 */
		public NewProjectDialog(String pt) {
			
			super("Add new project");
			resetFields();
			pType = pt;
			
			final JFrame npd = this;
			
			pSDate.setHorizontalAlignment(SwingConstants.RIGHT);
			pDeadline.setHorizontalAlignment(SwingConstants.RIGHT);
			pEndDate.setHorizontalAlignment(SwingConstants.RIGHT);
			pBudget.setHorizontalAlignment(SwingConstants.RIGHT);
			pTotalCost.setHorizontalAlignment(SwingConstants.RIGHT);
			pCompletion.setHorizontalAlignment(SwingConstants.RIGHT);
			
			int x = frame.getLocation().x;
			int y = frame.getLocation().y;
			
			int pWidth = frame.getSize().width;
			int pHeight = frame.getSize().height;
			
			getContentPane().setLayout(new BorderLayout());
			
			final JPanel fieldPanel = new JPanel();
			
			if(pType.equals("o")) {
				fieldPanel.setLayout(new GridLayout(7,3));
				System.out.println("ongoing");
				this.setPreferredSize(new Dimension(600,230));
			} else if(pType.equals("f")) {
				fieldPanel.setLayout(new GridLayout(6,3));
				System.out.println("finished");
				this.setPreferredSize(new Dimension(600,200));
			}
			fieldPanel.add(new JLabel("Project Code :"));
			fieldPanel.add(pCode);
			fieldPanel.add(msgCode);		
			fieldPanel.add(new JLabel("Project Name :"));
			fieldPanel.add(pName);
			fieldPanel.add(msgName);
			fieldPanel.add(new JLabel("Client :"));
			fieldPanel.add(pClient);
			fieldPanel.add(msgClient);
			fieldPanel.add(new JLabel("Start date: "));
			fieldPanel.add(pSDate);
			fieldPanel.add(msgSDate);
			msgSDate.setText(DATE_FORMAT);
			
			if(pType.equals("o")) {
				fieldPanel.add(new JLabel("Deadline :"));
				fieldPanel.add(pDeadline);
				fieldPanel.add(msgDeadline);
				msgDeadline.setText(DATE_FORMAT);
				fieldPanel.add(new JLabel("Budget :"));
				fieldPanel.add(pBudget);
				fieldPanel.add(msgBudget);
				fieldPanel.add(new JLabel("Completion :"));
				fieldPanel.add(pCompletion);			
				fieldPanel.add(msgCompletion);
				
			} else if(pType.equals("f")) {
				fieldPanel.add(new JLabel("End Date :"));
				fieldPanel.add(pEndDate);
				fieldPanel.add(msgEndDate);
				msgEndDate.setText(DATE_FORMAT);
				fieldPanel.add(new JLabel("Total Cost :"));
				fieldPanel.add(pTotalCost);
				fieldPanel.add(msgTotalCost);
			}
			
			JPanel optionButtons = new JPanel();
			optionButtons.setLayout(new FlowLayout());
			
			JButton btnAdd = new JButton("Add");
			btnAdd.addActionListener(new ActionListener() {

				/**
				 * Validates the information entered by the user and adds the project to the
				 * Portfolio object if data is valid.
				 */
				@Override
				public void actionPerformed(ActionEvent e) {
					
					if(validateForm()) {
						
						Project prj = compileProject();
						portfolio.add(prj);
						if(prj instanceof OngoingProject) {
							ProjectTableModel tm = (ProjectTableModel) opTable.getModel();
							tm.addRow(prj.toTable());
							opTable.setModel(tm);
							updateCounters();
							toggleSaved(false);
							sel.add(pCode.getText());
						} else if (prj instanceof FinishedProject) {
							ProjectTableModel tm = (ProjectTableModel) fpTable.getModel();
							tm.addRow(prj.toTable());
							fpTable.setModel(tm);
							updateCounters();
							toggleSaved(false);
							sel.add(pCode.getText());
						}
						npd.dispose();
					}
				}
				
			});
			optionButtons.add(btnAdd);
			
			JButton btnClose = new JButton("Close");
			btnClose.addActionListener(new ActionListener() {

				/* Closes the NewProjectDialog window. */
				@Override
				public void actionPerformed(ActionEvent e) {
					npd.dispose();
				}
				
			});
			
			optionButtons.add(btnClose);
			getContentPane().add(new JLabel("Enter the data for your new " + getPType(pType) + " project:"), BorderLayout.PAGE_START);
			this.add(fieldPanel, BorderLayout.CENTER);
			this.add(optionButtons, BorderLayout.PAGE_END);

			this.pack();
					
			int width = this.getSize().width;
			int height = this.getSize().height;
			
			this.setLocation(new Point((x+pWidth/2-width/2),(y+pHeight/2-height/2)));
			this.setVisible(true);
		}

		/**
		 * Returns a verbose description of the project type
		 * @param pType a String specifying the project type. "o" for OngoingProject or "f" for FinishedProject 
		 * @return a String with a verbose description of the project type 
		 */
		private String getPType(String pType) {
			if(pType.equals("o")) {
				return "ongoing";
			} else if(pType.equals("f")) {
				return "finished";
			}
			return null;
		}
		/**
		 * Compiles a Project object from the data entered by the user
		 * @return a Project object with data from the NewProjectDialog
		 * @see Project
		 */
		private Project compileProject() {
			Project p = null;
			
			if(pType.equals("o")) {
				try {
					p = new OngoingProject(pCode.getText(), pName.getText(), dateFormat.parse(pSDate.getText()),pClient.getText(), dateFormat.parse(pDeadline.getText()), Double.parseDouble(pBudget.getText()), Integer.parseInt(pCompletion.getText()));
				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (ParseException e) {
					e.printStackTrace();
				}
			} else if(pType.equals("f")) {
				try {
					p = new FinishedProject(pCode.getText(), pName.getText(), dateFormat.parse(pSDate.getText()),pClient.getText(), dateFormat.parse(pEndDate.getText()), Double.parseDouble(pTotalCost.getText()));
				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
					
			return p;
		}
		/**
		 * Verifies the information entered by the user and provides feedback if there is something
		 * wrong.
		 * @return a Boolean value indicating if the data entered in the form is valid.
		 */
		private static Boolean validateForm() {
			Boolean valid = true;
			if(pCode.getText().equals("")) {
				msgCode.setText("Project code is required");
				valid = false;
			} else if(portfolio.findByCode(pCode.getText()) != -1) {
				msgCode.setText("Project already exists!");
				valid = false;
			} else {
				msgCode.setText("");
			}
			
			if(pName.getText().equals("")) {
				msgName.setText("Project Name is required!");
				valid = false;
			} else {
				msgName.setText("");
			}
			
			if(pClient.getText().equals("")) {
				msgClient.setText("Client is required!");
				valid = false;
			} else {
				msgClient.setText("");
			}
			
			if(pSDate.getText().equals("")) {
				msgSDate.setText(DATE_FORMAT + " Start Date is required");
				valid = false;
			} else {
				msgSDate.setText(DATE_FORMAT);
			}
			
			switch(pType) {
			case "o":
				if(pDeadline.getText().equals("")) {
					msgDeadline.setText(DATE_FORMAT + " Deadline is required!");
					valid = false;
				} else {
					msgDeadline.setText(DATE_FORMAT);
				}
				
				try {
					Double b = Double.parseDouble(pBudget.getText());
					if(b<0) {
						msgBudget.setText("Must be a positive value");
						valid=false;
					} else {
						msgBudget.setText("");
					}
				} catch (Exception e) {
					msgBudget.setText("Invalid number!");
					valid = false;
				}
				
				try {
					int c = Integer.parseInt(pCompletion.getText());
					if(c<0 || c>100) {
						msgCompletion.setText("Value must be between 0 and 100");
						valid = false;
					} else {
						msgCompletion.setText("");
					}
				} catch (Exception e) {
					msgCompletion.setText("Invalid number!");
					valid = false;
				}
				
				break;
				
			case "f":
				if(pEndDate.getText().equals("")) {
					msgEndDate.setText(DATE_FORMAT + " End date is required!");
					valid = false;
				} else {
					msgEndDate.setText(DATE_FORMAT);
				}
				
				try {
					Double b = Double.parseDouble(pTotalCost.getText());
					if(b<0){
						msgTotalCost.setText("Must be a positive number");
						valid=false;
					} else {
						msgTotalCost.setText("");
					}
				} catch (Exception e) {
					msgTotalCost.setText("Invalid number!");
					valid = false;
				}
				
			}
			return valid;
		}
		/**
		 * Resets the NewProjectDialog's fields to default values.
		 */
		private static void resetFields() {
			//Common Project components
			pCode.setText("");
			msgCode.setText("");
			pName.setText("");
			msgName.setText("");
			pClient.setText("");
			msgClient.setText("");
			pSDate.setText(dateFormat.format(new Date()));
			msgSDate.setText("");
			
			//Ongoing Project components
			pDeadline.setText(dateFormat.format(new Date()));
			msgDeadline.setText("");
			pBudget.setText("0");
			msgBudget.setText("");
			pCompletion.setText("0");			
			msgCompletion.setText("");
			
			//Finished Project components
			pEndDate.setText(dateFormat.format(new Date()));
			msgEndDate.setText("");	
			pTotalCost.setText("0");
			msgTotalCost.setText("");
		}
	}

	/**
	 * Provides the model for the two project tables in the application main window.
	 * @author Gabriel Skoropada
	 * @version 1.0
	 * @see DefaultTableModel
	 */
	private static class ProjectTableModel extends DefaultTableModel {

		private static final long serialVersionUID = 1L;
		
		/** Constant String array with the names of the columns for the OngoingProjects table */
		private static final String[] ONGOING_COL_NAMES = {
			"Code",
			"Name",
			"Client",
			"Start Date",
			"Deadline",
			"Budget",
			"Completion"
		};
		
		/** Constant String array with the widths of the columns for the OngoingProjects table */
		private static final int[] ONGOING_COL_WIDTHS = {15, 150, 150, 45, 45, 45, 45, 15 };
		
		/** Constant String array with the names of the columns for the FinishedProjects table */
		private static final String[] FINISHED_COL_NAMES = {
			"Code",
			"Name",
			"Client",
			"Start Date",
			"End Date",
			"Total Cost"
		};
		
		/** Constant String array with the widths of the columns for the FinishedProjects table */
		private static final int[] FINISHED_COL_WIDTHS = {15, 150, 150, 45, 45, 45, 15 };
		
		/** This constructor builds a ProjectTableModel from the data of an ArrayList&#60;Project&#62; 
		 * @param prjs	An ArrayList&#60;Project&#62; object with the information for the table*/
		public ProjectTableModel(ArrayList<Project> prjs) {
			int rows = prjs.size();
			
			Object[][] data = null;
			String[] colNames = null;
			Object[] delOpt = new Object[rows];
			
			if(rows > 0) {
				if(prjs.get(0) instanceof OngoingProject) {
					data = new Object[rows][ONGOING_COL_NAMES.length];
					colNames = ONGOING_COL_NAMES;
				} else if(prjs.get(0) instanceof FinishedProject) {
					data = new Object[rows][FINISHED_COL_NAMES.length];
					colNames = FINISHED_COL_NAMES;
				}
			}
			
			for(int i=0;i<rows;i++) {
				data[i] = prjs.get(i).toTable();
				delOpt[i] = new Boolean(true);
			}
			
			super.setDataVector(data, (Object[]) colNames);
			this.addColumn("", delOpt);
					
		}
	
		
		@SuppressWarnings({ "unchecked", "rawtypes" })
		/**
		 * Returns the class of a specified column of the table
		 * @param c	Integer representing the column
		 * @returns	The class of the specified column
		 */
		public Class getColumnClass(int c) {
			return getValueAt(0, c).getClass();
	       
	    }
		
		@Override
		/**
		 * Returns an Object with the value of a specific cell.
		 * @param r	Integer representing the row where the cell is
		 * @param c	Integer representing the column where the cell is
		 * @returns an Object with the value of a specific cell.
		 */
		public Object getValueAt(int r, int c) {
			String code = (String) super.getValueAt(r, 0);
			Project p = portfolio.get(portfolio.findByCode(code));
			
			if(c==0) {
				return p.getCode();
			} else if(c==1) {
				return p.getName();
			} else if(c==2) {
				return p.getClient();
			} else if(c==3) {
				return p.getStartDate();
			} else if(p instanceof OngoingProject && c==4) {
				return ((OngoingProject)p).getDeadline();
			} else if(p instanceof OngoingProject && c==5) {
				return ((OngoingProject)p).getBudget();
			} else if(p instanceof OngoingProject && c==6) {
				return ((OngoingProject)p).getCompletion();
			} else if(p instanceof OngoingProject && c==7) {
				return sel.status(p.getCode());
			} else if(p instanceof FinishedProject && c==4) {
				return ((FinishedProject)p).getEndDate();
			} else if(p instanceof FinishedProject && c==5) {	
				return ((FinishedProject)p).getTotalCost();
			} else if(p instanceof FinishedProject && c==6) {
				return sel.status(p.getCode());
			} else {
				return null;
			}
		}
		
		@Override
		/**
		 * Sets the value for a specific cell
		 * @param value Object containing the value for the cell
		 * @param r 	Row where the cell is
		 * @param c		Column where the cell is
		 */
		public void setValueAt(Object value, int r, int c) {
			String code = (String) super.getValueAt(r, 0);
			int index = portfolio.findByCode(code);
			Project p = portfolio.get(index);
			
			if(c==1) {
				p.setName((String) value);
			} else if(c==2) {
				p.setClient((String) value);
			} else if(c==3) {
				System.out.println(value.getClass());
				try {
					p.setStartDate(dateFormat.parse((String) value));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if(p instanceof OngoingProject && c==4) {
				try {
					((OngoingProject)p).setDeadline(dateFormat.parse((String) value));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if(p instanceof OngoingProject && c==5) {
				((OngoingProject)p).setBudget((double) value );
			} else if(p instanceof OngoingProject && c==6) {
				((OngoingProject)p).setCompletion((int) value);
			} else if(p instanceof OngoingProject && c==7) {
				sel.findByCode(p.getCode()).toggle();
				return;
			} else if(p instanceof FinishedProject && c==4) {
				System.out.println(value);
				try {
					((FinishedProject)p).setEndDate(dateFormat.parse((String) value));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if(p instanceof FinishedProject && c==5) {	
				((FinishedProject)p).setTotalCost((double) value);
			} else if(p instanceof FinishedProject && c==6) {
				sel.findByCode(p.getCode()).toggle();
				return;
			}
			
			portfolio.replaceProject(p, index);
			toggleSaved(false);
		}
		
		@Override
		/**
		 * Checks if a specified cell is editable.
		 * @param r	Integer representing the row where the cell is
		 * @param c	Integer representing the column where the cell is
		 * @returns	true if the cell can be edited
		 */
		public boolean isCellEditable(int r, int c) {
			
			if(c == 0) {
				return false;
			} else {
				return true;
			}
		}
		
	}
	
	/**
	 * Renders the date columns in the project tables according to the default date format
	 * @author Gabriel Skoropada
	 * @version 1.0
	 */
	private static class DateRenderer extends DefaultTableCellRenderer {

		private static final long serialVersionUID = 1L;

		/**
		 * Creates a new DateRenderer object with the horizontal alignment set at center
		 */
		public DateRenderer() {
			super();
			this.setHorizontalAlignment(SwingConstants.CENTER);
			}

		@Override
		/**
		 * Sets the value for the cell component.
		 * @param value	Object containing the value
		 */
	    public void setValue(Object value) {
  	
	    	setText(dateFormat.format((Date) value));
	    }
	}
	
	/**
	 * Default renderer for the Completion column on the OngoingProjects table
	 * @author Gabriel Skoropada
	 * @version 1.0
	 * 
	 */
	private static class CompletionRenderer extends DefaultTableCellRenderer {

		private static final long serialVersionUID = 1L;

		/**
		 * Creates a CompletionRenderer object setting the alignmnet to the right
		 */
		public CompletionRenderer() {
			super();
			this.setHorizontalAlignment(SwingConstants.RIGHT);
		}
		
		@Override
		/**
		 * Sets the value for the cell component.
		 * @param value	Object containing the value
		 */
		public void setValue(Object value) {
			setText(String.format("%d %%", value));
		}
	}
	
	/**
	 * Default renderer for the Budget and TotalCost columns on the Projects tables
	 * @author Gabriel Skoropada
	 * @version 1.0
	 * 
	 */
	private static class CurrencyRenderer extends DefaultTableCellRenderer {

		private static final long serialVersionUID = 1L;

		/**
		 * Creates a CompletionRenderer object setting the alignmnet to the right
		 */
		public CurrencyRenderer() {
			super();
			this.setHorizontalAlignment(SwingConstants.RIGHT);
		}
		
		@Override
		/**
		 * Sets the value for the cell component.
		 * @param value	Object containing the value
		 */
		public void setValue(Object value) {
			setText(String.format("$ %.2f", value));
		}
	}
	
	/**
	 * Default cell editor for columns with Date values in the projec tables
	 * @author Gabriel Skoropada
	 * @version 1.0
	 *
	 */
	private static class DateEditor extends DefaultCellEditor {
		
		private static final long serialVersionUID = 1L;
		/** JFormattedTextField object to get input from user */
		JFormattedTextField tf;
		
		/**
		 * Creates a DateEditor object and sets the default behaviors for the JFormattedTextField object  
		 * */
		public DateEditor() {
			super(new JFormattedTextField(dateFormat));
			tf = (JFormattedTextField) getComponent();
			
			//React when the user presses Enter while the editor is
	        //active.  (Tab is handled as specified by
	        //JFormattedTextField's focusLostBehavior property.)
	        tf.getInputMap().put(KeyStroke.getKeyStroke(
	                                        KeyEvent.VK_ENTER, 0),
	                                        "check");
	        tf.getActionMap().put("check", new AbstractAction() {
	
				private static final long serialVersionUID = 1L;
				
				/* Verifies if the entered text is valid data */
				public void actionPerformed(ActionEvent e) {
			        if (tf.isEditValid()) { //The text is invalid.
			                           //The text is valid,
		                try {
							tf.commitEdit();
						} catch (ParseException e1) {
							e1.printStackTrace();
						}     //so use it.
		                tf.postActionEvent(); //stop editing
			        }
				}
			});
			
		}
		
	    //Override to invoke setValue on the formatted text field.
	    public Component getTableCellEditorComponent(JTable table,
	            Object value, boolean isSelected,
	            int row, int column) {
	        JFormattedTextField ftf =
	            (JFormattedTextField)super.getTableCellEditorComponent(
	                table, value, isSelected, row, column);
	        ftf.setValue(value);
	        return ftf;
	    }
	    
	    //Override to check whether the edit is valid,
	    //setting the value if it is and complaining if
	    //it isn't.  If it's OK for the editor to go
	    //away, we need to invoke the superclass's version 
	    //of this method so that everything gets cleaned up.
	    public boolean stopCellEditing() {
	        JFormattedTextField ftf = (JFormattedTextField)getComponent();
	        if (ftf.isEditValid()) {
	            try {
	            	System.out.print(ftf.getValue().getClass());
	                ftf.commitEdit();
	            } catch (java.text.ParseException exc) { }
	         
	        } else { 
	            return false; //don't let the editor go away
	       	        }
	        return super.stopCellEditing();
	    }
		
	}
	
}
