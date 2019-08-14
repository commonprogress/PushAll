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
			maven { url 'http://developer.huawei.com/repo/' }
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
PushReceiveService 自己实现的推送接收Service 推送注册结果就收类，还有推送事件到达类
```

#### push_config.gradle 配置各大推送平台key
```
/*配置账号相关*/
        ext {
            main_packagename = "com.dongxl.push" //程序主包名，选填
            pushreceiveservice = "com.dongxl.push.service.PushReceiveService" //自己实现的推送接收Service 
            
        //极光
            jpush_appkey = "46b514bd1b982739ec4d2f6f" //极光key 
            jpush_channel = "default_developer" //极光渠道 默认default_developer
        
        //华为
            huaweipush_appid = "101001991"
        
        //vivo
            vivopush_appkey = "6ab44aea-3c2a-4dd9-94c7-e0e5d0cb4d26"
            vivopush_appid = "14449"
        
        //小米
            xiaomipush_appid = "2882303761517172047"
            xiaomipush_appkey = "5331717244047"
        
        //oppo
            oppopush_appid = "bt2M9eaEu4jZZoYqqYVT6e3X"
            oppopush_appkey = "bt2M9eaEu4jZZoYqqYVT6e3X"
            oppopush_appsecret = "bt2M9eaEu4jZZoYqqYVT6e3X"
        
        //魅族
            meizupush_appkey = "ae5adce01f6c4fbe9b39d9f7ae3fc1de"
            meizupush_appid = "123037"
        }

```

#### 各大推送平台服务端简单demo
https://github.com/lingduzuobiao123/PushSenderSample 

********************************************************************************************************
#### v1.2.1

1. 此项目引用Gradle的配置

#### v1.1.0

1. 账号配置外迁，让你的配置更简单

#### v1.0.0

1. 集成各大厂家推送


#### 混淆注意事项：

```
-dontoptimize
-dontpreverify
-ignorewarning
-keepattributes *Annotation*
-keepattributes Exceptions
-keepattributes InnerClasses
-keepattributes Signature
-keepattributes SourceFile,LineNumberTable

#=================  push  =================
-dontwarn com.dongxl.pushdeme.**
-keep class com.dongxl.pushdeme.** { *; }

#====极光====
-dontwarn cn.jpush.**
-keep class cn.jpush.** { *; }
-keep class * extends cn.jpush.android.helpers.JPushMessageReceiver { *; }
-dontwarn cn.jiguang.**
-keep class cn.jiguang.** { *; }

#=================  小米push  =================
-keepclasseswithmembernames class com.xiaomi.**{*;}
-keep public class * extends com.xiaomi.mipush.sdk.PushMessageReceiver
-dontwarn com.xiaomi.push.service.a.a

#=================  华为push  =================
-keep class com.hianalytics.android.**{*;}
-keep class com.huawei.updatesdk.**{*;}
-keep class com.huawei.hms.**{*;}
-keep class com.huawei.android.hms.agent.**{*;}

#=================  vivo push  =================
-dontwarn com.vivo.push.**
-keep class com.vivo.push.**{*; }
-keep class com.vivo.vms.**{*; }
-keep class com.jsy.push.vivo.VivoPushReceiver{*;}

#=================  oppo push  =================
-keep public class * extends android.app.Service

#=================  meizu push  =================
-dontwarn com.meizu.cloud.pushsdk.**
-keep class com.meizu.cloud.pushsdk.**{*;}

```

********************************************************************************************************

此项目持续维护中，如有问题 请加QQ：254547297
![效果图1](img/C80925D365ADDABBC60EF71DE1C5B152.jpg)
