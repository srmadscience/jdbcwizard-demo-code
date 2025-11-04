
Installing the OrindaBuild Demo.
================================

In order to run the Demo you need to:

1. Create the tables

- Create a user called ORINDADEMO

- Use Sql*Plus to run the table creation script:

sqlplus orindademo/orindademo @orindabuild_demo_ddl


- Use Sql*Plus to run the sample data script:

sqlplus orindademo/orindademo @orindabuild_demo_dml

2. Run OrindaBuild

- Log in as OrindaDemo and create classes to call the OrindaDemo procedures, SQL files and Tables.

3. Use the generated classes.

- You can then modify the files com\orindasoft\demo\procedureDemo.java, sqlDemo.java and tableDemo.java to work against
  your database.
 




