package com.orindasoft.demo;

import oracle.jdbc.OracleConnection;

import com.orindasoft.pub.*;
import java.sql.*;

import com.orindasoft.demo.generated.DAOFactory;
import com.orindasoft.demo.generated.plsql.*;
import com.orindasoft.demo.generated.table.instance.*;
import com.orindasoft.demo.generated.table.manager.*;



public class OrindaBuildDAOExample
{
public void showDAOFactory(java.sql.Connection theConnection
		    , com.orindasoft.pub.LogInterface theLog)
{
	try 
	{
	  theLog.info("");
	  theLog.info("Creating an instance of the DAO Factory class.");
	  theLog.info("");

	  // Create an instance of the DAO Factory class. You'll need to 
	  // make sure that it knows how to connect to the database in step 4.3
	  // The example assumes a hard coded connect string of:
	  // jdbc:oracle:thin:ORINDADEMO/ORINDADEMO@localhost:1521:ORCL
	  // If this isn't valid you'll need to either edit the generated code
	  // (a bad idea!) or re-run OrindaBuild and change the connect string
	  // in step 4.3. If you enter the wrong connect string you won't notice
	  // until you attempt to access the database - the Data Access Object
	  // factory class doesn't bother getting a database connection until it 
	  // needs to.

	  DAOFactory theFactory = new DAOFactory(theConnection, theLog);

	
	  theLog.info("Getting airports DAO...");
	  // Get airports data access object. 
	  AirportsMgr theAirportsMgr = theFactory.getAirportsTableDAO();

	  theLog.info("Getting airport record for San Francisco...");
	  // Now we have an instance of AirportsMgr we can get an instance by 
	  // using the Primary Key of airports...
	  AirportsRow sfo = theAirportsMgr.getByPk("SFO");

	  theLog.info("Found airport: " + sfo.getRowAirportCode() + " " + sfo.getRowAirportName());
	  theLog.info("Finding flights that leave from SFO:");

	  // We pass the record for 'sfo' to the airports manager DAO and ask it
	  // to follow the foreign key to Flights....

	  FlightsRow[] sfoFlights = theAirportsMgr.getChildByFkFltDepCity(sfo);

	  for (int i=0; i < 10 && i < sfoFlights.length; i++)
	    {
	  	theLog.info(i + " " + sfoFlights[i].getRowAirlineName() + " " 
	  			    + sfoFlights[i].getRowFlightNumberInt() + " to "
					+ sfoFlights[i].getRowArrivalCity());
	    }

	  theLog.info("Creating a new airline...");
	  // Get airlines data access object. 
	  AirlinesMgr theAirlinesMgr = theFactory.getAirlinesTableDAO();
	  
	  AirlinesRow newAirline = new AirlinesRow();
	  newAirline.rowAirlineName =("ThinAir");

	  theAirlinesMgr.rowInsert(newAirline);

	  theLog.info("Created 'ThinAir'");

	  // Now delete our record, so we can run this demo again...

	  theAirlinesMgr.rowDelete(newAirline);
	  theLog.info("Deleted 'ThinAir'");

	  theLog.info("");
	  theLog.info("Now use a Data Access Object to run a stored procedure.");
	  theLog.info("");

	  // Get an instance of SimpleExamplesDirectFlightAvailable from the
	  // DAOFactory class. Note that we don't have to worry about database
	  // Connections as the DAO Factory looks after them for us.
	  CursorExampleDirectFlightAvailable directFlightAvailable = theFactory.getCursorExampleDirectFlightAvailablePlSqlDAO();
	  
	  directFlightAvailable.setParamPFromcity("SFO");
	  directFlightAvailable.setParamPFromcity("LAX");
	  directFlightAvailable.executeProc();
	  
	  if (directFlightAvailable.getFunctionResultBoolean())
	    {
	  	theLog.info("A direct flight exists between SFO and LAX");
	    }
	  else
	    {
	  	theLog.info("No direct flight exists between SFO and LAX");
	    }	  

	  theLog.info("Closing database connection.");
	  // release database connection. Note that the factory class will inform
	  // any Data Access Objects we created for us.
	  theFactory.commit();
	  theFactory.releaseResources();
	  
	  theLog.info("DAOFactoryDemo Finished");
	  theLog.info("");

	  }
	catch (com.orindasoft.pub.CSException e)
	  {
	  theLog.syserror(e);
	  if (e.getMessage().startsWith("Login Error"))
	    {
	    theLog.info("Check the 'confirmConnection' method of DAOFactory");
	    theLog.info("and make sure the connect string is valid.");
	    }
	  theLog.syserror("Exiting due to OrindaSoft library Exception");
	  System.exit(2);
	  }
	catch (Exception e)
	  {
	  theLog.syserror(e);
	  theLog.syserror("Exiting due to Other Exception");
	  System.exit(4);
	  }


	
}
}
