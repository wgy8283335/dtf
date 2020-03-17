#!/usr/bin/env bash
nohup java -jar ../../dtf-manager/target/dtf-manager-0.1-jar-with-dependencies.jar >dtf-manager.out &
sleep 10
nohup java -jar ../spring-demo/target/spring-demo-0.1.jar >spring-demo.out &
sleep 10
nohup java -jar ../spring-demo-2/target/spring-demo-2-0.1.jar >spring-demo-2.out &
sleep 10
nohup java -jar ../spring-demo-3/target/spring-demo-3-0.1.jar >spring-demo-3.out &
sleep 10