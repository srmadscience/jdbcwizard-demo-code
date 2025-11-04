

REM First create tables

CREATE TABLE AIRLINES
(AIRLINE_NAME VARCHAR2(50) NOT NULL);

CREATE TABLE AIRPORTS
(AIRPORT_CODE VARCHAR2(16) NOT NULL
,AIRPORT_NAME VARCHAR2(80));

CREATE TABLE AIRCRAFT
(AIRCRAFT_TYPE VARCHAR2(50) NOT NULL);

CREATE TABLE FLIGHTS
(DEPARTURE_CITY	 VARCHAR2(16) NOT NULL
,ARRIVAL_CITY    VARCHAR2(16) NOT NULL
,AIRLINE_NAME	 VARCHAR2(50) NOT NULL
,FLIGHT_NUMBER   NUMBER NOT NULL
,DEPARTURE_TIME  DATE NOT NULL
,ARRIVAL_TIME    DATE NOT NULL
,AIRCRAFT_TYPE   VARCHAR2(50) NOT NULL);

CREATE TABLE CUSTOMERS
(NAME		VARCHAR2(80) NOT NULL
,ADDRESS        VARCHAR2(50) 
,CITY           VARCHAR2(50) 
,STATE          VARCHAR2(2)  
,ZIP            NUMBER(5)    
,BIRTHDATE      DATE         
,PHONE		VARCHAR2(12));

CREATE TABLE BOOKINGS
(CUSTOMER_NAME   VARCHAR2(12) NOT NULL
,AIRLINE_NAME	 VARCHAR2(50) NOT NULL
,FLIGHT_NUMBER   NUMBER NOT NULL
,DEPARTURE_TIME  DATE NOT NULL
,SEAT            VARCHAR2(3));

REM Then add Primary keys

ALTER TABLE AIRLINES ADD
 (CONSTRAINT ALS_PK PRIMARY KEY (AIRLINE_NAME)
 USING INDEX);

ALTER TABLE AIRPORTS ADD
 (CONSTRAINT APT_PK PRIMARY KEY (AIRPORT_CODE)
 USING INDEX);
              
ALTER TABLE AIRCRAFT ADD
 (CONSTRAINT ACT_PK PRIMARY KEY (AIRCRAFT_TYPE)
 USING INDEX);
              
ALTER TABLE FLIGHTS ADD
 (CONSTRAINT FLT_PK PRIMARY KEY (AIRLINE_NAME, FLIGHT_NUMBER, DEPARTURE_TIME)
 USING INDEX);

ALTER TABLE CUSTOMERS ADD
 (CONSTRAINT CST_PK PRIMARY KEY (NAME)
 USING INDEX);

ALTER TABLE BOOKINGS ADD
(CONSTRAINT BKG_PK PRIMARY KEY 
(CUSTOMER_NAME  ,AIRLINE_NAME ,FLIGHT_NUMBER ,DEPARTURE_TIME ,SEAT)
 USING INDEX);

              
REM Once primary keys are in place add Foreign Keys

ALTER TABLE FLIGHTS ADD
 (CONSTRAINT FLT_DEP_CITY FOREIGN KEY (DEPARTURE_CITY) REFERENCES AIRPORTS);

ALTER TABLE FLIGHTS ADD
 (CONSTRAINT FLT_ARR_CITY FOREIGN KEY (ARRIVAL_CITY) REFERENCES AIRPORTS);

ALTER TABLE FLIGHTS ADD
 (CONSTRAINT FLT_AIRLINE FOREIGN KEY (AIRLINE_NAME) REFERENCES AIRLINES);

ALTER TABLE BOOKINGS ADD
 (CONSTRAINT BKG_CST FOREIGN KEY (CUSTOMER_NAME) REFERENCES CUSTOMERS);

ALTER TABLE BOOKINGS ADD
 (CONSTRAINT BKG_FLT FOREIGN KEY (AIRLINE_NAME, FLIGHT_NUMBER, DEPARTURE_TIME) REFERENCES FLIGHTS);

Rem Now Add some indices

ALTER TABLE FLIGHTS ADD
 (CONSTRAINT FLT_AIRCRAFT FOREIGN KEY (AIRCRAFT_TYPE) REFERENCES AIRCRAFT);

Rem Now Add some indices


CREATE INDEX FLT_IX1 ON FLIGHTS (DEPARTURE_CITY);

CREATE INDEX FLT_IX2 ON FLIGHTS (ARRIVAL_CITY);

CREATE INDEX FLT_IX3 ON FLIGHTS (AIRLINE_NAME,FLIGHT_NUMBER);

CREATE INDEX CST_IX1 ON CUSTOMERS (STATE);

REM create a sequence

CREATE sequence sequential_number_generator;

REM Create sample type

CREATE TYPE city_pair_oracle_type AS OBJECT
(FROM_CITY VARCHAR2(16)
,TO_CITY   VARCHAR2(16));
.
/

REM Create Booking Type

CREATE TYPE bookings_type AS OBJECT
(CUSTOMER_NAME   VARCHAR2(12) 
,AIRLINE_NAME    VARCHAR2(50) 
,FLIGHT_NUMBER   NUMBER 
,DEPARTURE_TIME  DATE 
,SEAT            VARCHAR2(3));
.
/

show errors

REM Create booking Table

CREATE TYPE bookings_table AS TABLE OF bookings_type;
.
/
show errors


REM create flights Type

CREATE TYPE flights_type AS OBJECT
(DEPARTURE_CITY  VARCHAR2(16) 
,ARRIVAL_CITY    VARCHAR2(16) 
,AIRLINE_NAME    VARCHAR2(50) 
,FLIGHT_NUMBER   NUMBER 
,DEPARTURE_TIME  DATE 
,ARRIVAL_TIME    DATE 
,AIRCRAFT_TYPE   VARCHAR2(50) )
.
/

REM Create flight Table

CREATE TYPE flights_table AS TABLE OF flights_type;
.
/
show errors

REM Create stored procedures


CREATE OR REPLACE PACKAGE CURSOR_EXAMPLE AS
--
/*
  This example shows how to call pl/sql procedure that returns one or more cursors from Java.

*/
--
TYPE airline_refcursor_type IS REF CURSOR RETURN airlines%ROWTYPE;
--
TYPE airport_refcursor_type IS REF CURSOR RETURN airports%ROWTYPE;
--
TYPE aircraft_refcursor_type IS REF CURSOR RETURN aircraft%ROWTYPE;
--
TYPE flights_refcursor_type  IS REF CURSOR RETURN flights%ROWTYPE;
--
PROCEDURE getLists(p_airline_listing  out airline_refcursor_type
                  ,p_airport_listing  out airport_refcursor_type
                  ,p_aircraft_listing out aircraft_refcursor_type);
--
FUNCTION direct_flight_available(p_fromcity in  airports.airport_code%TYPE
                                ,p_tocity   in  airports.airport_code%TYPE) RETURN boolean;
--
PROCEDURE find_direct_flights    (p_fromcity   in  airports.airport_code%TYPE
                                 ,p_tocity     in  airports.airport_code%TYPE
                                 ,p_orderby    in  number
                                 ,p_flightlist out flights_refcursor_type);
--
END;
.
/

show errors


CREATE OR REPLACE PACKAGE OBJECT_ARRAY_EXAMPLE AS
/*
  This example shows how to call pl/sql procedure that takes an array defined
  as an oracle TYPE object as a parameter.

  We call this directly from Java using JDBC and a class that is represented
  by oracle.sql.ARRAY. Our generated Java class will use the JDBC interface
  oracle.sql.Datum
*/
--
-- p_city is the same data type as the column DEPARTURE_CITY in the FLIGHTS table.
-- p_flights_from uses the Oracle TYPE object flights_table.
--
PROCEDURE get_object_array_of_flights(p_city         in            flights.departure_city%TYPE
                                     ,p_flights_from    out nocopy flights_table);
--
PROCEDURE add_bookings_object_array (p_customer       in             customers%ROWTYPE
                                    ,p_booking_table  in             bookings_table
                                    ,p_status_message    out nocopy varchar2);
--
END;
.
/

show errors 


CREATE OR REPLACE PACKAGE PACKAGE_ARRAY_EXAMPLE AS
/*
 This example shows how to call pl/sql procedure that takes a parameter defined
 in a PL/SQL package  as an array of %ROWTYPE (table record).

 There is no direct way to call this PL/SQL procedure from Java.

 Instead we use OrindaBuild to create Java JDBC code that can bypass this limitation and
 return or accept a PL/SQL array from Java using JDBC.
*/
--
TYPE flights_plsql_array IS TABLE OF flights%ROWTYPE;
--
TYPE bookings_plsql_array IS VARRAY(30) OF bookings%ROWTYPE;
--

PROCEDURE get_plsql_array_of_flights(p_city         in            flights.departure_city%TYPE
                                    ,p_flights_from    out nocopy flights_plsql_array);
--
PROCEDURE add_bookings_plsql_array (p_customer       in             customers%ROWTYPE
                                   ,p_booking_table  in             bookings_plsql_array
                                   ,p_status_message    out nocopy varchar2);
--
END PACKAGE_ARRAY_EXAMPLE;
.
/

show errors
 
CREATE OR REPLACE PACKAGE PACKAGE_IDXARRAY_EXAMPLE AS
/*
 This example shows how to call pl/sql procedure that takes a parameter defined
 in a PL/SQL package  as an index by array of %ROWTYPE (table record).

 There is no direct way to call this PL/SQL procedure from Java.

 Instead we use OrindaBuild to create Java JDBC code that can bypass this limitation
 and return an index by array from Java using JDBC.
*/
--
TYPE flights_plsql_array IS TABLE OF flights%ROWTYPE INDEX BY BINARY_INTEGER;
--
TYPE bookings_plsql_array IS TABLE OF bookings%ROWTYPE INDEX BY BINARY_INTEGER;
--

PROCEDURE get_plsql_array_of_flights(p_city         in            flights.departure_city%TYPE
                                    ,p_flights_from    out nocopy flights_plsql_array);
--
PROCEDURE add_bookings_plsql_array (p_customer       in             customers%ROWTYPE
                                   ,p_booking_table  in             bookings_plsql_array
                                   ,p_status_message    out nocopy varchar2);
--
END PACKAGE_IDXARRAY_EXAMPLE;
.
/

show errors

create or replace PACKAGE BODY cursor_example AS
--
PROCEDURE getLists(p_airline_listing out airline_refcursor_type
                  ,p_airport_listing out airport_refcursor_type
                  ,p_aircraft_listing out aircraft_refcursor_type) IS
--
BEGIN
--
  OPEN p_airline_listing
  FOR select   a.*
      from     airlines a
      order by airline_name;
--
  OPEN p_airport_listing
  FOR select   a.*
      from     airports a
      order by airport_code;
--
  OPEN p_aircraft_listing
  FOR select   a.*
      from     aircraft a
      order by aircraft_type;
--
END;
--
FUNCTION  direct_flight_available(p_fromcity in airports.airport_code%TYPE
                                 ,p_tocity   in airports.airport_code%TYPE) RETURN boolean IS
--
  CURSOR checkFlight IS
  SELECT null
  FROM   dual
  WHERE exists (SELECT flight_number
                FROM   flights
                WHERE  departure_city = p_fromcity
                AND    arrival_city = p_tocity);
--
  l_dummy VARCHAR2(1) := NULL;
  l_return BOOLEAN := FALSE;
--
BEGIN
--
  OPEN checkFlight;
--
  FETCH checkFlight INTO l_dummy;
--
  IF checkFlight%FOUND THEN
--
    l_return := TRUE;
--
  ELSE
--
    l_return := FALSE;
--
  END IF;
--
  CLOSE checkFlight;
--
  RETURN(l_return);
--
END;
--
PROCEDURE find_direct_flights    (p_fromcity   in  airports.airport_code%TYPE
                                 ,p_tocity     in  airports.airport_code%TYPE
                                 ,p_orderby    in  number
                                 ,p_flightlist out flights_refcursor_type) IS
--
BEGIN
--
  IF p_orderby = 1 THEN
--
-- Order by airline...
--
    OPEN p_flightlist
    FOR
    SELECT f.*
    FROM flights f
    WHERE f.departure_city = p_fromcity
    AND   f.arrival_city   = p_tocity
    ORDER by f.airline_name;
--
  ELSIF p_orderby = 2 THEN
--
-- Order by departure time...
--
    OPEN p_flightlist
    FOR
    SELECT f.*
    FROM flights f
    WHERE f.departure_city = p_fromcity
    AND   f.arrival_city   = p_tocity
    ORDER by f.departure_time;
--
  ELSE
--
-- Order by aircraft type...
--
    OPEN p_flightlist
    FOR
    SELECT f.*
    FROM flights f
    WHERE f.departure_city = p_fromcity
    AND   f.arrival_city   = p_tocity
    ORDER by f.aircraft_type;
--
  END IF;
--
END;
--
END;
.
/

show errors


CREATE OR REPLACE
PACKAGE BODY OBJECT_ARRAY_EXAMPLE AS
--
PROCEDURE get_object_array_of_flights(p_city         in            flights.departure_city%TYPE
                                     ,p_flights_from    out nocopy flights_table) IS
--
CURSOR departs_from IS
SELECT *
FROM flights
WHERE p_city = departure_city;
--
l_flight flights_type := null;
--
BEGIN
--
  p_flights_from := flights_table();
--
  FOR departs_from_rec IN departs_from LOOP
--
    p_flights_from.extend;
    l_flight := flights_type(departs_from_rec.departure_city
                            ,departs_from_rec.arrival_city
                            ,departs_from_rec.airline_name
                            ,departs_from_rec.flight_number
                            ,departs_from_rec.departure_time
                            ,departs_from_rec.arrival_time
                            ,departs_from_rec.aircraft_type);
    --
    p_flights_from(p_flights_from.COUNT) := l_flight;
--
  END LOOP;
--
END;
--
PROCEDURE add_bookings_object_array (p_customer       in             customers%ROWTYPE
                                    ,p_booking_table  in             bookings_table
                                    ,p_status_message    out nocopy varchar2) IS
--
BEGIN
--
  p_status_message := 'New Flights for '||p_customer.name||': ';
--
-- First create the customer. Ignore error if aleady exists...
--
  BEGIN
--
    INSERT INTO customers
    (name,address,city,zip,birthdate,phone)
    VALUES
    (p_customer.name,p_customer.address,p_customer.city,p_customer.zip,p_customer.birthdate,p_customer.phone);
--
  EXCEPTION WHEN dup_val_on_index THEN null;
--
  END;
--
-- Attempt to create all the bookings referrer to in bookings_table.
-- If we get an exception set the status message.
--
  FOR i IN p_booking_table.FIRST .. p_booking_table.LAST LOOP
--
-- Delete booking if it already exists - this is so this demo is repeatable. Hopefully
-- this doesn't happen in real booking systems.
--
    DELETE bookings
    WHERE customer_name  = p_booking_table(i).customer_name
    AND   airline_name   = p_booking_table(i).airline_name
    AND   flight_number  = p_booking_table(i).flight_number
    AND   departure_time = p_booking_table(i).departure_time
    AND   seat           = p_booking_table(i).seat;
--
-- Attempt to create this booking...
--
    INSERT INTO bookings
    (customer_name, airline_name
    , flight_number, departure_time, seat)
    VALUES
    (p_booking_table(i).customer_name, p_booking_table(i).airline_name
    , p_booking_table(i).flight_number, p_booking_table(i).departure_time, p_booking_table(i).seat);
--
    p_status_message := p_status_message || 'Added '|| p_booking_table(i).airline_name
      || ' ' || p_booking_table(i).flight_number || ' ' || p_booking_table(i).departure_time  || ';';
--
  END LOOP;
--
  COMMIT;
--
EXCEPTION
--
WHEN others THEN
--
  p_status_message := p_status_message || sqlerrm;
--
  ROLLBACK;
--
END;
--
END OBJECT_ARRAY_EXAMPLE;
.
/

show errors








create or replace PACKAGE BODY PACKAGE_ARRAY_EXAMPLE AS
--
PROCEDURE get_plsql_array_of_flights(p_city         in            flights.departure_city%TYPE
                                    ,p_flights_from    out nocopy flights_plsql_array) IS
--
CURSOR departs_from IS
SELECT *
FROM flights
WHERE p_city = departure_city;
--
l_flight flights%ROWTYPE := null;
--
BEGIN
--
  p_flights_from := flights_plsql_array();
--
  FOR departs_from_rec IN departs_from LOOP
--
    p_flights_from.extend;
    l_flight := departs_from_rec;
    p_flights_from(p_flights_from.COUNT) := l_flight;
--
  END LOOP;
--
END;
--
PROCEDURE add_bookings_plsql_array (p_customer       in             customers%ROWTYPE
                                   ,p_booking_table  in             bookings_plsql_array
                                   ,p_status_message    out nocopy varchar2) IS
--
BEGIN
--
  p_status_message := 'New Flights for '||p_customer.name||': ';
--
-- First create the customer. Ignore error if aleady exists...
--
  BEGIN
--
    INSERT INTO customers
    (name,address,city,zip,birthdate,phone)
    VALUES
    (p_customer.name,p_customer.address,p_customer.city,p_customer.zip,p_customer.birthdate,p_customer.phone);
--
  EXCEPTION WHEN dup_val_on_index THEN null;
--
  END;
--
-- Attempt to create all the bookings referrer to in bookings_table.
-- If we get an exception set the status message.
--
  FOR i IN p_booking_table.FIRST .. p_booking_table.LAST LOOP
--
-- Delete booking if it already exists - this is so this demo is repeatable. Hopefully
-- this doesn't happen in real booking systems.
--
    DELETE bookings
    WHERE customer_name  = p_booking_table(i).customer_name
    AND   airline_name   = p_booking_table(i).airline_name
    AND   flight_number  = p_booking_table(i).flight_number
    AND   departure_time = p_booking_table(i).departure_time
    AND   seat           = p_booking_table(i).seat;
--
-- Attempt to create this booking...
--
    INSERT INTO bookings
    (customer_name, airline_name
    , flight_number, departure_time, seat)
    VALUES
    (p_booking_table(i).customer_name, p_booking_table(i).airline_name
    , p_booking_table(i).flight_number, p_booking_table(i).departure_time, p_booking_table(i).seat);
--
    p_status_message := p_status_message || 'Added '|| p_booking_table(i).airline_name
      || ' ' || p_booking_table(i).flight_number || ' ' || p_booking_table(i).departure_time  || ';';
--
  END LOOP;
--
  COMMIT;
--
EXCEPTION
--
WHEN others THEN
--
  p_status_message := p_status_message || sqlerrm;
--
  ROLLBACK;
--
END;
--
END package_ARRAY_EXAMPLE;
.
/

show errors

create or replace PACKAGE BODY PACKAGE_IDXARRAY_EXAMPLE AS
--
PROCEDURE get_plsql_array_of_flights(p_city         in            flights.departure_city%TYPE
                                    ,p_flights_from    out nocopy flights_plsql_array) IS
--
CURSOR departs_from IS
SELECT *
FROM flights
WHERE p_city = departure_city;
--
l_flight flights%ROWTYPE := null;
--
BEGIN
--
  FOR departs_from_rec IN departs_from LOOP
--
    l_flight := departs_from_rec;
    p_flights_from(p_flights_from.COUNT) := l_flight;
--
  END LOOP;
--
END;
--
PROCEDURE add_bookings_plsql_array (p_customer       in             customers%ROWTYPE
                                   ,p_booking_table  in             bookings_plsql_array
                                   ,p_status_message    out nocopy varchar2) IS
--
BEGIN
--
  p_status_message := '';
--
-- First create the customer. Ignore error if aleady exists...
--
  BEGIN
--
    INSERT INTO customers
    (name,address,city,zip,birthdate,phone)
    VALUES
    (p_customer.name,p_customer.address,p_customer.city,p_customer.zip,p_customer.birthdate,p_customer.phone);
--
  EXCEPTION WHEN dup_val_on_index THEN null;
--
  END;
--
-- Attempt to create all the bookings referrer to in bookings_table.
-- If we get an exception set the status message.
--
  FOR i IN p_booking_table.FIRST .. p_booking_table.LAST LOOP
--
-- Delete booking if it already exists - this is so this demo is repeatable. Hopefully
-- this doesn't happen in real booking systems.
--
    DELETE bookings
    WHERE customer_name  = p_booking_table(i).customer_name
    AND   airline_name   = p_booking_table(i).airline_name
    AND   flight_number  = p_booking_table(i).flight_number
    AND   departure_time = p_booking_table(i).departure_time
    AND   seat           = p_booking_table(i).seat;
--
-- Attempt to create this booking...
--
    INSERT INTO bookings
    (customer_name, airline_name
    , flight_number, departure_time, seat)
    VALUES
    (p_booking_table(i).customer_name, p_booking_table(i).airline_name
    , p_booking_table(i).flight_number, p_booking_table(i).departure_time, p_booking_table(i).seat);
--
    p_status_message := p_status_message || 'Added '|| p_booking_table(i).airline_name
      || ' ' || p_booking_table(i).flight_number || ' ' || p_booking_table(i).departure_time  || ';';
--
  END LOOP;
--
  COMMIT;
--
EXCEPTION
--
WHEN others THEN
--
  p_status_message := p_status_message || sqlerrm;
--
  ROLLBACK;
--
END;
--
END PACKAGE_IDXARRAY_EXAMPLE;
.
/

show errors



exit

