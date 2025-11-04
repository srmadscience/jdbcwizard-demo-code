/**
 * 
 */
package com.orindasoft.demo;

import com.orindasoft.pub.*;

import com.orindasoft.demo.generated.*;
import com.orindasoft.demo.generated.plsql.*;
import com.orindasoft.demo.generated.table.instance.AirlinesRow;
import com.orindasoft.demo.generated.table.instance.AirportsRow;
import com.orindasoft.demo.generated.table.instance.FlightsRow;
import com.orindasoft.demo.generated.table.manager.AirlinesMgr;
import com.orindasoft.demo.generated.table.manager.AirportsMgr;

/**
 * @author Orinda Software, Dublin, Ireland. www.orindasoft.com
 * 
 */
public class RunOrindaBuildExamples {

	public RunOrindaBuildExamples() {
		// see 'main' method ...
	}


	private static void showPlsqlArrayPassing(
			com.orindasoft.pub.LogInterface theLog,
			com.orindasoft.demo.generated.DAOFactoryServiceImpl generatedService) {

		/*
		 * In the demo below we will get an array of flights leaving from a
		 * given airport and then create an array of bookings for a few of them.
		 */

		// We're going to look for flights from Los Angeles...
		final String departureCity = "LAX";

		try {
			/*
			 * Get a list of flights that leave our city. Note how little actual
			 * code is needed to do this. By examining the generated code you
			 * can see just how much time and energy it saves. Our goal is to
			 * call the PL/SQL procedure below:
			 * 
			 * TYPE flights_plsql_array IS TABLE OF flights%ROWTYPE;
			 * 
			 * PROCEDURE get_plsql_array_of_flights(p_city in
			 * flights.departure_city%TYPE ,p_flights_from out nocopy
			 * flights_plsql_array);
			 * 
			 * It isn't possible to directly call this from Java. Instead we use
			 * one of the TYPE objects we created above and run a anonymous
			 * PL/SQL block that unloads the PL/SQL package array (which JDBC
			 * can't handle) into an Oracle TYPE array (which it can):
			 * 
			 * DECLARE
			 * 
			 * p_city VARCHAR2(16) := ?; p_flights_from_A OB446NDD6_A :=
			 * OB446NDD6_A(); p_flights_from
			 * PACKAGE_ARRAY_EXAMPLE.FLIGHTS_PLSQL_ARRAY :=
			 * PACKAGE_ARRAY_EXAMPLE.FLIGHTS_PLSQL_ARRAY();
			 * 
			 * BEGIN
			 * 
			 * PACKAGE_ARRAY_EXAMPLE.GET_PLSQL_ARRAY_OF_FLIGHTS(p_city,
			 * p_flights_from);
			 * 
			 * IF p_flights_from.COUNT > 0 THEN
			 * p_flights_from_A.EXTEND(p_flights_from.COUNT); FOR i IN
			 * p_flights_from.FIRST..p_flights_from.LAST LOOP
			 * p_flights_from_A(i) := OB446NDD6_T
			 * (p_flights_from(i).DEPARTURE_CITY ,p_flights_from(i).ARRIVAL_CITY
			 * ,p_flights_from(i).AIRLINE_NAME ,p_flights_from(i).FLIGHT_NUMBER
			 * ,p_flights_from(i).DEPARTURE_TIME ,p_flights_from(i).ARRIVAL_TIME
			 * ,p_flights_from(i).AIRCRAFT_TYPE); END LOOP; END IF; ? :=
			 * p_flights_from_A; END;
			 */

			// FlightsAttrs is a generated class that maps to the FLIGHTS table.
			// We populate it
			// by calling the service.
			FlightsAttrs[] cityFlights = generatedService
					.servicePlsqlPackageArrayExampleGetPlsqlArrayOfFlights(departureCity);

			// To prove that we did in fact get data back log a message
			theLog.info(cityFlights.length + " flights departing "
					+ departureCity + " found.");

			/*
			 * Having got our flights we then need to make some bookings. This
			 * involves passing a %rowtype record that represents a CUSTOMER and
			 * a PL/SQL VARRAY using JDBC.
			 * 
			 * The procedure returns a VARCHAR2 with a status message.
			 * 
			 * TYPE bookings_plsql_array IS VARRAY(30) OF bookings%ROWTYPE;
			 * 
			 * PROCEDURE add_bookings_plsql_array (p_customer in
			 * customers%ROWTYPE ,p_booking_table in bookings_plsql_array
			 * ,p_status_message out nocopy varchar2);
			 * 
			 * As in the previous case this can't be called directly from Java.
			 * We pass a %ROWTYPE parameter to JDBC by passing in the individual
			 * fields and assembling a record in the anonymous block. We also
			 * pass in a PL/SQL package array parameter by sending it as an
			 * Oracle TYPE array and loading it into an appropriate variable
			 * before we issue the PL/SQL call. In the code below the Oracle
			 * TYPE array we use for transport is called 'p_booking_table_A' and
			 * the PL/SQL package array variable is called 'p_booking_table'.
			 * 
			 * DECLARE
			 * 
			 * p_customer CUSTOMERS%ROWTYPE; p_booking_table_A OB46ND2_A :=
			 * OB46ND2_A(); p_booking_table
			 * PACKAGE_ARRAY_EXAMPLE.BOOKINGS_PLSQL_ARRAY :=
			 * PACKAGE_ARRAY_EXAMPLE.BOOKINGS_PLSQL_ARRAY(); p_status_message
			 * VARCHAR2(4000) := null;
			 * 
			 * BEGIN
			 * 
			 * p_customer.name := ?; p_customer.address := ?; p_customer.city :=
			 * ?; p_customer.state := ?; p_customer.zip := ?;
			 * p_customer.birthdate := ?; p_customer.phone := ?;
			 * 
			 * p_booking_table_A := ?; p_status_message := null;
			 * 
			 * IF p_booking_table_A.COUNT > 0 THEN
			 * p_booking_table.EXTEND(p_booking_table_A.COUNT); FOR i IN
			 * p_booking_table_A.FIRST..p_booking_table_A.LAST LOOP
			 * p_booking_table(i).CUSTOMER_NAME := p_booking_table_A(i).COL_0;
			 * p_booking_table(i).AIRLINE_NAME := p_booking_table_A(i).COL_1;
			 * p_booking_table(i).FLIGHT_NUMBER := p_booking_table_A(i).COL_2;
			 * p_booking_table(i).DEPARTURE_TIME := p_booking_table_A(i).COL_3;
			 * p_booking_table(i).SEAT := p_booking_table_A(i).COL_4; END LOOP;
			 * END IF;
			 * 
			 * PACKAGE_ARRAY_EXAMPLE.ADD_BOOKINGS_PLSQL_ARRAY(p_customer,
			 * p_booking_table,p_status_message); ? := p_status_message; END;
			 */

			// As in the first example we have generated classed that match the
			// database
			// objects we work with. We create some to use as parameters.
			Customers ourCustomer = new Customers();
			ourCustomer.paramName = "John Smith";
			ourCustomer.paramBirthdate = new java.util.Date(System
					.currentTimeMillis());
			ourCustomer.paramCity = "Walnut Creek";
			ourCustomer.paramState = "CA";
			ourCustomer.paramPhone = "555 1212";

			Bookings ourFirstBooking = new Bookings();
			ourFirstBooking.paramCustomerName = ourCustomer.paramName;
			ourFirstBooking.paramAirlineName = cityFlights[0].paramAirlineName;
			ourFirstBooking.paramFlightNumber = cityFlights[0].paramFlightNumber;
			ourFirstBooking.paramDepartureTime = cityFlights[0].paramDepartureTime;
			ourFirstBooking.paramSeat = "1F";

			Bookings ourSecondBooking = new Bookings();
			ourSecondBooking.paramCustomerName = ourCustomer.paramName;
			ourSecondBooking.paramAirlineName = cityFlights[1].paramAirlineName;
			ourSecondBooking.paramFlightNumber = cityFlights[1].paramFlightNumber;
			ourSecondBooking.paramDepartureTime = cityFlights[1].paramDepartureTime;
			ourSecondBooking.paramSeat = "1A";

			Bookings ourThirdBooking = new Bookings();
			ourThirdBooking.paramCustomerName = ourCustomer.paramName;
			ourThirdBooking.paramAirlineName = cityFlights[2].paramAirlineName;
			ourThirdBooking.paramFlightNumber = cityFlights[2].paramFlightNumber;
			ourThirdBooking.paramDepartureTime = cityFlights[2].paramDepartureTime;
			ourThirdBooking.paramSeat = "2A";

			Bookings[] ourBookingArray = { ourFirstBooking, ourSecondBooking,
					ourThirdBooking };

			// The service returns a status message that describes what it did
			String statusMessage = generatedService
					.servicePlsqlPackageArrayExampleAddBookingsPlsqlArray(
							ourCustomer, ourBookingArray);

			// log the status message.
			theLog.info(statusMessage);

		} catch (DAOFactoryServiceException e) {

			theLog.error(e);
		}

	}

	private static void showDaoFunctionality(ConsoleLog theLog,
			DAOFactoryServiceImpl generatedService) {
		try {
			theLog.info("");
			theLog.info("Creating an instance of the DAO Factory class.");
			theLog.info("");

			// We don't need to do anything to create a DAO Factory -
			// the generated service extends a DAO factory class, so we
			// cast it back to DAOFFactory for the purposes of this demo

			DAOFactory theFactory = generatedService;

			theLog.info("Getting airports DAO...");
			// Get airports data access object.
			AirportsMgr theAirportsMgr = theFactory.getAirportsTableDAO();

			theLog.info("Getting airport record for San Francisco...");
			// Now we have an instance of AirportsMgr we can get an instance by
			// using the Primary Key of airports...
			AirportsRow sfo = theAirportsMgr.getByPk("SFO");

			theLog.info("Found airport: " + sfo.getRowAirportCode() + " "
					+ sfo.getRowAirportName());
			theLog.info("Finding flights that leave from SFO:");

			// We pass the record for 'sfo' to the airports manager DAO and ask
			// it
			// to follow the foreign key to Flights....

			FlightsRow[] sfoFlights = theAirportsMgr
					.getChildByFkFltDepCity(sfo);

			for (int i = 0; i < 10 && i < sfoFlights.length; i++) {
				theLog.info(i + " " + sfoFlights[i].getRowAirlineName() + " "
						+ sfoFlights[i].getRowFlightNumberInt() + " to "
						+ sfoFlights[i].getRowArrivalCity());
			}

			theLog.info("Creating a new airline...");
			// Get airlines data access object.
			AirlinesMgr theAirlinesMgr = theFactory.getAirlinesTableDAO();

			AirlinesRow newAirline = new AirlinesRow();
			newAirline.rowAirlineName = ("ThinAir");

			theAirlinesMgr.rowInsert(newAirline);

			theLog.info("Created 'ThinAir'");

			// Now delete our record, so we can run this demo again...

			theAirlinesMgr.rowDelete(newAirline);
			theLog.info("Deleted 'ThinAir'");

			theLog.info("");
			theLog
					.info("Now use a Data Access Object to run a stored procedure.");
			theLog
					.info("We'll call 'directFlightAvailable' for the parameters 'SFO' and 'LAX'");

			// Get an instance of SimpleExamplesDirectFlightAvailable from the
			// DAOFactory class. Note that we don't have to worry about database
			// Connections as the DAO Factory looks after them for us.
			CursorExampleDirectFlightAvailable directFlightAvailable = theFactory
					.getCursorExampleDirectFlightAvailablePlSqlDAO();

			directFlightAvailable.setParamPFromcity("SFO");
			directFlightAvailable.setParamPFromcity("LAX");
			directFlightAvailable.executeProc();

			if (directFlightAvailable.getFunctionResultBoolean()) {
				theLog.info("A direct flight exists between SFO and LAX");
			} else {
				theLog.info("No direct flight exists between SFO and LAX");
			}

			theLog.info("Closing database connection.");
			// release database connection. Note that the factory class will
			// inform
			// any Data Access Objects we created for us.
			// eFactory.commit();
			theFactory.releaseResources();

			theLog.info("DAOFactoryDemo Finished");
			theLog.info("");

		} catch (com.orindasoft.pub.CSException e) {
			theLog.syserror(e);
			if (e.getMessage().startsWith("Login Error")) {
				theLog
						.info("Check the 'confirmConnection' method of DAOFactory");
				theLog.info("and make sure the connect string is valid.");
			}
			theLog.syserror("Exiting due to OrindaSoft library Exception");
			System.exit(2);
		} catch (Exception e) {
			theLog.syserror(e);
			theLog.syserror("Exiting due to Other Exception");
			System.exit(4);
		}

	}

	private static void showCursorExample(ConsoleLog theLog,
			DAOFactoryServiceImpl generatedService) {
		try {

			theLog
					.info("Get list of Airports, Airplanes and Airlines in our database");
			theLog
					.info("To do this we call a procedure that returns multiple cursors...");
			theLog.info("");

			// Because web services take the 'functions must not modify
			// their arguments' rule seriously JDBCWizard creates special
			// Java classes to hold output parameters when a procedure returns
			// more than 1.
			CursorExampleGetlistsReturn theLists = generatedService
					.servicePlsqlCursorExampleGetlists();

			// 'theLists' consists of 3 arrays. Print out the first few elements
			// of each.

			theLog.info(" ");
			theLog
					.info(" The first ten airfields in our database. Note that our database contains ");
			theLog
					.info(" lots of obscure airfields that don't have scheduled passenger services");
			theLog.info(" ");

			// Loop through airportList until we get to 10 records or run out of
			// records
			for (int i = 0; i < 10 && i < theLists.paramPAirportListing.length; i++) {
				// Once you have picked your row most methods will operate on
				// the current one.
				// the ":" character is used as a field seperator
				// the "?" character is used when the field is unprintable.
				theLog.info("   "
						+ theLists.paramPAirportListing[i].paramAirportCode
						+ " "
						+ theLists.paramPAirportListing[i].paramAirportName);
			}

			theLog.info(" ");
			theLog.info(" The first ten airlines in our database ");
			theLog.info(" ");

			// Loop through airline List until we get to 10 records or run out
			// of records
			for (int i = 0; i < 10 && i < theLists.paramPAirlineListing.length; i++) {
				theLog.info("   "
						+ theLists.paramPAirlineListing[i].paramAirlineName);
			}

			theLog.info(" ");
			theLog.info(" The first ten aircraft types in our database ");
			theLog.info(" ");

			// Loop through aircraft List until we get to 10 records or run out
			// of records
			for (int i = 0; i < 10 && i < theLists.paramPAircraftListing.length; i++) {
				theLog.info("   "
						+ theLists.paramPAircraftListing[i].paramAircraftType);
			}

		} catch (DAOFactoryServiceException e) {

			theLog.error(e);
		}

		String fromCity = "LAX";
		String toCity = "JFK";

		try {

			boolean flightAvailable = generatedService
					.servicePlsqlCursorExampleDirectFlightAvailable(fromCity,
							toCity);

			if (flightAvailable) {
				theLog.info("A direct flight from " + fromCity + " to "
						+ toCity + " is available");

			} else {
				theLog.info("No direct flight from " + fromCity + " to "
						+ toCity + " is available");

			}
		} catch (DAOFactoryServiceException e) {

			theLog.error(e);
		}

		try {
			double customOrderBy = 3;

			CursorExampleFlightsRefcursorTypeAttrs[] directFlightsArray = generatedService
					.servicePlsqlCursorExampleFindDirectFlights(fromCity,
							toCity, customOrderBy);

			theLog.info("   Airline,Flight, From => To,Equipment");

			for (int i = 0; i < 7 && i < directFlightsArray.length; i++) {
				theLog.info("   " + directFlightsArray[i].paramAirlineName
						+ ", "
						+ directFlightsArray[i].paramFlightNumber.toString()
						+ ", " + directFlightsArray[i].paramDepartureCity
						+ " => " + directFlightsArray[i].paramArrivalCity
						+ ", " + directFlightsArray[i].paramAircraftType);
			}

		} catch (DAOFactoryServiceException e) {

			theLog.error(e);
		}

		try {
			double customOrderBy = 3;

			CursorExampleFlightsRefcursorTypeAttrs[] directFlightsArray = generatedService
					.servicePlsqlCursorExampleFindDirectFlights(fromCity,
							toCity, customOrderBy);

			theLog.info("   Airline,Flight, From => To,Equipment");

			for (int i = 0; i < 7 && i < directFlightsArray.length; i++) {
				theLog.info("   " + directFlightsArray[i].paramAirlineName
						+ ", "
						+ directFlightsArray[i].paramFlightNumber.toString()
						+ ", " + directFlightsArray[i].paramDepartureCity
						+ " => " + directFlightsArray[i].paramArrivalCity
						+ ", " + directFlightsArray[i].paramAircraftType);
			}

		} catch (DAOFactoryServiceException e) {

			theLog.error(e);
		}
	}

	private static void showObjectArrayPassing(ConsoleLog theLog,
			DAOFactoryServiceImpl generatedService) {
		/*
		 * In the demo below we will get an array of flights leaving from a
		 * given airport and then create an array of bookings for a few of them.
		 */

		// We're going to look for flights from Oakland...
		final String departureCity = "OAK";

		try {
			/*
			 * Get a list of flights that leave our city. Note how little actual
			 * code is needed to do this. By examining the generated code you
			 * can see just how much time and energy it saves. Our goal is to
			 * call the PL/SQL procedure below:
			 * 
			 * PROCEDURE get_object_array_of_flights(p_city in
			 * flights.departure_city%TYPE ,p_flights_from out nocopy
			 * flights_table); -- PROCEDURE add_bookings_object_array
			 * (p_customer in customers%ROWTYPE ,p_booking_table in
			 * bookings_table ,p_status_message out nocopy varchar2); -- END;
			 */

			// FlightsAttrs is a generated class that maps to the FLIGHTS table.
			// We populate it
			// by calling the service.
			FlightsTypeAttrs[] cityFlights = generatedService
					.servicePlsqlObjectArrayExampleGetObjectArrayOfFlights(departureCity);

			// To prove that we did in fact get data back log a message
			theLog.info(cityFlights.length + " flights departing "
					+ departureCity + " found.");

			/*
			 * Having got our flights we then need to make some bookings. This
			 * involves passing a %rowtype record that represents a CUSTOMER and
			 * an Oracle TYPE array using JDBC.
			 * 
			 * The procedure returns a VARCHAR2 with a status message.
			 * 
			 * PROCEDURE add_bookings_object_array (p_customer in
			 * customers%ROWTYPE ,p_booking_table in bookings_table
			 * ,p_status_message out nocopy varchar2);
			 */

			// As in the first example we have generated classed that match the
			// database
			// objects we work with. We create some to use as parameters.
			Customers ourCustomer = new Customers();
			ourCustomer.paramName = "John Smith";
			ourCustomer.paramBirthdate = new java.util.Date(System
					.currentTimeMillis());
			ourCustomer.paramCity = "Walnut Creek";
			ourCustomer.paramState = "CA";
			ourCustomer.paramPhone = "555 1212";

			BookingsTypeAttrs ourFirstBooking = new BookingsTypeAttrs();
			ourFirstBooking.paramCustomerName = ourCustomer.paramName;
			ourFirstBooking.paramAirlineName = cityFlights[0].paramAirlineName;
			ourFirstBooking.paramFlightNumber = cityFlights[0].paramFlightNumber;
			ourFirstBooking.paramDepartureTime = cityFlights[0].paramDepartureTime;
			ourFirstBooking.paramSeat = "1F";

			BookingsTypeAttrs ourSecondBooking = new BookingsTypeAttrs();
			ourSecondBooking.paramCustomerName = ourCustomer.paramName;
			ourSecondBooking.paramAirlineName = cityFlights[1].paramAirlineName;
			ourSecondBooking.paramFlightNumber = cityFlights[1].paramFlightNumber;
			ourSecondBooking.paramDepartureTime = cityFlights[1].paramDepartureTime;
			ourSecondBooking.paramSeat = "1A";

			BookingsTypeAttrs ourThirdBooking = new BookingsTypeAttrs();
			ourThirdBooking.paramCustomerName = ourCustomer.paramName;
			ourThirdBooking.paramAirlineName = cityFlights[2].paramAirlineName;
			ourThirdBooking.paramFlightNumber = cityFlights[2].paramFlightNumber;
			ourThirdBooking.paramDepartureTime = cityFlights[2].paramDepartureTime;
			ourThirdBooking.paramSeat = "2A";

			BookingsTypeAttrs[] ourBookingArray = { ourFirstBooking,
					ourSecondBooking, ourThirdBooking };

			// The service returns a status message that describes what it did
			String statusMessage = generatedService
					.servicePlsqlObjectArrayExampleAddBookingsObjectArray(
							ourCustomer, ourBookingArray);

			// log the status message.
			theLog.info(statusMessage);

		} catch (DAOFactoryServiceException e) {

			theLog.error(e);
		}
	}

	private static void showPlsqlIdxByArrayPassing(ConsoleLog theLog,
			DAOFactoryServiceImpl generatedService) {

		/*
		 * In the demo below we will get an array of flights leaving from a
		 * given airport and then create an array of bookings for a few of them.
		 */

		// We're going to look for flights from San Francisco...
		final String departureCity = "SFO";

		try {
			/*
			 * Get a list of flights that leave our city. Note how little actual
			 * code is needed to do this. By examining the generated code you
			 * can see just how much time and energy it saves. Our goal is to
			 * call the PL/SQL procedure below:
			 * 
			 * TYPE flights_plsql_array IS TABLE OF flights%ROWTYPE;
			 * 
			 * PROCEDURE get_plsql_array_of_flights(p_city in
			 * flights.departure_city%TYPE ,p_flights_from out nocopy
			 * flights_plsql_array);
			 * 
			 * It isn't possible to directly call this from Java. Instead we use
			 * one of the TYPE objects we created above and run a anonymous
			 * PL/SQL block that unloads the PL/SQL package array (which JDBC
			 * can't handle) into an Oracle TYPE array (which it can):
			 * 
			 * DECLARE
			 * 
			 * p_city VARCHAR2(16) := NULL; p_flights_from_A OSOFT446NDD6_A :=
			 * OSOFT446NDD6_A(); p_flights_from
			 * PACKAGE_IDXARRAY_EXAMPLE.FLIGHTS_PLSQL_ARRAY;
			 * 
			 * BEGIN p_city := ?;
			 * 
			 * PACKAGE_IDXARRAY_EXAMPLE.GET_PLSQL_ARRAY_OF_FLIGHTS(p_city,
			 * p_flights_from);
			 * 
			 * IF p_flights_from.COUNT > 0 THEN
			 * p_flights_from_A.EXTEND(p_flights_from.COUNT); FOR i IN
			 * p_flights_from.FIRST..p_flights_from.LAST LOOP IF
			 * p_flights_from.EXISTS(i) THEN p_flights_from_A(i) :=
			 * OSOFT446NDD6_T (p_flights_from(i).DEPARTURE_CITY
			 * ,p_flights_from(i).ARRIVAL_CITY ,p_flights_from(i).AIRLINE_NAME
			 * ,p_flights_from(i).FLIGHT_NUMBER
			 * ,p_flights_from(i).DEPARTURE_TIME ,p_flights_from(i).ARRIVAL_TIME
			 * ,p_flights_from(i).AIRCRAFT_TYPE); END IF; END LOOP; END IF; ? :=
			 * p_flights_from_A; END;
			 */

			// FlightsAttrs is a generated class that maps to the FLIGHTS table.
			// We populate it
			// by calling the service.
			FlightsAttrs[] cityFlights = generatedService
					.servicePlsqlPackageArrayExampleGetPlsqlArrayOfFlights(departureCity);

			// To prove that we did in fact get data back log a message
			theLog.info(cityFlights.length + " flights departing "
					+ departureCity + " found.");

			/*
			 * Having got our flights we then need to make some bookings. This
			 * involves passing a %rowtype record that represents a CUSTOMER and
			 * a PL/SQL VARRAY using JDBC.
			 * 
			 * The procedure returns a VARCHAR2 with a status message.
			 * 
			 * TYPE bookings_plsql_array IS VARRAY(30) OF bookings%ROWTYPE;
			 * 
			 * PROCEDURE add_bookings_plsql_array (p_customer in
			 * customers%ROWTYPE ,p_booking_table in bookings_plsql_array
			 * ,p_status_message out nocopy varchar2);
			 * 
			 * As in the previous case this can't be called directly from Java.
			 * We pass a %ROWTYPE parameter to JDBC by passing in the individual
			 * fields and assembling a record in the anonymous block. We also
			 * pass in a PL/SQL package array parameter by sending it as an
			 * Oracle TYPE array and loading it into an appropriate variable
			 * before we issue the PL/SQL call. In the code below the Oracle
			 * TYPE array we use for transport is called 'p_booking_table_A' and
			 * the PL/SQL package array variable is called 'p_booking_table'.
			 * 
			 * DECLARE
			 * 
			 * p_customer CUSTOMERS%ROWTYPE; p_booking_table_A OSOFT46ND2_A :=
			 * OSOFT46ND2_A();
			 * 
			 * p_booking_table PACKAGE_IDXARRAY_EXAMPLE.BOOKINGS_PLSQL_ARRAY;
			 * p_status_message VARCHAR2(4000) := null;
			 * 
			 * BEGIN
			 * 
			 * p_customer.name := ?; p_customer.address := ?; p_customer.city :=
			 * ?; p_customer.state := ?; p_customer.zip := ?;
			 * p_customer.birthdate := ?; p_customer.phone := ?;
			 * p_booking_table_A := ?; p_status_message := null;
			 * 
			 * IF p_booking_table_A.COUNT > 0 THEN FOR i IN
			 * p_booking_table_A.FIRST..p_booking_table_A.LAST LOOP
			 * p_booking_table(i).CUSTOMER_NAME := p_booking_table_A(i).COL_0;
			 * p_booking_table(i).AIRLINE_NAME := p_booking_table_A(i).COL_1;
			 * p_booking_table(i).FLIGHT_NUMBER := p_booking_table_A(i).COL_2;
			 * p_booking_table(i).DEPARTURE_TIME := p_booking_table_A(i).COL_3;
			 * p_booking_table(i).SEAT := p_booking_table_A(i).COL_4; END LOOP;
			 * END IF;
			 * 
			 * PACKAGE_IDXARRAY_EXAMPLE.ADD_BOOKINGS_PLSQL_ARRAY(p_customer,
			 * p_booking_table,p_status_message);
			 * 
			 * ? := p_status_message;
			 * 
			 * END;
			 */

			// As in the first example we have generated classed that match the
			// database
			// objects we work with. We create some to use as parameters.
			Customers ourCustomer = new Customers();
			ourCustomer.paramName = "John Smith";
			ourCustomer.paramBirthdate = new java.util.Date(System
					.currentTimeMillis());
			ourCustomer.paramCity = "Walnut Creek";
			ourCustomer.paramState = "CA";
			ourCustomer.paramPhone = "555 1212";

			Bookings ourFirstBooking = new Bookings();
			ourFirstBooking.paramCustomerName = ourCustomer.paramName;
			ourFirstBooking.paramAirlineName = cityFlights[0].paramAirlineName;
			ourFirstBooking.paramFlightNumber = cityFlights[0].paramFlightNumber;
			ourFirstBooking.paramDepartureTime = cityFlights[0].paramDepartureTime;
			ourFirstBooking.paramSeat = "1F";

			Bookings ourSecondBooking = new Bookings();
			ourSecondBooking.paramCustomerName = ourCustomer.paramName;
			ourSecondBooking.paramAirlineName = cityFlights[1].paramAirlineName;
			ourSecondBooking.paramFlightNumber = cityFlights[1].paramFlightNumber;
			ourSecondBooking.paramDepartureTime = cityFlights[1].paramDepartureTime;
			ourSecondBooking.paramSeat = "1A";

			Bookings ourThirdBooking = new Bookings();
			ourThirdBooking.paramCustomerName = ourCustomer.paramName;
			ourThirdBooking.paramAirlineName = cityFlights[2].paramAirlineName;
			ourThirdBooking.paramFlightNumber = cityFlights[2].paramFlightNumber;
			ourThirdBooking.paramDepartureTime = cityFlights[2].paramDepartureTime;
			ourThirdBooking.paramSeat = "2A";

			Bookings[] ourBookingArray = { ourFirstBooking, ourSecondBooking,
					ourThirdBooking };

			// The service returns a status message that describes what it did
			String statusMessage = generatedService
					.servicePlsqlPackageArrayExampleAddBookingsPlsqlArray(
							ourCustomer, ourBookingArray);

			// log the status message.
			theLog.info(statusMessage);

		} catch (DAOFactoryServiceException e) {

			theLog.error(e);
		}

	}

	private static void createExtraTypeObjects(ConsoleLog theLog,
			DAOFactoryServiceImpl generatedService) {
		/*
		 * Before we can pass a PL/SQL array to Java we must create some extra
		 * Oracle TYPE objects first.
		 * 
		 * This step only needs to happen once - once the objects are created
		 * the rest of the generated code will always run. We provide both the
		 * Java method below and a SQL script for the objects.
		 * 
		 * The statement below creates the following objects:
		 * 
		 * CREATE OR REPLACE TYPE OB46ND2_T AS OBJECT (COL_0 VARCHAR2(16) ,COL_1
		 * VARCHAR2(64) ,COL_2 NUMBER ,COL_3 DATE ,COL_4 VARCHAR2(4));
		 * 
		 * CREATE OR REPLACE TYPE OB46ND2_A AS TABLE OF OB46ND2_T;
		 * 
		 * CREATE OR REPLACE TYPE OB446NDD6_T AS OBJECT" (COL_0 VARCHAR2(16)
		 * ,COL_1 VARCHAR2(16) ,COL_2 VARCHAR2(64) ,COL_3 NUMBER ,COL_4 DATE
		 * ,COL_5 DATE ,COL_6 VARCHAR2(64));
		 * 
		 * CREATE OR REPLACE TYPE OB446NDD6_A AS TABLE OF OB446NDD6_T;
		 * 
		 * Note that the objects have highly generic names. We do this for two
		 * reasons. Firstly it avoids confusion by not creating new Oracle TYPE
		 * objects that have names almost identical to existing PL/SQL Package
		 * Arrays. Secondly we want to reduce the number of objects we create to
		 * a minimum - a single generated TYPE object is used everywhere a
		 * record with the same pattern of column data types and lengths is
		 * encountered.
		 */
		try {
			generatedService.createExtraTypeObjects();
		} catch (CSException e1) {
			theLog.error(e1);
		}
	}

	private static void dropExtraTypeObjects(ConsoleLog theLog,
			DAOFactoryServiceImpl generatedService) {
		/*
		 * For the sake of neatness we drop the extra type objects. We wouldn't
		 * do this in real life, as we would need them the next time we ran the
		 * code. If you create and drop TYPE objects repeatedly within the same
		 * session you can also get ORA-902 for no apparent reason...
		 */
		try {
			generatedService.dropExtraTypeObjects();

		} catch (CSException e1) {
			theLog.error(e1);
		}

	}

	public static void main(String[] args) {

		com.orindasoft.pub.ConsoleLog theLog = new ConsoleLog();

		theLog.info("Log in to database");

		theLog.info("Create a service using our DB Connection");
		com.orindasoft.demo.generated.DAOFactoryServiceImpl generatedService 
		         = new DAOFactoryServiceImpl(theLog);

		theLog.info("Demonstrate using our service to exectute multiple database queries in one call");
		showCursorExample(theLog, generatedService);

		theLog
				.info("Demonstrate passing arrays of Oracle TYPE objects back and forth to PL/SQL");
		showObjectArrayPassing(theLog, generatedService);

		theLog
				.info("Create extra TYPE objects we need for passing arrays back and forth");
		createExtraTypeObjects(theLog, generatedService);

		theLog
				.info("Demonstrate passing PL/SQL arrays back and forth to PL/SQL");
		showPlsqlArrayPassing(theLog, generatedService);

		theLog
				.info("Demonstrate passing PL/SQL Index By arrays back and forth to PL/SQL");
		showPlsqlIdxByArrayPassing(theLog, generatedService);

		theLog
				.info("drop extra TYPE objects we needed for passing arrays back and forth");
		dropExtraTypeObjects(theLog, generatedService);

		theLog
				.info("Show how JDBCWizard also works as a Data Access Object (DAO) for Oracle");
		showDaoFunctionality(theLog, generatedService);

	}

}

