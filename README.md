# cloud-serverless

`cloud-serverless` 是一个 Java 21 / Spring Boot 4 的机器人集成服务，当前支持：

- Discord 消息监听，并按频道转发到 Mirai QQ 或 Kook。
- Mirai QQ 管理员心跳和好友/群消息发送。
- Kook 频道消息、私聊心跳和 ArcheAge 公告推送。
- HTTP 健康检查接口。

## 项目结构

```text
src/main/java/cn/ivanzk
├── app          # 启动入口
├── bot          # Discord / Kook / Mirai 平台适配
├── config       # cloud-serverless.* 统一配置
├── forwarding   # 跨平台消息转发
├── news         # ArcheAge 公告抓取与推送
├── scheduler    # Spring @Scheduled 定时任务
└── web          # 健康检查 API
```

## 运行要求

- JDK 21+
- Maven 3.9+

## 配置

默认使用 `dev` profile，所有机器人默认关闭，便于本地编译和冒烟启动。

核心配置前缀为 `cloud-serverless`：

```yaml
cloud-serverless:
  bots:
    discord:
      enabled: false
      token:
    kook:
      enabled: false
      token:
      admin:
    mirai:
      enabled: false
      qq:
      admin:
      friends: []
      groups: []
  forwarding:
    discord-to-mirai:
      enabled: false
      channel-names: [更新]
    discord-to-kook:
      enabled: false
      channel-names: [债券, admin测试]
```

真实 token、QQ 号、管理员账号等敏感配置不要提交到仓库，建议放在本地 `application-prod.yml`、环境变量或外部配置系统。

## 接口

- `GET /api/health/ping`：返回 `pong`
- `GET /api/health/state`：返回 Discord、Mirai、Kook 的 `enabled/available/online/message` 状态

## 构建与启动

```shell
mvn -q -DskipTests compile
mvn -q test
mvn -q package
./run.sh
```

Windows：

```bat
run.bat
```

## crontab 示例

如需通过系统 crontab 定期重启服务，可参考仓库根目录 `crontab`：

```text
30 17 */3 * * root /home/cloud-serverless/run.sh
```

修改 `/etc/crontab` 后按系统发行版要求重载或重启 crond。

## 许可证

本项目使用 Apache-2.0 license，详见 [LICENSE](LICENSE)。
