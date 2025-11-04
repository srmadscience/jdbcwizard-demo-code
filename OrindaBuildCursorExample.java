package com.orindasoft.demo;

import oracle.jdbc.OracleConnection;

import com.orindasoft.pub.*;
import java.sql.*;

import com.orindasoft.demo.generated.*;
import com.orindasoft.demo.generated.plsql.*;

public class OrindaBuildCursorExample 
{

public void showReturningCursors(java.sql.Connection theConnection, com.orindasoft.pub.LogInterface theLog)
{
	com.orindasoft.demo.generated.DAOFactoryServiceInterface generatedService = new DAOFactoryServiceImpl(
			theConnection, theLog);

	try {
		

		theLog.info("Get list of Airports, Airplanes and Airlines in our database");
	    theLog.info("To do this we call a procedure that returns multiple cursors...");
	    theLog.info("");

	    // Because web services take the 'functions must not modify 
	    // their arguments' rule seriously JDBCWizard creates special 
	    // Java classes to hold output parameters when a procedure returns
	    // more than 1. 
	    CursorExampleGetlistsReturn theLists  
		  = generatedService.servicePlsqlCursorExampleGetlists();

	    // 'theLists' consists of 3 arrays. Print out the first few elements 
	    // of each. 
	    
	    theLog.info(" ");
	    theLog.info(" The first ten airfields in our database. Note that our database contains ");
	    theLog.info(" lots of obscure airfields that don't have scheduled passenger services");
	    theLog.info(" ");

	    // Loop through airportList until we get to 10 records or run out of records
	    for (int i=0; i < 10 && i <  theLists.paramPAirportListing.length; i++)
	      {
	      // Once you have picked your row most methods will operate on the current one.
	      // the ":" character is used as a field seperator
	      // the "?" character is used when the field is unprintable.
	      theLog.info("   " + theLists.paramPAirportListing[i].paramAirportCode + " " 
	      		    + theLists.paramPAirportListing[i].paramAirportName);
	      }

	    theLog.info(" ");
	    theLog.info(" The first ten airlines in our database ");
	    theLog.info(" ");

	    // Loop through airline List until we get to 10 records or run out of records
	    for (int i=0; i < 10 && i < theLists.paramPAirlineListing.length; i++)
	      {
	      theLog.info("   " + theLists.paramPAirlineListing[i].paramAirlineName);
	      }

	    theLog.info(" ");
	    theLog.info(" The first ten aircraft types in our database ");
	    theLog.info(" ");

	    // Loop through aircraft List until we get to 10 records or run out of records
	    for (int i=0; i < 10 && i < theLists.paramPAircraftListing.length; i++)
	      {
	      theLog.info("   " + theLists.paramPAircraftListing[i].paramAircraftType);
	      }			
		
	} catch (DAOFactoryServiceException e) {
		
		theLog.error(e);
	}
		
		
	String fromCity = "LAX";
	String toCity = "JFK";
		
		try {
			
			boolean flightAvailable = generatedService.servicePlsqlCursorExampleDirectFlightAvailable(fromCity,toCity);
			
			if (flightAvailable)
			{
				theLog.info("A direct flight from " + fromCity + " to " + toCity + " is available");
				
			}
			else
			{
				theLog.info("No direct flight from " + fromCity + " to " + toCity + " is available");
				
			}
		} catch (DAOFactoryServiceException e) {
		
			theLog.error(e);
		}
		
		try 
		{
			double customOrderBy = 3;
			
		CursorExampleFlightsRefcursorTypeAttrs[] directFlightsArray 
			  = generatedService.servicePlsqlCursorExampleFindDirectFlights
		    (fromCity
		    ,toCity
		    ,customOrderBy);
		  
			 theLog.info("   Airline,Flight, From => To,Equipment");
			 
             for (int i=0; i < 7 && i < directFlightsArray.length; i++)
		      {
		      theLog.info("   " + directFlightsArray[i].paramAirlineName 
	    		      + ", " + directFlightsArray[i].paramFlightNumber.toString()
	    		      + ", " + directFlightsArray[i].paramDepartureCity
	    		      + " => " + directFlightsArray[i].paramArrivalCity
	    		      + ", " + directFlightsArray[i].paramAircraftType
		    		      );
		      }			
			
			
		
	} catch (DAOFactoryServiceException e) {
		
		theLog.error(e);
	}		
	
	try 
	{
		double customOrderBy = 3;
		
		CursorExampleFlightsRefcursorTypeAttrs[] directFlightsArray 
		  = generatedService.servicePlsqlCursorExampleFindDirectFlights
	    (fromCity
	    ,toCity
	    ,customOrderBy);
	  
		 theLog.info("   Airline,Flight, From => To,Equipment");
		 
         for (int i=0; i < 7 && i < directFlightsArray.length; i++)
	      {
	      theLog.info("   " + directFlightsArray[i].paramAirlineName 
    		      + ", " + directFlightsArray[i].paramFlightNumber.toString()
    		      + ", " + directFlightsArray[i].paramDepartureCity
    		      + " => " + directFlightsArray[i].paramArrivalCity
    		      + ", " + directFlightsArray[i].paramAircraftType
	    		      );
	      }			
		
		
	
} catch (DAOFactoryServiceException e) {
	
	theLog.error(e);
}			                                   
}
	
	

}

