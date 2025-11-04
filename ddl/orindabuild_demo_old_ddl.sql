CREATE OR REPLACE PACKAGE simple_examples AS
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
PROCEDURE direct_flight_available(p_fromcity in  airports.airport_code%TYPE
                                 ,p_tocity   in  airports.airport_code%TYPE
                                 ,p_direct   out boolean);
--
PROCEDURE find_direct_flights    (p_fromcity   in  airports.airport_code%TYPE
                                 ,p_tocity     in  airports.airport_code%TYPE
                                 ,p_startdate  in date
                                 ,p_enddate    in date
                                 ,p_orderby    in  number
                                 ,p_flightlist out flights_refcursor_type);
--
END;
.
/

show errors
CREATE OR REPLACE PACKAGE complex_example AS
--
TYPE city_pair_plsql_record IS RECORD (from_city flights.departure_city%type
                                      ,to_city   flights.arrival_city%type);
--
PROCEDURE flies_between_all (p_first_city_pair  in  city_pair_oracle_type
                            ,p_second_city_pair in  city_pair_plsql_record
                            ,p_third_city_from  in  airports%ROWTYPE
                            ,p_third_city_to    in  airports%ROWTYPE
                            ,p_airline_list     out simple_examples.airline_refcursor_type);
--
PROCEDURE get_table_of_flights_from (p_city         in     flights.departure_city%TYPE
                                    ,p_flights_from    out flights_table);
--
PROCEDURE add_bookings (p_customer       in     customers%ROWTYPE
                       ,p_booking_table  in     bookings_table
                       ,p_status_message    out varchar2);
--
END;
.
/
show errors

CREATE OR REPLACE PACKAGE BODY simple_examples AS
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
PROCEDURE direct_flight_available(p_fromcity in airports.airport_code%TYPE
                                 ,p_tocity   in airports.airport_code%TYPE
                                 ,p_direct   out boolean) IS
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
--
BEGIN
--
  OPEN checkFlight;
--
  FETCH checkFlight INTO l_dummy;
--
  IF checkFlight%FOUND THEN
--
    p_direct := TRUE;
--
  ELSE
--
    p_direct := FALSE;
--
  END IF;
--
  CLOSE checkFlight;
--
END;
--
PROCEDURE find_direct_flights    (p_fromcity   in  airports.airport_code%TYPE
                                 ,p_tocity     in  airports.airport_code%TYPE
                                 ,p_startdate  in date
                                 ,p_enddate    in date
                                 ,p_orderby    in  number
                                 ,p_flightlist out flights_refcursor_type) IS
--
  l_startdate DATE := p_startdate;
  l_enddate DATE := p_enddate;
--
BEGIN
--
  IF p_startdate IS NULL THEN
--
    l_startdate := to_date('01-01-1900','DD-MM-YYYY'); 
--
  END IF;
--
  IF p_enddate IS NULL THEN
--
    l_enddate := to_date('01-01-2100','DD-MM-YYYY'); 
--
  END IF;
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
    AND   f.departure_time BETWEEN l_startdate AND l_enddate
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
    AND   f.departure_time BETWEEN l_startdate AND l_enddate
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
    AND   f.departure_time BETWEEN l_startdate AND l_enddate
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

CREATE OR REPLACE PACKAGE BODY complex_example AS
--
PROCEDURE flies_between_all (p_first_city_pair  in  city_pair_oracle_type
                            ,p_second_city_pair in  city_pair_plsql_record
                            ,p_third_city_from  in  airports%ROWTYPE
                            ,p_third_city_to    in  airports%ROWTYPE
                            ,p_airline_list     out simple_examples.airline_refcursor_type) IS
--
BEGIN
--
  OPEN p_airline_list 
  FOR SELECT a.*
  FROM airlines a
  WHERE EXISTS
        (SELECT null
         FROM   flights f1
         WHERE  f1.departure_city = p_first_city_pair.from_city
         AND    f1.arrival_city   = p_first_city_pair.to_city
         AND    f1.airline_name   = a.airline_name)
  AND  EXISTS
        (SELECT null
         FROM   flights f2
         WHERE  f2.departure_city = p_second_city_pair.from_city
         AND    f2.arrival_city   = p_second_city_pair.to_city
         AND    f2.airline_name   = a.airline_name)
  AND  EXISTS
        (SELECT null
         FROM   flights f3
         WHERE  f3.departure_city = p_third_city_from.airport_code
         AND    f3.arrival_city   = p_third_city_to.airport_code
         AND    f3.airline_name   = a.airline_name)
  ORDER BY a.airline_name;
--
END;
--
PROCEDURE get_table_of_flights_from (p_city         in     flights.departure_city%TYPE
                                    ,p_flights_from    out flights_table) IS
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
    p_flights_from(p_flights_from.COUNT) := l_flight;
--
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
PROCEDURE add_bookings (p_customer       in     customers%ROWTYPE
                       ,p_booking_table  in     bookings_table
                       ,p_status_message    out varchar2) IS
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
END;
.
/
show errors

exit
