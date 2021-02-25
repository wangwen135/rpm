@echo off
REM 
REM Windows下的启动脚本
REM 
REM 支持java参数，如下：
REM startupSocksServer.bat -Dport=11080

java -Xmx1024m -cp ..\config;..\lib\* %* com.wwh.rpm.tools.proxy.SocksServer
