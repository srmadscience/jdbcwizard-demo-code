set echo off
set feedback 0
set pages 0
col foo format a150
set lines 150


select
'INSERT INTO flights                                                           '
,'(airline_name, flight_number,aircraft_type, departure_time,arrival_time, arrival_city,departure_city)'
,' VALUES                                                                       '
,'('''|| airline ||''','''||flight_number ||''','''||aircraft_type||''','
,'to_date('''||replace(departure_time,':',' ')||''',''MM-DD-YYYY HH-MI PM''),'
,'to_date('''||replace(arrival_time,':',' ')||''',''MM-DD-YYYY HH-MI PM''),'''
||replace(replace( substr(arrival_city,instr(arrival_city,'(')) ,'(','') ,')','')
||''','''
||replace(replace( substr(departure_city,instr(departure_city,'(')) ,'(','') ,')','') 
||''');'
foo
from flight_datapoints
group by
'INSERT INTO flights                                                           '
,'(airline_name, flight_number,aircraft_type, departure_time,arrival_time, arrival_city,departure_city)'
,' VALUES                                                                       '
,'('''|| airline ||''','''||flight_number ||''','''||aircraft_type||''','
,'to_date('''||replace(departure_time,':',' ')||''',''MM-DD-YYYY HH-MI PM''),'
,'to_date('''||replace(arrival_time,':',' ')||''',''MM-DD-YYYY HH-MI PM''),'''
||replace(replace( substr(arrival_city,instr(arrival_city,'(')) ,'(','') ,')','')
||''','''
||replace(replace( substr(departure_city,instr(departure_city,'(')) ,'(','') ,')','') 
||''');'
order by 1
/

