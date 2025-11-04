set echo on
set feedback on
set showmode on

connect system/manager@&1

drop user orindademo cascade;

drop tablespace orindademo including contents;

create tablespace orindademo datafile '/export/data/oradata/&1/orindademo.dbf' size 10M reuse;

grant connect, resource,dba to orindademo identified by "orindademo";


exit



