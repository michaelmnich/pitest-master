java  -Xdebug -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=y -classpath D:\Doktorat\pitcmd\pitest-1.1.11-SNAPSHOT.jar;D:\Doktorat\pitcmd\pitest-command-line-1.1.11-SNAPSHOT.jar;C:\junit\hamcrest-core-1.3.jar;C:\junit\junit-4.12.jar;  org.pitest.mutationtest.commandline.MutationCoverageReport  --classPath D:\Doktorat\PitPlayground\IOgr602-master\target\test-classes\,D:\Doktorat\PitPlayground\IOgr602-master\target\classes\ --reportDir D:\trash\ --targetClasses matrixlibrary.* --targetTests matrixlibrary.* --sourceDirs D:\Doktorat\PitPlayground\IOgr602-master\