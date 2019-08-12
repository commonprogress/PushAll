package com.dongxl.oppo.component;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.coloros.mcssdk.PushManager;
import com.coloros.mcssdk.callback.PushAdapter;
import com.coloros.mcssdk.callback.PushCallback;
import com.coloros.mcssdk.mode.SubscribeResult;
import com.dongxl.oppo.component.fragment.BaseFragment;
import com.dongxl.oppo.component.fragment.PushConfigFragment;
import com.dongxl.oppo.component.fragment.PushInfoFragment;
import com.dongxl.oppo.component.fragment.UserBandingFragment;
import com.dongxl.oppo.constants.AppParam;
import com.dongxl.oppo.util.LogUtil;
import com.dongxl.oppo.util.TestModeUtil;
import com.example.jpushdemo.R;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.dongxl.oppo.util.TestModeUtil.TYPE_LOG;

public class OppoDemoActivity extends AppCompatActivity implements TestModeUtil.UpdateTestMode {

    /**
     * 显示的log格式
     */
    private static final String LOG_FORMAT = "time ->[tag] msg";
    private static final String LOG_FORMAT_NO_TAG = "time -> msg";
    private static final int MAX_LOG_SIZE = 3000;
    /**
     * log中的时间格式
     */
    private static SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:SS", Locale.CHINA);
    /**
     * log信息显示到界面上，方便调试
     */
    private TextView logInfoTextView;
    private ScrollView mScrollView;
    /**
     * 用户操作和反馈信息，显示到界面上方便调试
     */
    private List<String> logs = new CopyOnWriteArrayList<>();
    private BaseFragment userBandingFragment;
    private BaseFragment pushConfigFragment;
    private BaseFragment[] fragments;
    private int lastShowFragment = 0;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switchFragment(lastShowFragment, lastShowFragment = item.getOrder());
            return true;
        }
    };

    /************************************************************************************
     * ***************************callbacks from mcs************************************
     ***********************************************************************************/
    private PushCallback mPushCallback = new PushAdapter() {
        @Override
        public void onRegister(int code, String s) {
            if (code == 0) {
                showResult("注册成功", "registerId:" + s);
            } else {
                showResult("注册失败", "code=" + code + ",msg=" + s);
            }
        }

        @Override
        public void onUnRegister(int code) {
            if (code == 0) {
                showResult("注销成功", "code=" + code);
            } else {
                showResult("注销失败", "code=" + code);
            }
        }

        @Override
        public void onGetAliases(int code, List<SubscribeResult> list) {
            if (code == 0) {
                showResult("获取别名成功", "code=" + code + ",msg=" + Arrays.toString(list.toArray()));
            } else {
                showResult("获取别名失败", "code=" + code);
            }
        }

        @Override
        public void onSetAliases(int code, List<SubscribeResult> list) {
            if (code == 0) {
                showResult("设置别名成功", "code=" + code + ",msg=" + Arrays.toString(list.toArray()));
            } else {
                showResult("设置别名失败", "code=" + code);
            }
        }

        @Override
        public void onUnsetAliases(int code, List<SubscribeResult> list) {
            if (code == 0) {
                showResult("取消别名成功", "code=" + code + ",msg=" + Arrays.toString(list.toArray()));
            } else {
                showResult("取消别名失败", "code=" + code);
            }
        }

        @Override
        public void onSetTags(int code, List<SubscribeResult> list) {
            if (code == 0) {
                showResult("设置标签成功", "code=" + code + ",msg=" + Arrays.toString(list.toArray()));
            } else {
                showResult("设置标签失败", "code=" + code);
            }
        }

        @Override
        public void onUnsetTags(int code, List<SubscribeResult> list) {
            if (code == 0) {
                showResult("取消标签成功", "code=" + code + ",msg=" + Arrays.toString(list.toArray()));
            } else {
                showResult("取消标签失败", "code=" + code);
            }
        }

        @Override
        public void onGetTags(int code, List<SubscribeResult> list) {
            if (code == 0) {
                showResult("获取标签成功", "code=" + code + ",msg=" + Arrays.toString(list.toArray()));
            } else {
                showResult("获取标签失败", "code=" + code);
            }
        }


        @Override
        public void onGetPushStatus(final int code, int status) {
            if (code == 0 && status == 0) {
                showResult("Push状态正常", "code=" + code + ",status=" + status);
            } else {
                showResult("Push状态错误", "code=" + code + ",status=" + status);
            }
        }

        @Override
        public void onGetNotificationStatus(final int code, final int status) {
            if (code == 0 && status == 0) {
                showResult("通知状态正常", "code=" + code + ",status=" + status);
            } else {
                showResult("通知状态错误", "code=" + code + ",status=" + status);
            }
        }

        @Override
        public void onSetPushTime(final int code, final String s) {
            showResult("SetPushTime", "code=" + code + ",result:" + s);
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oppo_main);
        //顶部的appid等应用信息
        TextView appInfo = (TextView) findViewById(R.id.tv_app_info);
        String info = "AppId :\t" + AppParam.appId + "\n" + "AppKey :\t" + AppParam.appKey;
        appInfo.setText(info);
        logInfoTextView = (TextView) findViewById(R.id.tv_log_msg);
        mScrollView = (ScrollView) findViewById(R.id.scrollView);
        //底部tab切换控件使用了BottomNavigationView
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        //初始化fragment
        BaseFragment baseInfoFragment = new PushInfoFragment();
        userBandingFragment = new UserBandingFragment();
        pushConfigFragment = new PushConfigFragment();
        fragments = new BaseFragment[]{baseInfoFragment, userBandingFragment, pushConfigFragment};
        getSupportFragmentManager().beginTransaction().add(R.id.content, baseInfoFragment).show(baseInfoFragment).commit();
        initData();
    }

    private void initData() {
        //初始化push，调用注册接口
        addLog("AppId", AppParam.appId);
        addLog("appKey", AppParam.appKey);
        addLog("appSecret", AppParam.appSecret);
        addLog("pkgName", getPackageName());
        addLog("", "");
        TestModeUtil.setUpdateTestMode(this);
        onLogUpdate(TYPE_LOG);
        onLogUpdate(TestModeUtil.TYPE_STATUS);
        try {
            addLog("初始化注册", "调用register接口");
            PushManager.getInstance().register(this, AppParam.appKey, AppParam.appSecret, mPushCallback);//setPushCallback接口也可设置callback
        } catch (Exception e) {
            e.printStackTrace();
            TestModeUtil.addLogString(e.getLocalizedMessage());
        }
    }

    @Override
    public void onLogUpdate(int type) {
        if (type == TYPE_LOG) {
            logInfoTextView.post(new Runnable() {
                @Override
                public void run() {
                    String info = TestModeUtil.getLastLog();
                    if (info == null) return;
                    while (info != null) {
                        logInfoTextView.append(info + "\n");
                        info = TestModeUtil.getLastLog();
                    }
                    //滚动log到底部
                    mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
                }
            });
        } else if (type == TestModeUtil.TYPE_STATUS) {
            LogUtil.w("LOG TYPE_STATUS");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        onLogUpdate(TYPE_LOG);
        onLogUpdate(TestModeUtil.TYPE_STATUS);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.menu_about:
//                startActivity(new Intent(this, AboutActivity.class));
//                break;
//        }
        return super.onOptionsItemSelected(item);
    }

    protected void addLog(String tag, String info) {
        TestModeUtil.addLogString(tag, info);
    }

    /**
     * 切换Fragment
     *
     * @param lastIndex 上个显示Fragment的索引
     * @param index     需要显示的Fragment的索引
     */
    public void switchFragment(int lastIndex, int index) {
        if (lastIndex == index) return;
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.hide(fragments[lastIndex]);
        if (!fragments[index].isAdded()) {
            transaction.add(R.id.content, fragments[index]);
        }
        transaction.show(fragments[index]).commitAllowingStateLoss();
    }


    /**
     * 主页面中的各个fragment的按钮点击事件都汇集到这里处理，已经在layout xml文件中声明
     */
    public void onButtonClick(View view) {
        if (view instanceof Button) {
            addLog("点击Button", ((Button) view).getText().toString());
        } else if (view instanceof TextView) {
            addLog("点击TextView", ((TextView) view).getText().toString());
        } else {
            addLog("点击控件", view.getClass().getSimpleName());
        }
//        try {
//            switch (view.getId()) {
//                //基本信息页面按钮事件
//                case R.id.btn_app_register:
//                    PushManager.getInstance().getRegister();
//                    break;
//                case R.id.btn_app_unregister:
//                    PushManager.getInstance().unRegister();
//                    break;
//                case R.id.btn_check_opush_support:
//                    addLog("isSupportPush", "" + PushManager.isSupportPush(this));
//                    break;
//                case R.id.btn_get_regid:
//                    PushManager.getInstance().getRegister();
//                    break;
//                case R.id.btn_open_notification_setting:
//                    PushManager.getInstance().openNotificationSettings();
//                    break;
//                case R.id.btn_check_appid:
//                    showResult("AppId", AppParam.appId);
//                    showResult("TODO", "功能暂未实现");
//                    break;
//                case R.id.btn_get_push_service_status:
//                    PushManager.getInstance().getPushStatus();
//                    break;
//                case R.id.btn_get_statusbar_status:
//                    PushManager.getInstance().getNotificationStatus();
//                    break;
//                case R.id.btn_get_version_info:
//                    StringBuilder sb = new StringBuilder();
//                    sb.append("McsVerCode:").append(PushManager.getInstance().getPushVersionCode());
//                    sb.append(",McsVerName:").append(PushManager.getInstance().getPushVersionName());
//                    sb.append(",SdkVer:").append(PushManager.getInstance().getSDKVersion());
//                    addLog("Version", sb.toString());
//                    break;
//                //用户绑定页面信息事件
//                case R.id.btn_set_alias:
//                    PushManager.getInstance().setAliases(getAliasOrTopicsList());
//                    break;
//                case R.id.btn_unset_alias:
//                    PushManager.getInstance().unsetAliases(getAliasOrTopicsList());
//                    break;
//                case R.id.btn_get_alias_list:
//                    PushManager.getInstance().getAliases();
//                    break;
//                case R.id.btn_set_topics:
//                    PushManager.getInstance().setTags(getAliasOrTopicsList());
//                    break;
//                case R.id.btn_unset_topics:
//                    PushManager.getInstance().unsetTags(getAliasOrTopicsList());
//                    break;
//                case R.id.btn_get_topic_list:
//                    PushManager.getInstance().getTags();
//                    break;
//                //推送设置页面信息事件
//                case R.id.btn_pause_push:
//                    PushManager.getInstance().pausePush();
//                    break;
//                case R.id.btn_resume_push:
//                    PushManager.getInstance().resumePush();
//                    break;
//                default:
//                    break;
//            }
//        } catch (Exception e) {
//            showResult("Exception", e.getLocalizedMessage());
//            TestModeUtil.addLogString(e.getLocalizedMessage());
//        }
    }

    /**
     * 修改每周推送时间或者修改每天推送时间之后会触发此事件
     */
    public void onPushTimeConfigChange(@Nullable View view) {
        List<Integer> times = getPushTimesSet();//时间段要符合规则
        List<Integer> days = getPushWeekdays();//用1-7表示一周的7天
        if (days.isEmpty()) {
            showResult("Error", "推送时间设置不能为空！");
            return;
        }
        try {
            PushManager.getInstance().setPushTime(days, times.get(0), times.get(1), times.get(2), times.get(3));
            ((PushConfigFragment) pushConfigFragment).onPushDaysChange();
        } catch (Exception e) {
            showResult("Error", e.getLocalizedMessage());
            TestModeUtil.addLogString(e.getLocalizedMessage());
        }
    }

    /**
     * 编辑框中输入的别名或者标签列表设置，使用分号或者空格，逗号等隔开,从fragment中获取
     *
     * @return 至少包含一个参数，否则会报错
     */
    private List<String> getAliasOrTopicsList() {
        String content = ((UserBandingFragment) userBandingFragment).getInputText();
        List<String> list = Arrays.asList(content.split("[,;`~!?\\s.，。；？！·]"));
        LogUtil.d("list:" + Arrays.toString(list.toArray()));
        return list;
    }

    /**
     * 每周可加收推送消息的时间,从fragment中获取
     */
    private List<Integer> getPushWeekdays() {
        return ((PushConfigFragment) pushConfigFragment).getPushWeekdays();
    }

    /**
     * 每天接受推送消息的时间,从fragment中获取
     */
    private List<Integer> getPushTimesSet() {
        return ((PushConfigFragment) pushConfigFragment).getPushTime();
    }

    /**
     * 此方法会将结果进行回显
     */
    private void showResult(@Nullable String tag, @NonNull String msg) {
        addLog(tag, msg);
        LogUtil.d(tag + ":" + msg);
    }


}
