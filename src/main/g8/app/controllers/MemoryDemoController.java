package controllers;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import play.mvc.Http;
import play.mvc.Result;

import java.time.Duration;

/**
 * Sample controller to demo memory release feature of JVM 11.
 */
public class MemoryDemoController extends BasePageController {
    static Cache<String, byte[]> cache = buildCache();

    static Cache<String, byte[]> buildCache() {
        return CacheBuilder.newBuilder().expireAfterWrite(Duration.ofMillis(10000)).build();
    }

    static String format(double bytes, int digits) {
        String[] dictionary = { "bytes", "KB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB" };
        int index = 0;
        for (index = 0; index < dictionary.length; index++) {
            if (bytes < 1024) {
                break;
            }
            bytes = bytes / 1024;
        }
        return String.format("%." + digits + "f", bytes) + " " + dictionary[index];
    }

    public Result memInfo(Http.Request request, Integer requestMemMb) {
        StringBuffer sb = new StringBuffer();
        if (requestMemMb != null && requestMemMb.intValue() > 0) {
            sb.append("<p>Requesting " + requestMemMb + " mb of memory...</p>");
            long start = System.currentTimeMillis();
            for (int i = 0; i < requestMemMb.intValue(); i++) {
                cache.put(String.valueOf(start + i), new byte[1024 * 1024]);
            }
        }
        if (requestMemMb != null && requestMemMb.intValue() < 0) {
            sb.append("<p>Clearing cache...</p>");
            cache = buildCache();
            if (requestMemMb.intValue() == -1) {
                sb.append("<p>System.gc()...</p>");
                System.gc();
            }
        }

        Runtime runtime = Runtime.getRuntime();
        sb.append("<ul>");
        sb.append("<li>Max memory  : ").append(format(runtime.maxMemory(), 2)).append("</li>");
        sb.append("<li>Free memory : ").append(format(runtime.freeMemory(), 2)).append("</li>");
        sb.append("<li>Total memory: ").append(format(runtime.totalMemory(), 2)).append("</li>");
        sb.append("</ul>");

        sb.append("<p>Cache instance: ").append(Integer.toHexString(System.identityHashCode(cache))).append("</p>");

        return ok(sb.toString()).as("text/html; charset=utf-8");
    }
}
