# cloud-serverless
由Java编写
MiraiBot-QQ机器人、discord机器人、kook机器人、定时任务、httpClient、web服务等模块的集成项目。
具有转发消息、爬取网页内容、定时任务等能力。

## 导出maven依赖
```
mvn dependency:copy-dependencies -DoutputDirectory=D:\lib
```

## 给sh脚本添加权限
```shell
chmod -R 777 run.sh
```

## 添加定时任务
在最后一行加上定时任务的配置 `/etc/crontab` `每三天的17:30执行一次`
```
SHELL=/bin/bash
PATH=/sbin:/bin:/usr/sbin:/usr/bin
MAILTO=root

# For details see man 4 crontabs

# Example of job definition:
# .---------------- minute (0 - 59)
# |  .------------- hour (0 - 23)
# |  |  .---------- day of month (1 - 31)
# |  |  |  .------- month (1 - 12) OR jan,feb,mar,apr ...
# |  |  |  |  .---- day of week (0 - 6) (Sunday=0 or 7) OR sun,mon,tue,wed,thu,fri,sat
# |  |  |  |  |
# *  *  *  *  * user-name  command to be executed
  30  17  */3  *  * root  /home/cloud-serverless/run.sh
```
## 修改crontab的配置后需要重启crontab，使配置生效
```shell
# 保存任务
crontab /etc/crontab
# 查看任务
crontab -l
# 其他命令
systemctl start crond
systemctl stop crond
systemctl restart crond
systemctl reload crond
systemctl status crond
```

## 许可证
本项目使用`Apache-2.0 license`许可证。许可证的完整文本可参见[LICENSE](https://github.com/JokerIvanZK/cloud-serverless/blob/master/LICENSE)

## 支持
如果有任何问题或建议，欢迎提[issue](https://github.com/JokerIvanZK/cloud-serverless/issues)
