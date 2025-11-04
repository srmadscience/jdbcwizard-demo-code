set echo off
set feedback 0
set pages 0
col foo format a132
set lines 132

select 'PROMPT Creating airline records' from dual;

select 'INSERT INTO airlines (airline_name) VALUES ('''|| airline ||''');' foo
from flight_datapoints
where airline is not null
and airline not like '%update%'
group by 'INSERT INTO airlines (airline_name) VALUES ('''|| airline ||''');'
order by 1
/

