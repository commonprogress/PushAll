package com.dongxl.oppo.constants;


import com.example.jpushdemo.BuildConfig;

/**
 * <p>Title:${Title} </p>
 * <p>Description: AppParam</p>
 * <p>Copyright (c) 2016 www.oppo.com Inc. All rights reserved.</p>
 * <p>Company: OPPO</p>
 *
 * @author QuWanxin
 * @version 1.0
 * @date 2017/7/27
 */

public class AppParam {
    /**
     * 后台为每个应用分配的id，用于唯一标识一个应用，在程序代码中用不到
     */
    public static String appId = BuildConfig.appId;
    /**
     * appKey，用于向push服务器进行注册，开发者应当谨慎保存，避免泄漏
     */
    public static String appKey = BuildConfig.appKey;
    /**
     * appSecret，用于进行注册和消息加解密，开发者应当谨慎保存，避免泄漏
     */
    public static String appSecret = BuildConfig.appSecret;
}
