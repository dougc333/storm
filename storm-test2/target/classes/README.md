storm
=====

storm POC material


need a packet/second test
a clearer metadata+tuple test
remove field grouping/direct streams, only need multi fields and field grouping
add direct stream(perf diff between direct and non direct stream? )

TestStorm
1) test4.demo.TestStorm1:similar to ExclamationTopology, verifies storm setup correctly
2) test4.demo.TestStorm2: uses Redis, verifies Redis installed and Storm can access
3) test4.demo.TestStorm3: test multi field output and fieldgrouping. Spout emits 2 values in Value() and you designate each position in Value list to a field

where to add multi threads, tasks, numworkers, after performance framework is built or in same program? 

4) test4.demo.TestStorm4: test device packet performance? 
5) test4.demo.TestStorm5: test email/http server/client here remove perf fromo here
6) test4.demo.TestStorm6: ????
7) test5.demo.TestStorm7: ????

do we need to test serialization? not yet
test search bolt w/concurrency? yes.

