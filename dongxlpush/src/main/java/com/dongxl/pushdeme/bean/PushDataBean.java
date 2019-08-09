/**
 * Wire
 * Copyright (C) 2019 Wire Swiss GmbH
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.dongxl.pushdeme.bean;

import java.io.Serializable;

public class PushDataBean implements Serializable {

    /**
     * 推送平台 小米 华为等
     */
    private String platform;
    /**
     * 返回结果的类型，比如 注册 ，透传 通知等
     */
    private int resultType;
    /**
     * 推送注册 token 或者 regId
     */
    private String regId;
    private String topic;
    private String alias;
    private String account;
    private String startTime;
    private String endTime;
    /**
     * 原因 说明
     */
    private String reason;
    /**
     * 结果码
     */
    private long resultCode;
    /**
     * 操作类型
     */
    private String command;

    private MessageDataBean throughMessage;

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public int getResultType() {
        return resultType;
    }

    public void setResultType(int resultType) {
        this.resultType = resultType;
    }

    public String getRegId() {
        return regId;
    }

    public void setRegId(String regId) {
        this.regId = regId;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public long getResultCode() {
        return resultCode;
    }

    public void setResultCode(long resultCode) {
        this.resultCode = resultCode;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public MessageDataBean getThroughMessage() {
        return throughMessage;
    }

    public void setThroughMessage(MessageDataBean throughMessage) {
        this.throughMessage = throughMessage;
    }

    public PushDataBean(int resultType) {
        this.resultType = resultType;
    }

    public PushDataBean(String command, long resultCode) {
        this.command = command;
        this.resultCode = resultCode;
    }

    public PushDataBean(int resultType, long resultCode) {
        this.resultType = resultType;
        this.resultCode = resultCode;
    }

    public PushDataBean() {
    }

    @Override
    public String toString() {
        return "PushDataBean{" +
            "platform='" + platform + '\'' +
            ", resultType=" + resultType +
            ", regId='" + regId + '\'' +
            ", topic='" + topic + '\'' +
            ", alias='" + alias + '\'' +
            ", account='" + account + '\'' +
            ", startTime='" + startTime + '\'' +
            ", endTime='" + endTime + '\'' +
            ", reason='" + reason + '\'' +
            ", resultCode=" + resultCode +
            ", command='" + command + '\'' +
            ", throughMessage=" + throughMessage +
            '}';
    }
}
