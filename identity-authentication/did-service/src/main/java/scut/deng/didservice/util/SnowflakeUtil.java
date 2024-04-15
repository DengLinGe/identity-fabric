package scut.deng.didservice.util;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;

import java.lang.management.ManagementFactory;
import java.util.stream.Collectors;

public class SnowflakeUtil {
    private static Snowflake snowFlake = IdUtil.getSnowflake(
            NetUtil.ipv4ToLong(NetUtil.localIpv4s().stream().filter(s -> !StrUtil.startWith(s, "127")).limit(1).collect(Collectors.joining())) % 31,
            Long.parseLong(ManagementFactory.getRuntimeMXBean().getName().split("@")[0]) % 31
    );

    public static String nextIdStr() {
        return snowFlake.nextIdStr();
    }

}
