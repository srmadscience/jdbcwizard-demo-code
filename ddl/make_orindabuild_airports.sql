set echo off
set feedback 0
set pages 0
col foo format a132
set lines 180

delete flight_datapoints where upper(departure_city) like  '%NEWARK%';
delete flight_datapoints where upper(arrival_city) like  '%NEWARK%';

select 'INSERT INTO airports (airport_code, airport_name) VALUES ('''||
replace(replace(
substr(departure_city,instr(departure_city,'('))
,'(','')
,')','')
||''','''|| departure_city||''');' foo
from flight_datapoints f
where upper(departure_city) not like '%SPAIN%'
and upper(departure_city) not like '%UPDATE%'
and departure_city is not null
and length(replace(replace(
substr(departure_city,instr(departure_city,'('))
,'(','')
,')','') ) > 0
group by
'INSERT INTO airports (airport_code, airport_name) VALUES ('''||
replace(replace(
substr(departure_city,instr(departure_city,'('))
,'(','')
,')','')
||''','''|| departure_city||''');'
order by 1
/

select 'INSERT INTO airports (airport_code, airport_name) VALUES ('''||
replace(replace(
substr(arrival_city,instr(arrival_city,'('))
,'(','')
,')','')
||''','''|| arrival_city||''');' foo
from flight_datapoints f
where upper(arrival_city) not like '%SPAIN%'
and upper(arrival_city) not like '%UPDATE%'
and arrival_city is not null
and length(replace(replace(
substr(arrival_city,instr(arrival_city,'('))
,'(','')
,')','') ) > 0
group by
'INSERT INTO airports (airport_code, airport_name) VALUES ('''||
replace(replace(
substr(arrival_city,instr(arrival_city,'('))
,'(','')
,')','')
||''','''|| arrival_city||''');'
order by 1
/

