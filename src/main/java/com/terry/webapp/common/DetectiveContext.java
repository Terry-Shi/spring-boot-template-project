package com.terry.webapp.common;

import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 
 */
public class DetectiveContext {
    private static ThreadLocal<ConcurrentHashMap<String, String>> threadLocal = ThreadLocal.withInitial(ConcurrentHashMap::new);

    public static String getUserId() {
        return threadLocal.get().getOrDefault("userId", null);
    }

    public static void setUserId(String userId) {
        if (StringUtils.isNotBlank(userId)) {
            threadLocal.get().put("userId", userId);
        }
    }

    public static String getName() {
        return threadLocal.get().getOrDefault("name", null);
    }

    public static void setName(String name) {
        if (StringUtils.isNotBlank(name)) {
            threadLocal.get().put("name", name);
        }
    }

    public static String getLevel() {
        return threadLocal.get().getOrDefault("level", null);
    }

    public static void setLevel(String level) {
        if (StringUtils.isNotBlank(level)) {
            threadLocal.get().put("level", level);
        }
    }

    public static String getRole() {
        return threadLocal.get().getOrDefault("role", null);
    }

    public static void setRole(String role) {
        if (StringUtils.isNotBlank(role)) {
            threadLocal.get().put("role", role);
        }
    }

    public static String getOrganization() {
        return threadLocal.get().getOrDefault("organization", null);
    }

    public static void setOrganization(String organization) {
        if (StringUtils.isNotBlank(organization)) {
            threadLocal.get().put("organization", organization);
        }
    }

    public static String getOrganizationCode() {
        return threadLocal.get().getOrDefault("organizationCode", null);
    }

    public static void setOrganizationCode(String organizationCode) {
        if (StringUtils.isNotBlank(organizationCode)) {
            threadLocal.get().put("organizationCode", organizationCode);
        }
    }

    public static String getStatus() {
        return threadLocal.get().getOrDefault("status", null);
    }

    public static void setStatus(String status) {
        if (StringUtils.isNotBlank(status)) {
            threadLocal.get().put("status", status);
        }
    }

    //每次http请求随机产生的ID
    public static String getRequestId() {
        return threadLocal.get().getOrDefault("requestId", "");
    }

    public static void setRequestId(String requestId) {
        if (StringUtils.isNotBlank(requestId)) {
            threadLocal.get().put("requestId", requestId);
        }
    }

}
