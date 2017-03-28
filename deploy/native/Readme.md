# omega-framework 裸机环境安装
没有docker/dns环境时，安装omega-framework及其依赖。


##  初始化所有环境

所有机器列表在hosts/all目录下，通过人肉分配角色。

```
10.186.124.41    eureka1
10.186.124.55    eureka2
10.186.124.72    zookeeper1
10.186.124.80    configserver1
10.186.124.82    rabbitmq
10.186.124.105   mycat1
10.186.124.108   mycat2
10.186.124.113
10.186.124.115
10.186.124.125   configserver2
10.186.124.129
10.186.124.203
10.186.124.206
10.186.124.229
10.186.124.230
10.186.124.232
10.186.124.245

```

在prepare.sh中会初始化hosts/all中的所有机器，安装jdk以及常用软件包，并实现免登录和配置/etc/hosts。

注意prepare.sh只能只能执行一次。

## 安装zookeeper

为hosts/zookeeper中的zk机器安装zk，并启动zk服务。

测试环境只准备一台zk。


## 安装rabbitmq

为hosts/rabbitmq中的rabbitmq机器安装rabbitmq，并初始化用户/vhost分配权限，以及初始化queue／exchange.

## 发布eureka

测试环境只启动一个eureka。

## 发布configserver

测试环境只启动一个configserver。