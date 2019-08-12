###  Pushdemo 让你的推送集成更简洁，快速。

本项目集成了小米 oppo vivo 华为 魅族五大厂商，以及极光推送。

************************************************ 集成 ********************************************************

集成方式：

### Gradle

```
 	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}

```

```
	dependencies {
	        implementation 'com.github.commonprogress:PushAll:Tag'
	}

```
### Maven

```
	<repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
	</repositories>

```

```
	<dependency>
	    <groupId>com.github.commonprogress</groupId>
	    <artifactId>PushAll</artifactId>
	    <version>Tag</version>
	</dependency>

```

代码中需要实现的步骤：

```
PushRegisterSet 推送注册操作类
PushRegisterSet.applicationInit()初始化
PushRegisterSet.registerInitPush()注册

```

```
PushRegisterSet.registerInitPush()注册,可以放在application也可以放在app第一个界面中
PushReceiveService 推送注册结果就收类，还有推送事件到达类
```

#### push_config.gradle 配置
```
/*配置账号相关*/
    main_packagename = "\"com.dongxl.push\""
    pushreceiveservice = "\"com.dongxl.push.service.PushReceiveService\""

    jpush_pkgname = "com.dongxl.push"
    jpush_appkey = "46b514bd1b982739ec4d2f6f"
    jpush_channel = "default_developer"

    huaweipush_appid = "101001991"

    vivopush_appkey = "101001991"
    vivopush_appid = "101001991"

    xiaomipush_appid = "\"2882303761517172047\""
    xiaomipush_appkey = "\"5331717244047\""

    oppopush_appid = "\"bt2M9eaEu4jZZoYqqYVT6e3X\""
    oppopush_appkey = "\"bt2M9eaEu4jZZoYqqYVT6e3X\""
    oppopush_appsecret = "\"bt2M9eaEu4jZZoYqqYVT6e3X\""

    meizupush_appkey = "\"bt2M9eaEu4jZZoYqqYVT6e3X\""
    meizupush_appid = "\"bt2M9eaEu4jZZoYqqYVT6e3X\""

```

********************************************************************************************************
#### v1.2.1

1. 此项目引用Gradle的配置

#### v1.1.0

1. 账号配置外迁，让你的配置更简单

#### v1.0.0

1. 集成各大厂家推送

********************************************************************************************************

此项目持续维护中，如有问题 请加QQ：254547297
![效果图1](img/C80925D365ADDABBC60EF71DE1C5B152.jpg)





push_config.gradle 配置各大推送平台key
