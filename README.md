storm
=====

storm POC material

To run Storm in Pseudo-distributed mode:

First start redis using redis-server and then check the server is accessible using redis-cli. Once the cli is up do a get 1000 and see if there is a null or data being returned. 
If null, set a value for key 1000 like set 1000 "asdf" then do a get to verify the db works

Run the commands in start.sh to start the storm cluster on your dev machine

Run the command in runcommand with the jar in storm-test4 and verify first test4.demo.StormTest runs then test4.demo.StormTest1 then test4.demo.TestTrans

