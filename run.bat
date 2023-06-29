set "javaOpts=-Xms2g -Xmx2g -Xmn1g -Xss256k -XX:MetaspaceSize=256m -XX:MaxMetaspaceSize=256m"
set "javaOpts=%javaOpts% -Dmirai.no-desktop"
set "javaOpts=%javaOpts% -Djava.rmi.server.hostname=127.0.0.1"
set "javaOpts=%javaOpts% -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=%cd%/oom"
set "javaOpts=%javaOpts% -XX:SurvivorRatio=6 -XX:MaxTenuringThreshold=5 -XX:PretenureSizeThreshold=0 -XX:LargePageSizeInBytes=128m -XX:CMSInitiatingOccupancyFraction=92"
set "javaOpts=%javaOpts% -XX:+UseParNewGC -XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled -XX:+UseFastAccessorMethods -XX:+UseCMSInitiatingOccupancyOnly"
java -server %javaOpts% -classpath %cd%/.;%cd%/lib/* cn.ivanzk.BootApplication