set echo on
set feedback on
set showmode on

connect system/manager@&1

drop user orindademo cascade;

drop tablespace orindademo including contents;

create tablespace orindademo datafile 'C:\oracle\ora90\oradata\DB920A21\orindademo.dbf' size 10M reuse;

grant connect, resource,dba to orindademo identified by orindademo;

connect orindademo/orindademo@&1

@demo

show errors

exit



