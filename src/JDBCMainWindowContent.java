import java.awt.*;
import java.awt.event.*;
import java.io.FileWriter;
import java.io.PrintWriter;
import javax.swing.*;
import javax.swing.border.*;
import java.sql.*;

@SuppressWarnings("serial")
public class JDBCMainWindowContent extends JInternalFrame implements ActionListener
{	
	String cmd = null;

	// DB Connectivity Attributes
	private Connection con = null;
	private Statement stmt = null;
	private ResultSet rs = null;

	private Container content;

	private JPanel detailsPanel;
	private JPanel exportButtonPanel;
	//private JPanel exportConceptDataPanel;
	private JScrollPane dbContentsPanel;

	private Border lineBorder;

	private JLabel IDLabel=new JLabel("ID:                 ");
	private JLabel FirstNameLabel=new JLabel("First Name:               ");
	private JLabel LastNameLabel=new JLabel("Last Name:      ");
	private JLabel RiderNumberLabel=new JLabel("Number:      ");
	private JLabel MakeLabel=new JLabel("Make:        ");
	private JLabel ModelLabel=new JLabel("Model:                 ");
	private JLabel YearLabel=new JLabel("Year:               ");
	private JLabel TeamLabel=new JLabel("Team:      ");
	private JLabel CountryLabel=new JLabel("Country:        ");
	private JLabel WagesLabel=new JLabel("Wages:        ");
	private JLabel BonusLabel=new JLabel("Win Bonus %:        ");

	private JTextField IDTF= new JTextField(10);
	private JTextField FirstNameTF=new JTextField(10);
	private JTextField LastNameTF=new JTextField(10);
	private JTextField RiderNumberTF=new JTextField(10);
	private JTextField MakeTF=new JTextField(10);
	private JTextField ModelTF=new JTextField(10);
	private JTextField YearTF=new JTextField(10);
	private JTextField TeamTF=new JTextField(10);
	private JTextField CountryTF=new JTextField(10);
	private JTextField WagesTF=new JTextField(10);
	private JTextField BonusTF=new JTextField(10);


	private static QueryTableModel TableModel = new QueryTableModel();
	//Add the models to JTabels
	private JTable TableofDBContents=new JTable(TableModel);
	//Buttons for inserting, and updating members
	//also a clear button to clear details panel
	private JButton updateButton = new JButton("Update");
	private JButton insertButton = new JButton("Insert");
	private JButton exportButton  = new JButton("Export");
	private JButton deleteButton  = new JButton("Delete");
	private JButton clearButton  = new JButton("Clear");

	private JButton NumMakes = new JButton("Number of manufacturers:");
	private JTextField NumMakesTF = new JTextField(12);
	private JButton AverageWageForCountry = new JButton("Average Wage For Country");
	private JTextField AverageWageForCountryTF = new JTextField(12);
	private JButton ListAllTeams  = new JButton("List All Teams");
	private JButton ListAllModels = new JButton("List All Models");



	public JDBCMainWindowContent( String aTitle)
	{	
		//setting up the GUI
		super(aTitle, false,false,false,false);
		setEnabled(true);

		initiate_db_conn();
		//add the 'main' panel to the Internal Frame
		content=getContentPane();
		content.setLayout(null);
		content.setBackground(Color.lightGray);
		lineBorder = BorderFactory.createEtchedBorder(15, Color.red, Color.black);

		//setup details panel and add the components to it
		detailsPanel=new JPanel();
		detailsPanel.setLayout(new GridLayout(11,2));
		detailsPanel.setBackground(Color.lightGray);
		detailsPanel.setBorder(BorderFactory.createTitledBorder(lineBorder, "CRUD Actions"));

		detailsPanel.add(IDLabel);			
		detailsPanel.add(IDTF);
		detailsPanel.add(FirstNameLabel);		
		detailsPanel.add(FirstNameTF);
		detailsPanel.add(LastNameLabel);		
		detailsPanel.add(LastNameTF);
		detailsPanel.add(RiderNumberLabel);
		detailsPanel.add(RiderNumberTF);
		detailsPanel.add(MakeLabel);
		detailsPanel.add(MakeTF);
		detailsPanel.add(ModelLabel);
		detailsPanel.add(ModelTF);
		detailsPanel.add(YearLabel);
		detailsPanel.add(YearTF);
		detailsPanel.add(TeamLabel);
		detailsPanel.add(TeamTF);
		detailsPanel.add(CountryLabel);
		detailsPanel.add(CountryTF);
		detailsPanel.add(WagesLabel);
		detailsPanel.add(WagesTF);
		detailsPanel.add(BonusLabel);
		detailsPanel.add(BonusTF);

		//setup details panel and add the components to it
		exportButtonPanel=new JPanel();
		exportButtonPanel.setLayout(new GridLayout(3,2));
		exportButtonPanel.setBackground(Color.lightGray);
		exportButtonPanel.setBorder(BorderFactory.createTitledBorder(lineBorder, "Export Data"));
		exportButtonPanel.add(NumMakes);
		exportButtonPanel.add(NumMakesTF);
		exportButtonPanel.add(AverageWageForCountry);
		exportButtonPanel.add(AverageWageForCountryTF);
		exportButtonPanel.add(ListAllTeams);
		exportButtonPanel.add(ListAllModels);
		exportButtonPanel.setSize(500, 200);
		exportButtonPanel.setLocation(3, 300);
		content.add(exportButtonPanel);

		insertButton.setSize(100, 30);
		updateButton.setSize(100, 30);
		exportButton.setSize (100, 30);
		deleteButton.setSize (100, 30);
		clearButton.setSize (100, 30);

		insertButton.setLocation(370, 10);
		updateButton.setLocation(370, 110);
		exportButton.setLocation (370, 160);
		deleteButton.setLocation (370, 60);
		clearButton.setLocation (370, 210);

		insertButton.addActionListener(this);
		updateButton.addActionListener(this);
		exportButton.addActionListener(this);
		deleteButton.addActionListener(this);
		clearButton.addActionListener(this);

		this.ListAllTeams.addActionListener(this);
		this.NumMakes.addActionListener(this);
		this.AverageWageForCountry.addActionListener(this);
		this.ListAllModels.addActionListener(this);

		content.add(insertButton);
		content.add(updateButton);
		content.add(exportButton);
		content.add(deleteButton);
		content.add(clearButton);


		TableofDBContents.setPreferredScrollableViewportSize(new Dimension(900, 300));

		dbContentsPanel=new JScrollPane(TableofDBContents,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		dbContentsPanel.setBackground(Color.lightGray);
		dbContentsPanel.setBorder(BorderFactory.createTitledBorder(lineBorder,"Database Content"));

		detailsPanel.setSize(360, 300);
		detailsPanel.setLocation(3,0);
		dbContentsPanel.setSize(700, 300);
		dbContentsPanel.setLocation(477, 0);

		content.add(detailsPanel);
		content.add(dbContentsPanel);

		setSize(982,645);
		setVisible(true);

		TableModel.refreshFromDB(stmt);
	}

	public void initiate_db_conn()
	{
		try
		{
			// Load the JConnector Driver
			Class.forName("com.mysql.jdbc.Driver");
			// Specify the DB Name
			String url="jdbc:mysql://localhost:3306/moto_gp";
			// Connect to DB using DB URL, Username and password
			con = DriverManager.getConnection(url, "root", "");
			//Create a generic statement which is passed to the TestInternalFrame1
			stmt = con.createStatement();
		}
		catch(Exception e)
		{
			System.out.println("Error: Failed to connect to database\n"+e.getMessage());
		}
	}

	//event handling 
	public void actionPerformed(ActionEvent e)
	{
		Object target=e.getSource();
		if (target == clearButton)
		{
			IDTF.setText("");
			FirstNameTF.setText("");
			LastNameTF.setText("");
			RiderNumberTF.setText("");
			MakeTF.setText("");
			ModelTF.setText("");
			YearTF.setText("");
			TeamTF.setText("");
			CountryTF.setText("");
			WagesTF.setText("");
			BonusTF.setText("");
		}

		if (target == insertButton)
		{		 
			try
			{
//				String updateTemp ="INSERT INTO entrants VALUES("+
//						null +",'"+FirstNameTF.getText()+"','"+LastNameTF.getText()+"','"+RiderNumberTF.getText()+"','"+MakeTF.getText()+"','"
//						+ModelTF.getText()+"','"+YearTF.getText()+"','"+TeamTF.getText()+"','"+CountryTF.getText()+"','"+WagesTF.getText()+"','"+BonusTF.getText()+"');";

				String updateTemp ="Call spInsertNew("+
						null +",'"+FirstNameTF.getText()+"','"+LastNameTF.getText()+"','"+RiderNumberTF.getText()+"','"+MakeTF.getText()+"','"
						+ModelTF.getText()+"','"+YearTF.getText()+"','"+TeamTF.getText()+"','"+CountryTF.getText()+"','"+WagesTF.getText()+"','"+BonusTF.getText()+"');";
				stmt.executeUpdate(updateTemp);

			}
			catch (SQLException sqle)
			{
				System.err.println("Error with  insert:\n"+sqle.toString());
			}
			finally
			{
				TableModel.refreshFromDB(stmt);
			}
		}
		if (target == deleteButton)
		{

			try
			{
				String updateTemp ="DELETE FROM entrants WHERE id = "+IDTF.getText()+";";
				stmt.executeUpdate(updateTemp);

			}
			catch (SQLException sqle)
			{
				System.err.println("Error with delete:\n"+sqle.toString());
			}
			finally
			{
				TableModel.refreshFromDB(stmt);
			}
		}
		if (target == updateButton)
		{
			try
			{
				if((IDTF.getText()!=null) || (!IDTF.getText().equals(""))) {
//					String updateTemp = "UPDATE entrants SET " +
//							"first_name = '" + FirstNameTF.getText() +
//							"', surname = '" + LastNameTF.getText() +
//							"', race_id = '" + RiderNumberTF.getText() +
//							"', make = '" + MakeTF.getText() +
//							"', model = '" + ModelTF.getText() +
//							"', bike_year = '" + YearTF.getText() +
//							"', team = '" + TeamTF.getText() +
//							"', country = '" + CountryTF.getText() +
//							"', wages = '" + WagesTF.getText() +
//							"', win_bonus = '" + BonusTF.getText() +
//							"' where id = " + IDTF.getText();
					String updateTemp = "Call spUpdateAll(" +
							"'" + IDTF.getText()
							+ "','" + FirstNameTF.getText()
							+ "','" + LastNameTF.getText()
							+ "','" + RiderNumberTF.getText()
							+ "','" + MakeTF.getText()
							+ "','" + ModelTF.getText()
							+ "','" + YearTF.getText()
							+ "','" + TeamTF.getText()
							+ "','" + CountryTF.getText()
							+ "','" + WagesTF.getText()
							+ "','" + BonusTF.getText() + "')";

					System.out.println(updateTemp);
					stmt.executeUpdate(updateTemp);
				}
				//these lines do nothing but the table updates when we access the db.
				rs = stmt.executeQuery("SELECT * from entrants");
				rs.next();
				rs.close();	
			}
			catch (SQLException sqle){
				System.err.println("Error with  update:\n"+sqle.toString());
			}
			finally{
				TableModel.refreshFromDB(stmt);
			}
		}

		/////////////////////////////////////////////////////////////////////////////////////
		//I have only added functionality of 2 of the button on the lower right of the template
		///////////////////////////////////////////////////////////////////////////////////

		if(target == this.ListAllTeams){

			cmd = "select distinct team from entrants;";

			try{					
				rs= stmt.executeQuery(cmd); 	
				writeToFile(rs);
			}
			catch(Exception e1){e1.printStackTrace();}

		}

		if(target == this.NumMakes){
			String manufacturers = this.NumMakesTF.getText();

			cmd = "select make, count(*) "+  "from entrants " + "where make = '"  +manufacturers+"';";

			System.out.println(cmd);
			try{					
				rs= stmt.executeQuery(cmd); 	
				writeToFile(rs);
			}
			catch(Exception e1){e1.printStackTrace();}

		}

		if(target == this.AverageWageForCountry){
			String country = this.AverageWageForCountryTF.getText();

			cmd = "select round(avg(wages), 2) "+  "from entrants " + "where country = '"  +country+"';";

			System.out.println(cmd);
			try{
				rs= stmt.executeQuery(cmd);
				writeToFile(rs);
			}
			catch(Exception e1){e1.printStackTrace();}

		}
		if(target == this.ListAllModels){

			cmd = "select distinct model from entrants;";

			try{
				rs= stmt.executeQuery(cmd);
				writeToFile(rs);
			}
			catch(Exception e1){e1.printStackTrace();}

		}
	}
	///////////////////////////////////////////////////////////////////////////

	private void writeToFile(ResultSet rs){
		try{
			System.out.println("In writeToFile");
			FileWriter outputFile = new FileWriter("Sheila.csv");
			PrintWriter printWriter = new PrintWriter(outputFile);
			ResultSetMetaData rsmd = rs.getMetaData();
			int numColumns = rsmd.getColumnCount();

			for(int i=0;i<numColumns;i++){
				printWriter.print(rsmd.getColumnLabel(i+1)+",");
			}
			printWriter.print("\n");
			while(rs.next()){
				for(int i=0;i<numColumns;i++){
					printWriter.print(rs.getString(i+1)+",");
				}
				printWriter.print("\n");
				printWriter.flush();
			}
			printWriter.close();
		}
		catch(Exception e){e.printStackTrace();}
	}
}
