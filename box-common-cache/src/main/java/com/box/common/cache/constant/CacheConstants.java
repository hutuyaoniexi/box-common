package com.box.common.cache.constant;

import java.time.Duration;

/**
 * 缓存通用常量。
 */
public final class CacheConstants {

    public static final String KEY_SEPARATOR = ":";
    public static final String DEFAULT_NAMESPACE = "box";
    public static final Duration DEFAULT_TTL = Duration.ofHours(1);
    public static final Duration NONE_TTL = Duration.ZERO;

    /**
     * 参数管理 cache key
     */
    public static final String SYS_CONFIG_KEY = "sys_config:";
    /**
     * 密码最大错误次数
     */
    public final static int PASSWORD_MAX_RETRY_COUNT = 5;

    /**
     * 密码锁定时间，默认10（分钟）
     */
    public final static long PASSWORD_LOCK_TIME = 10;

    /**
     * 登录IP黑名单 cache key
     */
    public static final String SYS_LOGIN_BLACKIPLIST = SYS_CONFIG_KEY + "sys.login.blackIPList";
    private CacheConstants() {
    }

    /**
     * 登录账户密码错误次数 redis key
     */
    public static final String PWD_ERR_CNT_KEY = "pwd_err_cnt:";
    /**
     * 缓存有效期，默认720（分钟）
     */
    public final static long EXPIRATION = 720;


    /**
     * 权限缓存前缀
     */
    public final static String LOGIN_TOKEN_KEY = "login_tokens:";

    /**
     * 缓存刷新时间，默认120（分钟）
     */
    public final static long REFRESH_TIME = 120;
}
