###  Pushdemo 让你的推送集成更简洁，快速。

本项目集成了小米 oppo vivo 华为 魅族五大厂商，以及极光推送。

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




