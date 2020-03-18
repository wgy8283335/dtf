#!/usr/bin/env bash
tmp=`ps -ef | grep dtf-manager-0.1| grep -v grep | awk '{print $2}'`
echo ${tmp}
kill -9 ${tmp}

tmp=`ps -ef | grep spring-demo-0.1| grep -v grep | awk '{print $2}'`
echo ${tmp}
kill -9 ${tmp}

tmp=`ps -ef | grep spring-demo-2-0.1| grep -v grep | awk '{print $2}'`
echo ${tmp}
kill -9 ${tmp}

tmp=`ps -ef | grep spring-demo-3-0.1| grep -v grep | awk '{print $2}'`
echo ${tmp}
kill -9 ${tmp}