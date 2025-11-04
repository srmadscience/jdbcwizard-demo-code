set echo off
set feedback 0
set pages 0
set lines 132
col foo format a132

select 'PROMPT Creating aircraft records' from dual;

select 'INSERT INTO aircraft (aircraft_type) VALUES ('''|| aircraft_type ||''');' foo
from flight_datapoints
where aircraft_type is not null
group by 'INSERT INTO aircraft (aircraft_type) VALUES ('''|| aircraft_type ||''');'
order by 1
/

