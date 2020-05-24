还没想好做成什么样子

传输层 SSL

零拷贝的ByteBuf缓冲

TCP UDP 支持

应该有一个工具集合
- socketClient
- socketServer
- UDP 工具

大概应该是这个样子

## 远程端口映射程序
> rpm =  remote port mapper



### 正向端口映射
将公网上的服务端口，映射到本地

**常规**
客户端程序  ----> ~  internet ~ ---->  服务端程序

> 客户端程序直连服务端程序

**经过RPM**
[ 客户端程序 ---->  RPM-client ] ---->  ~  internet ~  ---->  [ RPM-server  ----> 服务端程序 ]

> 客户端程序认为 RPM-client 是服务端  
> 服务端程序认为 RPM-server 是客户端


### 反向端口映射
用于将局域网电脑的端口映射到公网主机上


**常规**
在有公网IP的服务器上启动程序


**经过RPM**

内网服务（内网端口） <----- RPM-client <------>  ~  internet ~  <------>  RPM-server（公网端口）

在内网启动服务，将内网的服务端口通过RPM-server映射到公网上，并对外提供服务

此时RPM客户端 与 RPM服务端将保持长连接



### 客户端
内网机器部署  
主动发起请求连接服务端  

#### 配置
服务器的IP  
服务器的端口  

认证

正向映射配置客户端



### 服务端
有公网IP的机器部署  
开发端口，接受客户端的连接  

#### 配置
绑定的网口  
绑定的端口  

认证


反向映射配置服务端

配置监听端口

如果有多个客户端的情况下，反向映射时应该到哪一个客户端，应该加一个id标识










