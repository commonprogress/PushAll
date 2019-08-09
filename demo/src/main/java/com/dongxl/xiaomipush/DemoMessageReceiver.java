package com.dongxl.xiaomipush;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.example.jpushdemo.ExampleApplication;
import com.example.jpushdemo.R;
import com.xiaomi.mipush.sdk.ErrorCode;
import com.xiaomi.mipush.sdk.MiPushClient;
import com.xiaomi.mipush.sdk.MiPushCommandMessage;
import com.xiaomi.mipush.sdk.MiPushMessage;
import com.xiaomi.mipush.sdk.PushMessageReceiver;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 1、PushMessageReceiver 是个抽象类，该类继承了 BroadcastReceiver。<br/>
 * 2、需要将自定义的 DemoMessageReceiver 注册在 AndroidManifest.xml 文件中：
 * <pre>
 * {@code
 *  <receiver
 *      android:name="com.dongxl.xiaomipush.DemoMessageReceiver"
 *      android:exported="true">
 *      <intent-filter>
 *          <action android:name="com.xiaomi.mipush.RECEIVE_MESSAGE" />
 *      </intent-filter>
 *      <intent-filter>
 *          <action android:name="com.xiaomi.mipush.MESSAGE_ARRIVED" />
 *      </intent-filter>
 *      <intent-filter>
 *          <action android:name="com.xiaomi.mipush.ERROR" />
 *      </intent-filter>
 *  </receiver>
 *  }</pre>
 * 3、DemoMessageReceiver 的 onReceivePassThroughMessage 方法用来接收服务器向客户端发送的透传消息。<br/>
 * 4、DemoMessageReceiver 的 onNotificationMessageClicked 方法用来接收服务器向客户端发送的通知消息，
 * 这个回调方法会在用户手动点击通知后触发。<br/>
 * 5、DemoMessageReceiver 的 onNotificationMessageArrived 方法用来接收服务器向客户端发送的通知消息，
 * 这个回调方法是在通知消息到达客户端时触发。另外应用在前台时不弹出通知的通知消息到达客户端也会触发这个回调函数。<br/>
 * 6、DemoMessageReceiver 的 onCommandResult 方法用来接收客户端向服务器发送命令后的响应结果。<br/>
 * 7、DemoMessageReceiver 的 onReceiveRegisterResult 方法用来接收客户端向服务器发送注册命令后的响应结果。<br/>
 * 8、以上这些方法运行在非 UI 线程中。
 */
public class DemoMessageReceiver extends PushMessageReceiver {
    /**
     * RegID是由服务器端生成的，能够唯一标识某台手机上的某个应用。
     * 应用在获取到RegID后，需要将RegID上报到应用的服务器，
     * 此时应用服务端就可以利用RegID向客户端发送消息
     */
    private String mRegId;
    private String mMessage;
    /**
     * 开发者可以结合自己的业务特征，给用户打上不同的标签(Topic)。
     * 在消息的推送过程中，开发者结合每条消息的内容和目标用户群,选择每条消息所对应的标签,可以进行更精准的定向推送。
     * 注：一台设备可以订阅多个不同的标签，并且一个标签可以对应多台设备，这点和别名不同
     */
    private String mTopic;
    /**
     * 利用别名向设备推送消息的方法不需要应用服务器保存客户端上传的RegID。
     * 开发者可以根据业务的需要给不同设备设置不同的别名。
     * 注：一台设备可以设置多个不同的别名，而一个别名只能对应某一台设备。如果多台设备设置同一个别名，
     * 那么只有最后设置的一台设备生效。
     */
    private String mAlias;
    /**
     * 开发者可以在不同设备上设置同一个userAccount。然后使用Server SDK给该userAccount发送消息；
     * 此时，所有设置了该userAccount的设备都可以收到消息
     */
    private String mUserAccount;
    private String mStartTime;
    private String mEndTime;

    /**
     * 透传消息——— 封装消息的MiPushMessage对象直接通过PushMessageReceiver继承类的的onReceivePassThroghMessage方法传到客户端
     *
     * @param context
     * @param message
     */
    @Override
    public void onReceivePassThroughMessage(Context context, MiPushMessage message) {
        Log.v(ExampleApplication.XIAOMI_TAG,
                "onReceivePassThroughMessage is called. " + message.toString());
        String log = context.getString(R.string.recv_passthrough_message, message.getContent());
        XiaomiMainActivity.logList.add(0, getSimpleDate() + " " + log);

        mMessage = message.getContent();
        if (!TextUtils.isEmpty(message.getTopic())) {
            mTopic = message.getTopic();
        } else if (!TextUtils.isEmpty(message.getAlias())) {
            mAlias = message.getAlias();
        } else if (!TextUtils.isEmpty(message.getUserAccount())) {
            mUserAccount = message.getUserAccount();
        }

        Message msg = Message.obtain();
        msg.obj = log;
        ExampleApplication.getHandler().sendMessage(msg);
    }

    /**
     * 注：通知消息通过onNotificationMessageClicked传到客户端只对"自定义点击行为"有效。
     * 注：在MIUI上，如果没有收到onNotificationMessageArrived回调，是因为使用的MIUI版本还不支持该特性，
     * 需要升级到MIUI7之后。非MIUI手机都可以收到这个回调
     *
     * @param context
     * @param message
     */
    @Override
    public void onNotificationMessageClicked(Context context, MiPushMessage message) {

        Log.v(ExampleApplication.XIAOMI_TAG,
                "onNotificationMessageClicked is called. " + message.toString());
        String log = context.getString(R.string.click_notification_message, message.getContent());
        XiaomiMainActivity.logList.add(0, getSimpleDate() + " " + log);

        mMessage = message.getContent();
        if (!TextUtils.isEmpty(message.getTopic())) {
            mTopic = message.getTopic();
        } else if (!TextUtils.isEmpty(message.getAlias())) {
            mAlias = message.getAlias();
        } else if (!TextUtils.isEmpty(message.getUserAccount())) {
            mUserAccount = message.getUserAccount();
        }

        Message msg = Message.obtain();
        if (message.isNotified()) {
            msg.obj = log;
        }
        ExampleApplication.getHandler().sendMessage(msg);
    }

    /**
     * 通知消息 ———消息到达客户端后会弹出通知，通知消息到达时，
     * 不需要用户点击通知就会通过PushMessageReceiver继承类的onNotificationMessageArrived方法传到客户端，
     * 只有在用户点击了通知后封装消息的MiPushMessage对象才会通过PushMessageReceiver继承类的onNotificationMessageClicked方法传到客户端。
     * 对于应用在前台时不弹通知类型的通知信息，通知到达后会通过PushMessageReceiver继承类的onNotificationMessageArrived方法传到客户端，
     * 但不会触发onNotificationMessageClicked方法
     *
     * @param context
     * @param message
     */
    @Override
    public void onNotificationMessageArrived(Context context, MiPushMessage message) {
        Log.v(ExampleApplication.XIAOMI_TAG,
                "onNotificationMessageArrived is called. " + message.toString());
        String log = context.getString(R.string.arrive_notification_message, message.getContent());
        XiaomiMainActivity.logList.add(0, getSimpleDate() + " " + log);

        mMessage = message.getContent();
        if (!TextUtils.isEmpty(message.getTopic())) {
            mTopic = message.getTopic();
        } else if (!TextUtils.isEmpty(message.getAlias())) {
            mAlias = message.getAlias();
        } else if (!TextUtils.isEmpty(message.getUserAccount())) {
            mUserAccount = message.getUserAccount();
        }

        Message msg = Message.obtain();
        msg.obj = log;
        ExampleApplication.getHandler().sendMessage(msg);
    }

    @Override
    public void onCommandResult(Context context, MiPushCommandMessage message) {
        Log.v(ExampleApplication.XIAOMI_TAG,
                "onCommandResult is called. " + message.toString());
        String command = message.getCommand();
        List<String> arguments = message.getCommandArguments();
        String cmdArg1 = ((arguments != null && arguments.size() > 0) ? arguments.get(0) : null);
        String cmdArg2 = ((arguments != null && arguments.size() > 1) ? arguments.get(1) : null);
        String log;
        if (MiPushClient.COMMAND_REGISTER.equals(command)) {
            //注册的结果
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                //RegID：针对单一设备推送消息。应用调用MiPushClient类的静态方法registerPush注册小米推送服务，
                // 注册的结果将通过PushMessageReceiver继承类的onCommandResult方法和onReceiveRegisterResult中的MiPushCommandMessage参数对象message传到客户端。
                // 当message对象的command等于MiPushClient.COMMAND_REGISTER并且message对象的resultCode等于ErrorCode.SUCCESS时，
                // message对象commandArguments包含了服务器返回的RegID
                mRegId = cmdArg1;
                log = context.getString(R.string.register_success);
            } else {
                log = context.getString(R.string.register_fail);
            }
        } else if (MiPushClient.COMMAND_UNREGISTER.equals(command)) {
            //注销的结果
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mRegId = cmdArg1;
                log = context.getString(R.string.register_success);
            } else {
                log = context.getString(R.string.register_fail);
            }
        } else if (MiPushClient.COMMAND_SET_ALIAS.equals(command)) {
            //设置别名的结果
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                //别名：针对单一设备推送消息。应用只有在成功注册小米推送服务后才能调用MiPushClient类的静态方法setAlias设置别名。
                // 同样，设置别名的结果将通过PushMessageReceiver继承类的onCommandResult方法中的MiPushCommandMessage参数对象message传到客户端
                // 成功设置别名后，服务器就可以通过这个别名将消息推送到对应的设备上
                mAlias = cmdArg1;
                log = context.getString(R.string.set_alias_success, mAlias);
            } else {
                log = context.getString(R.string.set_alias_fail, message.getReason());
            }
        } else if (MiPushClient.COMMAND_UNSET_ALIAS.equals(command)) {
            //撤销别名
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mAlias = cmdArg1;
                log = context.getString(R.string.unset_alias_success, mAlias);
            } else {
                log = context.getString(R.string.unset_alias_fail, message.getReason());
            }
        } else if (MiPushClient.COMMAND_SET_ACCOUNT.equals(command)) {
            //设置帐号
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                //userAccount:针对多个设备推送消息。应用只有在成功注册小米推送服务后才能调用MiPushClient类的静态方法setUserAccount设置userAccount。
                // 同样，设置userAccount的结果将通过PushMessageReceiver继承类的onCommandResult方法中的MiPushCommandMessage参数对象message传到客户端
                mUserAccount = cmdArg1;
                log = context.getString(R.string.set_account_success, mUserAccount);
            } else {
                log = context.getString(R.string.set_account_fail, message.getReason());
            }
        } else if (MiPushClient.COMMAND_UNSET_ACCOUNT.equals(command)) {
            //撤销账号
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mUserAccount = cmdArg1;
                log = context.getString(R.string.unset_account_success, mUserAccount);
            } else {
                log = context.getString(R.string.unset_account_fail, message.getReason());
            }
        } else if (MiPushClient.COMMAND_SUBSCRIBE_TOPIC.equals(command)) {
            //设置标签
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                //标签：针对多个设备推送消息。应用只有成功注册小米推送服务后才能调用MiPushClient类的静态方法subscribe订阅标签。
                // 同样，订阅标签的结果将通过PushMessageReceiver继承类的onCommandResult方法中的MiPushCommandMessage参数对象message传到客户端。
                mTopic = cmdArg1;
                log = context.getString(R.string.subscribe_topic_success, mTopic);
            } else {
                log = context.getString(R.string.subscribe_topic_fail, message.getReason());
            }
        } else if (MiPushClient.COMMAND_UNSUBSCRIBE_TOPIC.equals(command)) {
            //撤销标签
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mTopic = cmdArg1;
                log = context.getString(R.string.unsubscribe_topic_success, mTopic);
            } else {
                log = context.getString(R.string.unsubscribe_topic_fail, message.getReason());
            }
        } else if (MiPushClient.COMMAND_SET_ACCEPT_TIME.equals(command)) {
            //设置接收消息时间
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                mStartTime = cmdArg1;
                mEndTime = cmdArg2;
                log = context.getString(R.string.set_accept_time_success, mStartTime, mEndTime);
            } else {
                log = context.getString(R.string.set_accept_time_fail, message.getReason());
            }
        } else {
            log = message.getReason();
        }

        XiaomiMainActivity.logList.add(0, getSimpleDate() + "    " + log);

        Message msg = Message.obtain();
        msg.obj = log;
        ExampleApplication.getHandler().sendMessage(msg);
    }

    @Override
    public void onReceiveRegisterResult(Context context, MiPushCommandMessage message) {
        Log.v(ExampleApplication.XIAOMI_TAG,
                "onReceiveRegisterResult is called. " + message.toString());
        String command = message.getCommand();
        List<String> arguments = message.getCommandArguments();
        String cmdArg1 = ((arguments != null && arguments.size() > 0) ? arguments.get(0) : null);
        String cmdArg2 = ((arguments != null && arguments.size() > 1) ? arguments.get(1) : null);
        String log;
        if (MiPushClient.COMMAND_REGISTER.equals(command)) {
            if (message.getResultCode() == ErrorCode.SUCCESS) {
                //RegID：针对单一设备推送消息。应用调用MiPushClient类的静态方法registerPush注册小米推送服务，
                // 注册的结果将通过PushMessageReceiver继承类的onCommandResult方法和onReceiveRegisterResult中的MiPushCommandMessage参数对象message传到客户端。
                // 当message对象的command等于MiPushClient.COMMAND_REGISTER并且message对象的resultCode等于ErrorCode.SUCCESS时，
                // message对象commandArguments包含了服务器返回的RegID
                mRegId = cmdArg1;
                log = context.getString(R.string.register_success);
            } else {
                log = context.getString(R.string.register_fail);
            }
        } else {
            log = message.getReason();
        }


        Message msg = Message.obtain();
        msg.obj = log;
        ExampleApplication.getHandler().sendMessage(msg);
    }

    @Override
    public void onRequirePermissions(Context context, String[] permissions) {
        super.onRequirePermissions(context, permissions);
        Log.e(ExampleApplication.XIAOMI_TAG,
                "onRequirePermissions is called. need permission" + arrayToString(permissions));

        if (Build.VERSION.SDK_INT >= 23 && context.getApplicationInfo().targetSdkVersion >= 23) {
            Intent intent = new Intent();
            intent.putExtra("permissions", permissions);
            intent.setComponent(new ComponentName(context.getPackageName(), PermissionActivity.class.getCanonicalName()));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            context.startActivity(intent);
        }
    }

    @SuppressLint("SimpleDateFormat")
    private static String getSimpleDate() {
        return new SimpleDateFormat("MM-dd hh:mm:ss").format(new Date());
    }

    public String arrayToString(String[] strings) {
        String result = " ";
        for (String str : strings) {
            result = result + str + " ";
        }
        return result;
    }
}
