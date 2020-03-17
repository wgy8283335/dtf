#!/usr/bin/env bash
sleep 10
nohup java -jar ../spring-demo/target/spring-demo-0.1.jar &
sleep 10
nohup java -jar ../spring-demo-2/target/spring-demo-2-0.1.jar &
sleep 10
nohup java -jar ../spring-demo-3/target/spring-demo-3-0.1.jar &
sleep 10