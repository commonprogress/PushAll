package com.dongxl.oppo.component;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import com.dongxl.oppo.util.AppUtils;
import com.dongxl.oppo.util.LogUtil;
import com.dongxl.oppo.util.TestModeUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.List;

/**
 * <p>Title:${Title} </p>
 * <p>Description: MessageDispatcher</p>
 * <p>Copyright (c) 2016 www.oppo.com Inc. All rights reserved.</p>
 * <p>Company: OPPO</p>
 *
 * @author QuWanxin
 * @version 1.0
 * @date 2017/7/31
 */

public class MessageDispatcher {

    public static void dispatch(Context ctx, String content) {
        Intent intent = null;
        try {
            intent = Intent.parseUri(content, Intent.URI_INTENT_SCHEME);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        try {
            if (isSupportByIntent(ctx, intent)) {
                WebActivity.start(ctx, content, "");
            } else if (isJson(content)) {
                LogUtil.d("json command:" + content);
                handleSptMsg(ctx, content);
            } else if (content.startsWith("command")) {
                LogUtil.d("run command:" + content);
                handleCustomerMsg(ctx, content);
            } else {
                LogUtil.d("unknow command:" + content);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void handleCustomerMsg(Context ctx, String msg) {//普通的自定义消息
    }

    private static void handleSptMsg(final Context ctx, String msg) throws JSONException {//json格式的命令
        JSONObject json = new JSONObject(msg);
        final String actionUrl = json.optString("actionUrl");
        String actionActivity = json.optString("actionActivity");
        String logoUrl = json.optString("logoUrl");
        final String title = json.optString("title");
        String content = json.optString("content");
        JSONObject actionParams = json.optJSONObject("actionParams");
        if (AppUtils.isApplicationInBackground(ctx)) {
            TestModeUtil.addLogString("应用在后台，暂时不处理透传消息");
        } else {
            TestModeUtil.addLogString("应用在前台，打开透传消息指定的网页");
            WebActivity.start(ctx, actionUrl, title);
        }
    }

    private static boolean isSupportByIntent(Context context, Intent intent) {
        if (intent == null) return false;
        List list = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list != null && list.size() > 0;
    }

    private static boolean isJson(String json) {
        try {
            JSONObject object = new JSONObject(json);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
