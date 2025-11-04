
set feedback 0
set pages 0
set lines 120

select 'PROMPT Creating '||count(*)||' flight records' from flights;


select 'INSERT INTO flights                                                      '
     , '(departure_city, arrival_city,airline_name, arrival_time, departure_time,flight_number,aircraft_type)'
     , 'VALUES                                                                   '
     , '('''
        ||departure_city
        ||''','''
        ||arrival_city
        ||''','''
        || airline_name
        ||''','
	,'to_date('''||to_char(arrival_time,'MM-DD-YYYY HH-MI PM')||''',''MM-DD-YYYY HH-MI PM''),'
	,'to_date('''||to_char(departure_time,'MM-DD-YYYY HH-MI PM')||''',''MM-DD-YYYY HH-MI PM''),'
        ||flight_number||','''||aircraft_type ||''');'
from flights
order by airline_name, flight_number
/

