package com.atguigu.gmall.bean;

import javax.persistence.Transient;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class UmsMember implements Serializable{

    String id;
    String memberLevelId;
    String username;
    String password;
    String nickname;
    String phone;
    String status;
    Date createTime;
    String icon;
    String gender;
    Date birthday;
    String city;
    String job;
    String personalizedSignature;
    String sourceUid;
    BigDecimal sourceType;
    String integration;
    String growth;
    String luckeyCount;

    public List<UmsMemberReceiveAddress> getUmsMemberReceiveAddresses() {
        return umsMemberReceiveAddresses;
    }

    public void setUmsMemberReceiveAddresses(List<UmsMemberReceiveAddress> umsMemberReceiveAddresses) {
        this.umsMemberReceiveAddresses = umsMemberReceiveAddresses;
    }

    String accessToken;
    String accessCode;
    BigDecimal historyIntegration;

    @Transient
    List<UmsMemberReceiveAddress> umsMemberReceiveAddresses;



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMemberLevelId() {
        return memberLevelId;
    }

    public void setMemberLevelId(String memberLevelId) {
        this.memberLevelId = memberLevelId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getPersonalizedSignature() {
        return personalizedSignature;
    }

    public void setPersonalizedSignature(String personalizedSignature) {
        this.personalizedSignature = personalizedSignature;
    }

    public String getSourceUid() {
        return sourceUid;
    }

    public void setSourceUid(String sourceUid) {
        this.sourceUid = sourceUid;
    }

    public BigDecimal getSourceType() {
        return sourceType;
    }

    public void setSourceType(BigDecimal sourceType) {
        this.sourceType = sourceType;
    }

    public String getIntegration() {
        return integration;
    }

    public void setIntegration(String integration) {
        this.integration = integration;
    }

    public String getGrowth() {
        return growth;
    }

    public void setGrowth(String growth) {
        this.growth = growth;
    }

    public String getLuckeyCount() {
        return luckeyCount;
    }

    public void setLuckeyCount(String luckeyCount) {
        this.luckeyCount = luckeyCount;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessCode() {
        return accessCode;
    }

    public void setAccessCode(String accessCode) {
        this.accessCode = accessCode;
    }

    public BigDecimal getHistoryIntegration() {
        return historyIntegration;
    }

    public void setHistoryIntegration(BigDecimal historyIntegration) {
        this.historyIntegration = historyIntegration;
    }
}
