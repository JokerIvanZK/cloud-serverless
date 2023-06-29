#!/bin/sh

identifyname="cn.ivanzk.BootApplication"
basePath=$(cd $(dirname $0); pwd);
javaOpts="-Xms256m -Xmx256m -Xmn128m -Xss256k -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=128m"
javaOpts="${javaOpts} -Djava.rmi.server.hostname=127.0.0.1"
javaOpts="${javaOpts} -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=${basePath}/oom.hprof"
javaOpts="${javaOpts} -XX:SurvivorRatio=6 -XX:MaxTenuringThreshold=5 -XX:PretenureSizeThreshold=0 -XX:LargePageSizeInBytes=128m -XX:CMSInitiatingOccupancyFraction=92"
javaOpts="${javaOpts} -XX:+UseParNewGC -XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled -XX:+UseFastAccessorMethods -XX:+UseCMSInitiatingOccupancyOnly"

#检查并停止已经启动的服务
pid=`ps aux | grep $identifyname | grep -v grep | awk '{print $2}'`
if [ -n "$pid" ]
then
	kill -9 $pid
	echo "stop old service ["$identifyname"] pid:" $pid
fi

#删除日志目录
rm -rf ${basePath}/logs
rm -rf ${basePath}/tmp

#新启动服务
nohup java -server ${javaOpts} -classpath ${basePath}/.:${basePath}/lib/* ${identifyname} 推送 >/dev/null 2>&1 &
echo "service ["$identifyname"] started."
ps -ef|grep ${identifyname}
