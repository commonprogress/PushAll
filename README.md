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

#### build.gradle 配置
```
/*配置账号相关*/
        manifestPlaceholders = [
                JPUSH_PKGNAME   : "com.dongxl.push",//值来自开发者平台取得的AppKey
                JPUSH_APPKEY    : "46b514bd1b982739ec4d2f6f",//值来自开发者平台取得的AppKey
                JPUSH_CHANNEL   : "default_developer",
                HUAWEIPUSH_APPID: "101001991", //
                VIVOPUSH_APPKEY : "101001991",
                VIVOPUSH_APPID  : "101001991"
        ]
        buildConfigField "String", "MAIN_PACKAGENAME", "\"com.dongxl.push\""
        buildConfigField "String", "PUSHRECEIVESERVICE", "\"com.dongxl.push.service.PushReceiveService\""

        buildConfigField 'String', 'XIAOMIPUSH_APPID', "\"2882303761517172047\""
        buildConfigField 'String', 'XIAOMIPUSH_APPKEY', "\"5331717244047\""
        buildConfigField "String", "OPPOPUSH_APPID", "\"bt2M9eaEu4jZZoYqqYVT6e3X\""
        buildConfigField "String", "OPPOPUSH_APPKEY", "\"67ZtaSY1EyjZZoYqqYVT6e3X\""
        buildConfigField "String", "OPPOPUSH_APPSECRET", "\"sK8dkfTwHt11QezBuIO4kjJV\""
        buildConfigField "String", "MEIZUPUSH_APPKEY", "\"67ZtaSY1EyjZZoYqqYVT6e3X\""
        buildConfigField "String", "MEIZUPUSH_APPID", "\"sK8dkfTwHt11QezBuIO4kjJV\""

```

********************************************************************************************************
#### v1.1.0

1. 账号配置外迁，让你的配置更简单

#### v1.0.0

1. 集成各大厂家推送

********************************************************************************************************

此项目持续维护中，如有问题 请加QQ：254547297
![效果图1](img/C80925D365ADDABBC60EF71DE1C5B152.jpg)





push_config.gradle 配置各大推送平台key
