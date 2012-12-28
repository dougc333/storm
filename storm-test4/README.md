Maven fix needed when running mvn eclipse:eclipse: 
https://github.com/mfriedenhagen/dummy-lifecycle-mapping-plugin

With the Eclipse maven plugin installed you won't be able to run mvn install when right clicking on the pom.xml under the storm-test4 directory


corrupt .project file 

I get a pom.xml file cannot be found in Eclipse even though the file is in the storm-test4 directory. 
Delete the .classpath, .settings dir, .project files and rerun mvn eclipse:eclipse again



