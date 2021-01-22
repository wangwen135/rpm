#!/bin/sh
#
# linux 下的关闭脚本
#

#进入脚本目录
cd `dirname $0`

java -cp ../config:../lib/* com.wwh.rpm.client.Shutdown
