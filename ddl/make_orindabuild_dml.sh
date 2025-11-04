#/!bin/sh

DBNAME=DB920
SYSTEMID=system/manager
USERID=orindademo/orindademo

sqlplus -s ${SYSTEMID}@${DBNAME} @orindademo $DBNAME
sqlplus -s ${USERID}@${DBNAME} @flight_datapoints
echo 'exit' | sqlplus -s ${USERID}@${DBNAME} @../../../Lib/flights

rm -rf orindabuild_demo_dml.sql 2> /dev/null

echo 'REM create airlines' >> orindabuild_demo_dml.sql
echo '' >> orindabuild_demo_dml.sql
echo 'set feedback 0' >> orindabuild_demo_dml.sql
echo '' >> orindabuild_demo_dml.sql
echo 'exit' | sqlplus -s orindademo/orindademo@db920 @make_orindabuild_airlines >> orindabuild_demo_dml.sql
echo '' >> orindabuild_demo_dml.sql
echo 'REM create airports' >> orindabuild_demo_dml.sql
echo 'PROMPT Creating airport records' >> orindabuild_demo_dml.sql
echo 'exit' | sqlplus -s orindademo/orindademo@db920 @make_orindabuild_airports | sort -u >> orindabuild_demo_dml.sql
echo '' >> orindabuild_demo_dml.sql
echo 'REM create aircraft' >> orindabuild_demo_dml.sql
echo '' >> orindabuild_demo_dml.sql
echo 'exit' | sqlplus -s orindademo/orindademo@db920 @make_orindabuild_aircraft >> orindabuild_demo_dml.sql
echo '' >> orindabuild_demo_dml.sql
echo 'REM create flights ' >> orindabuild_demo_dml.sql
echo '' >> orindabuild_demo_dml.sql


echo 'exit' | sqlplus -s ${USERID}@${DBNAME} @orindabuild_demo_ddl
sleep  3
echo 'exit' | sqlplus -s ${USERID}@${DBNAME} @orindabuild_demo_dml
sleep  3
echo 'exit' | sqlplus -s ${USERID}@${DBNAME} @make_orindabuild_flights.sql > /tmp/$$.sql
echo 'exit' | sqlplus -s ${USERID}@${DBNAME}  @/tmp/$$.sql
rm /tmp/$$.sql

echo 'exit' | sqlplus -s ${USERID}@${DBNAME} @make_orindabuild_flights2.sql >> orindabuild_demo_dml.sql


echo '' >> orindabuild_demo_dml.sql
echo 'commit;' >> orindabuild_demo_dml.sql
echo '' >> orindabuild_demo_dml.sql
echo 'exit;' >> orindabuild_demo_dml.sql

