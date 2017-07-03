本文档描述在没有docker/dns的环境下如何安装omega-framework及其依赖。

## 选择一台机器作为发布机

以root用户执行如下命令初始化发布机。
```
yum install -y  pssh curl telnet
ln -s /usr/bin/pscp.pssh /usr/bin/pscp

useradd admin
passwd xxx
su - admin
if [[ ! -f ~/.ssh/id_rsa.pub ]]; then
    ssh-keygen -t rsa
fi
```

请记住发布机admin用户密码，后续流程需要以admin用户登录发布机操作。

## omega-framework安装

将omega-framework的release包在发布机解压开。
```
su - admin
tar zxvf omega-framework-assembly-0.1-bin.tar.gz
cd omega-framework-assembly-0.1/deploy/native
```

##  初始化所有机器

人工在hosts/all中分配角色。
```
10.186.124.41    eureka1
10.186.124.55    eureka2
10.186.124.72    eureka3
10.186.124.80    configserver1
10.186.124.82    configserver2
10.186.124.105   rabbitmq
10.186.124.108   zookeeper1
10.186.124.113   turbine
10.186.124.115   zipkin
10.186.124.125   monitor
10.186.124.129   mycat1
10.186.124.203   mycat2
10.186.124.206
10.186.124.229
10.186.124.230
10.186.124.232
10.186.124.245
```

然后在native目录下执行
```
sh -x prepare.sh
```
初始化hosts/all中列举的所有机器，配置/etc/hosts，安装jdk以及常用软件包，并实现发布机到所有机器的免登录。

注意prepare.sh只能执行一次。

## 安装zookeeper

在native目录下执行：
```
sh -x install-zookeeper.sh
```
安装zookeeper并启动服务。

## 安装rabbitmq

在native目录下执行
```
sh -x install-rabbitmq.sh
```
安装rabbitmq服务，并初始化用户及vhost，分配权限，以及初始化queue与exchange.

## 发布eureka

在native目录下执行：
```
sh -x pub-eureka.sh
```
发布eureka集群服务。

## 发布configserver

SVN上为每个环境（测试、生产）准备好一个目录，内部存放各个模块的配置文件，命名规则：模块-${profile}.yml，例如：
```
omega-demo-service-test.yml
```
其中test是profile名称。

在native目录下执行：
```
sh -x pub-configserver.sh test
```
发布configserver集群服务，并通知所有微服务（refresh）。

## 发布其它omega-framework模块

创建hosts/omega-framework-taskserver文件，记录下所有omega-framework-taskserver机器的IP地址，一个IP占一行。

在native目录下执行：
```
sh -x pub-omega.sh omega-framework-taskserver 0.1
```
发布omega-framework-taskserver服务。

## 发布业务模块

以omega-demo-service模块为例。

将程序文件omega-demo-service-0.1.jar放置在lib-repo目录下；

创建hosts/omega-demo-service文件，记录下所有omega-demo-service机器的IP地址，一个IP占一行。

在native目录下执行：
```
sh -x pub-service.sh omega-demo-service 0.1
```
发布omega-demo-service模块。
