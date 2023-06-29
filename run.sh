#!/bin/sh

identifyname="cloud-serverless-1.0.jar"
basePath=$(cd $(dirname $0); pwd);

#检查并停止已经启动的服务
pid=`ps aux | grep $identifyname | grep -v grep | awk '{print $2}'`
if [ -n "$pid" ]
then
	kill -9 $pid
	echo "stop old service ["$identifyname"] pid:" $pid
fi

#进入目录
cd ${basePath}

#删除日志目录
rm -rf logs
rm -rf nohup.log

#新启动服务
nohup java -jar ${identifyname} > nohup.log 2>&1 &
echo "service ["$identifyname"] started."
ps -ef|grep ${identifyname}