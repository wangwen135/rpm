#!/bin/sh
#
# linux 下的启动脚本
#
# 支持java参数，如下：
# ./startupSocksServer.sh -Dport=11080

#

#进入脚本目录
cd `dirname $0`


## 启动程序
## 参数支持
## 设置了最大内存为1G

echo '*********************************************************'
echo '** useage:'
echo '** ./startupSocksServer.sh -Dhost=0.0.0.0 -Dport=11080'
echo '**'
echo '*********************************************************'
echo 
echo Start Socks Server ...
echo
nohup java -Xmx1024m -cp ../config:../lib/* $@ com.wwh.rpm.tools.proxy.SocksServer >socksServer.out 2>&1 &