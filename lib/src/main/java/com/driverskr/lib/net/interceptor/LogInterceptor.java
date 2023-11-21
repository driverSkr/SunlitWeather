package com.driverskr.lib.net.interceptor;

import com.driverskr.lib.utils.LogUtil;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.http.HttpHeaders;
import okio.Buffer;
import okio.BufferedSource;

public class LogInterceptor implements Interceptor {
    private static final Charset UTF8 = Charset.forName("UTF-8");

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        RequestBody requestBody = request.body();
        boolean hasRequestBody = requestBody != null;

        StringBuffer stringBuffer = new StringBuffer();

        stringBuffer.append("\n" + request.method());
        stringBuffer.append("  " + request.url());

        Headers requestHeaders = request.headers();
        for (int i = 0, count = requestHeaders.size(); i < count; i++) {
            String name = requestHeaders.name(i);
            // Skip headers from the request body as they are explicitly logged above.
            if (!"Content-Type".equalsIgnoreCase(name) && !"Content-Length".equalsIgnoreCase(name)
                    && !"User-Agent".equalsIgnoreCase(name)) {
                stringBuffer.append(" " + name + ": " + requestHeaders.value(i));
            }
        }

        if (hasRequestBody && !bodyEncoded(request.headers())) {
            Buffer buffer = new Buffer();
            requestBody.writeTo(buffer);

            Charset charset = UTF8;
            MediaType contentType = requestBody.contentType();
            if (contentType != null) {
                charset = contentType.charset(UTF8);
            }
            if (isPlaintext(buffer)) {
                stringBuffer.append("\n" + buffer.readString(charset));
            }
        }

        long startNs = System.nanoTime();
        Response response;
        try {
            response = chain.proceed(request);
        } catch (Exception e) {
            throw e;
        }
        long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);

        ResponseBody responseBody = response.body();
        long contentLength = responseBody.contentLength();

        stringBuffer.append("\ncode:" + response.code())
                .append("  message:" + response.message())
                .append("  time:" + tookMs + "ms")
                .append("  contentLength:" + contentLength);

        // 响应头
        Headers responseHeaders = response.headers();
        for (int i = 0, count = responseHeaders.size(); i < count; i++) {
            String name = responseHeaders.name(i);
            if ("Date".equalsIgnoreCase(name)) {
                stringBuffer.append("  " + name + ": " + responseHeaders.value(i));
            }
        }

        // 响应体
        responseBody.source();
        if (HttpHeaders.hasBody(response)) {
            BufferedSource source = responseBody.source();
            source.request(Long.MAX_VALUE); // Buffer the entire body.
            Buffer buffer = source.buffer();
            if ("gzip".equalsIgnoreCase(response.headers().get("Content-Encoding"))) {
                String content = streamToString(buffer.clone().inputStream());
                stringBuffer.append("\nresponse=" + content);
//                stringBuffer.append("\nresponse=" + decodeUnicode(content));
            } else {
                if (isPlaintext(buffer) && contentLength != 0) {
                    Charset charset = UTF8;
                    MediaType contentType = responseBody.contentType();
                    if (contentType != null) {
                        charset = contentType.charset(UTF8);
                    }
                    stringBuffer.append("\nresponse=" + buffer.clone().readString(charset));
                }
            }
        }

        LogUtil.d(stringBuffer.toString() + "\n ");
        return response;
    }

    public static String streamToString(InputStream in) throws IOException {
        //定义一个内存输出流
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        //将流转换成字符串
        GZIPInputStream gis = new GZIPInputStream(in);
        int len1 = -1;
        byte[] b1 = new byte[1024];
        while ((len1 = gis.read(b1)) != -1) {
            byteArrayOutputStream.write(b1, 0, len1);
        }
        byteArrayOutputStream.close();
        gis.close();
        in.close();
        return byteArrayOutputStream.toString();
    }

    /*
     * unicode编码转中文
     */
    public static String decodeUnicode(String dataStr) {
        int start = 0;
        int end;
        final StringBuffer buffer = new StringBuffer();
        while (start > -1) {
            end = dataStr.indexOf("\\u", start + 2);
            String charStr = "";
            if (end == -1) {
                charStr = dataStr.substring(start + 2);
            } else {
                charStr = dataStr.substring(start + 2, end);
            }
            char letter = (char) Integer.parseInt(charStr, 16); // 16进制parse整形字符串。
            buffer.append(new Character(letter).toString());
            start = end;
        }
        return buffer.toString();
    }

    static boolean isPlaintext(Buffer buffer) {
        try {
            Buffer prefix = new Buffer();
            long byteCount = buffer.size() < 64 ? buffer.size() : 64;
            buffer.copyTo(prefix, 0, byteCount);
            for (int i = 0; i < 16; i++) {
                if (prefix.exhausted()) {
                    break;
                }
                int codePoint = prefix.readUtf8CodePoint();
                if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                    return false;
                }
            }
            return true;
        } catch (EOFException e) {
            return false; // Truncated UTF-8 sequence.
        }
    }

    private boolean bodyEncoded(Headers headers) {
        String contentEncoding = headers.get("Content-Encoding");
        return contentEncoding != null && !contentEncoding.equalsIgnoreCase("identity");
    }
}