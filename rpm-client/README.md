
## 远程端口映射程序-客户端

### 配置文件
```
# 客户端ID
cid: aaaassssdddd

# 服务端配置
serverConf:
  encryption: 1
  host: 127.0.0.1
  port: 9999
  sid: xdkjadfqwwefasdfasdfel123

# 转发配置
forwardOverServer:
- listenHost: 127.0.0.1
  listenPort: 8899
  forwardHost: 127.0.0.1
  forwardPort: 8800



# 其他参数
arguments:
  enableNettyLog: true
  nettyLogLevel: INFO

```
#### 客户端ID
**cid** 需要唯一否则后面注册的客户端会覆盖前面的客户端

#### 服务端配置
**host** 服务器监听地址  
**port** 服务器监听端口  
**sid**  服务器配置的sid，不能有误，作为认证加密的key  

#### 转发配置
**list结构，可配置多个**  
> 本地监听配置  
- listenHost: 本地监听地址
- listenPort: 本地监听端口  

> 远端转发配置  
- forwardHost: 经由服务器转发的目的地址
- forwardPort: 经由服务器转发的目的端口

#### 其他参数
