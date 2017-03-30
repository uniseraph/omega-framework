# omega-framework 裸机环境安装
没有docker/dns环境时，安装omega-framework及其依赖。

拿到omega-framework的release包，在发布机解压开。建议使用configserver作为发布机


```
tar zxvf omega-framework-assembly-0.1-bin.tar.gz -C /opt
```

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


## 选择一台机器作为发布机，建议使用rabbitmq作为发布机

执行如下命令初始化发布机

```
yum install -y  pssh curl telnet
ln -s /usr/bin/pscp.pssh /usr/bin/pscp

useradd admin
passwd admin
su - admin
if [[ ! -f ~/.ssh/id_rsa.pub ]]; then
    ssh-keygen -t rsa
fi


```
请记住发布机admin用户代码，后续需要登录发布机。


## 拷贝omega-framework的release包到发布机/home/admin目录

```
scp omega-framework-assembly/target/omega-framework-assembly-0.1-bin.tar.gz root@47.92.30.250:/home/admin
```


```

在native目录下执行
```
sh -x prepare.sh
```
初始化hosts/all中的所有机器，安装jdk以及常用软件包，并实现免登录和配置/etc/hosts。





注意prepare.sh只能执行一次。

## 安装zookeeper

在hosts/zookeeper中记录zookeeper的地址，测试环境只需要一台。

为hosts/zookeeper中的zk机器安装zk，并启动zk服务。


在native目录下执行
```
sh -x install-zookeeper.sh
```

安装zookeeper并启动服务。


## 安装rabbitmq

为hosts/rabbitmq中的rabbitmq机器安装rabbitmq，并初始化用户/vhost分配权限，以及初始化queue／exchange.

hosts/rabbitmq记录了rabbitmq的地址。

在native目录下执行
```
sh -x install-rabbitmq.sh
```


## 发布eureka

在hosts/eureka中记录所有eureka服务器，
```
10.186.124.41
10.186.124.55
```

在hosts/eureka1中记录eureka1
```
10.186.124.41
```


测试环境只启动一个eureka,注意在生产环境eureka必须轮转发布，不能并行。

```
sh -x pub-eureka.sh
```

## 发布configserver

在hosts/configserver中记录所有configserver
```
10.186.124.80

```

测试环境只启动一个configserver。

```
sh -x pub-configserver.sh
```
configserver可以并行发布。


## 发布其它omega-framework模块

在configserver的/root/config-repo目录下准备好配置文件

发布omega-framework-taskserver
```
sh -x pub-omega.sh omega-framework-taskserver
```
