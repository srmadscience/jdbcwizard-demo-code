package com.orindasoft.demo;

/***
 This class demonstrates OrindaBuild's ability to 
 pass PL/SQL arrays and %ROWTYPE variables to PL/SQL procedures.

 This class assumes that the OrindaBuild database demo user
 has been created along with its tables and procedures.

 (c) 2003-2011 Orinda Software Ltd

 @version 6.0
 @author  <a href="http://www.orindasoft.com/?pdsrc=api" target="_blank" class=news>Orinda Software</a>

 */

// JDBCWizard generates code that uses a Java Library. If you buy JDBCWizard
// you get the right to use this library in code you generate.
import com.orindasoft.pub.*;

// com.orindasoft.example.generated contains the service and DAOFactory classes that you
// normally access in preference to working with the generated classes directly themselves
import com.orindasoft.demo.generated.*;
import com.orindasoft.demo.generated.plsql.*;

// We use Customer and Bookings classes created here in this example.

public class OrindaBuildPlsqlArrayExample {

	/*
	 This method shows how to pass a PL/SQL package array into Java using Oracle JDBC.
	 This is not possible by default because Oracle's JDBC implementation things that only
	 Oracle TYPE objects can be arrays. We get round this limitation by creating our own TYPE objects
	 and loading/unloading data from them into PL/SQL arrays as needed.
	 */
	public void showPlsqlArrayPassing(java.sql.Connection theConnection,
			com.orindasoft.pub.LogInterface theLog) {
		com.orindasoft.demo.generated.DAOFactoryServiceImpl generatedService = new DAOFactoryServiceImpl(
				theConnection, theLog);

		/*
		 Before we can pass a PL/SQL array to Java we must create some extra Oracle TYPE objects first.

  	     This step only needs to happen once - once the objects are created the rest of the generated code 
  	     will always run. We provide both the Java method below and a SQL script for the objects.
 
 		 The statement below creates the following objects:
 		 
 		    CREATE OR REPLACE TYPE OB46ND2_T AS OBJECT
            (COL_0 VARCHAR2(16)
            ,COL_1 VARCHAR2(64)
            ,COL_2 NUMBER
            ,COL_3 DATE
            ,COL_4 VARCHAR2(4));

            CREATE OR REPLACE TYPE OB46ND2_A  AS TABLE OF OB46ND2_T;
 
            CREATE OR REPLACE TYPE OB446NDD6_T AS OBJECT" 
            (COL_0 VARCHAR2(16)
            ,COL_1 VARCHAR2(16)
            ,COL_2 VARCHAR2(64)
            ,COL_3 NUMBER
            ,COL_4 DATE
            ,COL_5 DATE
            ,COL_6 VARCHAR2(64));

            CREATE OR REPLACE TYPE OB446NDD6_A  AS TABLE OF OB446NDD6_T;

         Note that the objects have highly generic names. We do this for two reasons. Firstly it avoids confusion by 
         not creating new Oracle TYPE objects that have names almost identical to existing PL/SQL Package Arrays. 
         Secondly we want to reduce the number of objects we create to a minimum - a single generated TYPE object
         is used everywhere a record with the same pattern of column data types and lengths is encountered.

		 */
		try {
			generatedService.createExtraTypeObjects();
		} catch (CSException e1) {
			theLog.error(e1);
		}


		/*
		 In the demo below we will get an array of flights leaving from a given airport and then
		 create an array of bookings for a few of them.
		 */
		
		// We're going to look for flights from Los Angeles...
		final String departureCity = "LAX";

		try {
			/* Get a list of flights that leave our city. Note how little actual code is needed to do this.
			   By examining the generated code you can see just how much time and energy it saves. Our goal is
			   to call the PL/SQL procedure below:
			  
			   TYPE flights_plsql_array IS TABLE OF flights%ROWTYPE;

			   PROCEDURE get_plsql_array_of_flights(p_city         in            flights.departure_city%TYPE
                                                   ,p_flights_from    out nocopy flights_plsql_array);
                                                   
               It isn't possible to directly call this from Java. Instead we use one of the TYPE objects
               we created above and run a anonymous PL/SQL block that unloads the PL/SQL package array (which JDBC 
               can't handle) into an Oracle TYPE array (which it can):
               
               DECLARE 

               p_city VARCHAR2(16) := ?; 
               p_flights_from_A OB446NDD6_A := OB446NDD6_A(); 
               p_flights_from PACKAGE_ARRAY_EXAMPLE.FLIGHTS_PLSQL_ARRAY := PACKAGE_ARRAY_EXAMPLE.FLIGHTS_PLSQL_ARRAY(); 

               BEGIN  
 
                 PACKAGE_ARRAY_EXAMPLE.GET_PLSQL_ARRAY_OF_FLIGHTS(p_city,p_flights_from); 
  
                 IF p_flights_from.COUNT > 0 THEN 
                    p_flights_from_A.EXTEND(p_flights_from.COUNT); 
                    FOR i IN p_flights_from.FIRST..p_flights_from.LAST LOOP  
                      p_flights_from_A(i) := OB446NDD6_T 
                          (p_flights_from(i).DEPARTURE_CITY 
                          ,p_flights_from(i).ARRIVAL_CITY 
                          ,p_flights_from(i).AIRLINE_NAME 
                          ,p_flights_from(i).FLIGHT_NUMBER 
                          ,p_flights_from(i).DEPARTURE_TIME 
                          ,p_flights_from(i).ARRIVAL_TIME 
                          ,p_flights_from(i).AIRCRAFT_TYPE); 
                    END LOOP; 
                  END IF; 
                ? := p_flights_from_A; 
              END; 


            */

			// FlightsAttrs is a generated class that maps to the FLIGHTS table. We populate it
			// by calling the service.
			FlightsAttrs[] cityFlights = generatedService
					.servicePlsqlPackageArrayExampleGetPlsqlArrayOfFlights(departureCity);
			
			// To prove that we did in fact get data back log a message
			theLog.info(cityFlights.length + " flights departing "
					+ departureCity + " found.");
			
			/* Having got our flights we then need to make some bookings. This involves
			   passing a %rowtype record that represents a CUSTOMER and a PL/SQL VARRAY using JDBC.
			   
			   The procedure returns a VARCHAR2 with a status message.
			   
                 TYPE bookings_plsql_array IS VARRAY(30) OF bookings%ROWTYPE;	
                 		    
                 PROCEDURE add_bookings_plsql_array (p_customer       in             customers%ROWTYPE
                                                    ,p_booking_table  in             bookings_plsql_array
                                                    ,p_status_message    out nocopy varchar2);
                                                    
                 As in the previous case this can't be called directly from Java. We pass a %ROWTYPE parameter to JDBC 
                 by passing in the individual fields and assembling a record in the anonymous block. We also 
                 pass in a PL/SQL package array parameter by sending it as an Oracle TYPE array and loading it 
                 into an appropriate variable before we issue the PL/SQL call. In the code below the Oracle TYPE array
                 we use for transport is called 'p_booking_table_A' and the PL/SQL package array variable is called 
                 'p_booking_table'.

                  DECLARE
                  
                  p_customer CUSTOMERS%ROWTYPE;
                  p_booking_table_A OB46ND2_A := OB46ND2_A();
                  p_booking_table PACKAGE_ARRAY_EXAMPLE.BOOKINGS_PLSQL_ARRAY := PACKAGE_ARRAY_EXAMPLE.BOOKINGS_PLSQL_ARRAY();
                  p_status_message VARCHAR2(4000) := null;
                 
                 BEGIN
                  
                  p_customer.name := ?;
                  p_customer.address := ?;
                  p_customer.city := ?;
                  p_customer.state := ?;
                  p_customer.zip := ?;
                  p_customer.birthdate := ?;
                  p_customer.phone := ?;
                  
                  p_booking_table_A := ?;
                  p_status_message := null;
                 
                  IF p_booking_table_A.COUNT > 0 THEN
                    p_booking_table.EXTEND(p_booking_table_A.COUNT);
                    FOR i IN p_booking_table_A.FIRST..p_booking_table_A.LAST LOOP
                        p_booking_table(i).CUSTOMER_NAME  := p_booking_table_A(i).COL_0;
                        p_booking_table(i).AIRLINE_NAME   := p_booking_table_A(i).COL_1;
                        p_booking_table(i).FLIGHT_NUMBER  := p_booking_table_A(i).COL_2;
                        p_booking_table(i).DEPARTURE_TIME := p_booking_table_A(i).COL_3;
                        p_booking_table(i).SEAT           := p_booking_table_A(i).COL_4;
                    END LOOP;
                  END IF;
 
                  PACKAGE_ARRAY_EXAMPLE.ADD_BOOKINGS_PLSQL_ARRAY(p_customer,p_booking_table,p_status_message);
                  ? := p_status_message;
                 END;
             */
		
			// As in the first example we have generated classed that match the database
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

		/* 
		 For the sake of neatness we drop the extra type objects. We wouldn't do this in real life,
		 as we would need them the next time we ran the code.
		 */
		try {
			generatedService.dropExtraTypeObjects();

		} catch (CSException e1) {
			theLog.error(e1);
		}

	}
}

