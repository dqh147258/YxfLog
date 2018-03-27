package com.yxf.log;

import android.app.AlertDialog;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.LogPrinter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 * Created by quehuang.du on 2018/3/23.
 */

public class YxfLog {

    public static final int VERBOSE = 2;
    public static final int DEBUG = 3;
    public static final int INFO = 4;
    public static final int WARN = 5;
    public static final int ERROR = 6;
    public static final int ASSERT = 7;
    public static final int NONE = 9;


    private static String MAIN_TAG = "Yxf";
    private static String realTAG = null;
    private static int defaultMethodCount = 2;
    private static int defaultMethodOffset = 0;
    private static int defaultTAGLength = 20;
    private static boolean defaultIsShowThreadInfo = false;

    private static int defaultLogLevel = VERBOSE;

    private static Printer printer;

    static {
        update();
    }

    public static void setMainTag(String mainTag) {
        MAIN_TAG = mainTag;
        update();
    }

    public static void setDefaultMethodCount(int defaultMethodCount) {
        YxfLog.defaultMethodCount = defaultMethodCount;
        update();
    }

    public static void setDefaultTAGLength(int defaultTAGLength) {
        YxfLog.defaultTAGLength = defaultTAGLength;
        update();
    }

    public static void setDefaultMethodOffset(int defaultMethodOffset) {
        YxfLog.defaultMethodOffset = defaultMethodOffset;
        update();
    }

    public static void setDefaultIsShowThreadInfo(boolean defaultIsShowThreadInfo) {
        YxfLog.defaultIsShowThreadInfo = defaultIsShowThreadInfo;
        update();
    }

    public static void setDefaultLogLevel(int defaultLogLevel) {
        YxfLog.defaultLogLevel = defaultLogLevel;
        update();
    }

    private static String getBlank(int count) {
        String result = "";
        for (int i = 0; i < count; i++) {
            result = result + ".";
        }
        return result;
    }

    private static void update() {
        realTAG = MAIN_TAG + "." + getBlank(defaultTAGLength);
        PrettyFormatStrategy strategy = PrettyFormatStrategy.newBuilder()
                .tag(realTAG)
                .showThreadInfo(defaultIsShowThreadInfo)
                .methodCount(defaultMethodCount)
                .methodOffset(defaultMethodOffset)
                .build();
        LogAdapter adapter = new AndroidLogAdapter(strategy);
        printer = new LoggerPrinter().setLogLevel(defaultLogLevel);
        printer.addAdapter(adapter);
    }


    public static void clearLogAdapters() {
        printer.clearLogAdapters();
    }

    /**
     * Given tag will be used as tag only once for this method call regardless of the tag that's been
     * set during initialization. After this invocation, the general tag that's been set will
     * be used for the subsequent log calls
     */
    public static Printer t(@Nullable String tag) {
        return printer.t(tag);
    }

    /**
     * General log function that accepts all configurations as parameter
     */
    public static void log(int priority, @Nullable String tag, @Nullable String message, @Nullable Throwable throwable) {
        printer.log(priority, tag, message, throwable);
    }

    public static void d(@NonNull String message, @Nullable Object... args) {
        printer.d(message, args);
    }

    public static void d(@Nullable Object object) {
        printer.d(object);
    }

    public static void e(@NonNull String message, @Nullable Object... args) {
        printer.e(null, message, args);
    }

    public static void e(@Nullable Throwable throwable, @NonNull String message, @Nullable Object... args) {
        printer.e(throwable, message, args);
    }

    public static void i(@NonNull String message, @Nullable Object... args) {
        printer.i(message, args);
    }

    public static void v(@NonNull String message, @Nullable Object... args) {
        printer.v(message, args);
    }

    public static void w(@NonNull String message, @Nullable Object... args) {
        printer.w(message, args);
    }

    /**
     * Tip: Use this for exceptional situations to log
     * ie: Unexpected errors etc
     */
    public static void wtf(@NonNull String message, @Nullable Object... args) {
        printer.wtf(message, args);
    }

    /**
     * Formats the given json content and print it
     */
    public static void json(@Nullable String json) {
        printer.json(json);
    }

    /**
     * Formats the given xml content and print it
     */
    public static void xml(@Nullable String xml) {
        printer.xml(xml);
    }

    //----------------------simple log -----------------

    public static void sv(String message) {
        if (defaultLogLevel <= VERBOSE) {
            Log.v(realTAG, message);
        }
    }

    public static void sv(String message, Throwable throwable) {
        if (defaultLogLevel <= VERBOSE) {
            Log.v(realTAG, message, throwable);
        }
    }

    public static void sd(String message) {
        if (defaultLogLevel <= DEBUG) {
            Log.d(realTAG, message);
        }
    }

    public static void sd(String message, Throwable throwable) {
        if (defaultLogLevel <= DEBUG) {
            Log.d(realTAG, message, throwable);
        }
    }

    public static void si(String message) {
        if (defaultLogLevel <= INFO) {
            Log.i(realTAG, message);
        }
    }

    public static void si(String message, Throwable throwable) {
        if (defaultLogLevel <= INFO) {
            Log.i(realTAG, message, throwable);
        }
    }

    public static void sw(String message) {
        if (defaultLogLevel <= WARN) {
            Log.w(realTAG, message);
        }
    }

    public static void sw(String message, Throwable throwable) {
        if (defaultLogLevel <= WARN) {
            Log.w(realTAG, message, throwable);
        }
    }

    public static void se(String message) {
        if (defaultLogLevel <= ERROR) {
            Log.e(realTAG, message);
        }
    }

    public static void se(String message, Throwable throwable) {
        if (defaultLogLevel <= ERROR) {
            Log.e(realTAG, message, throwable);
        }
    }

    //---------------------- sub log builder ----------------

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(String TAG) {
        return new Builder(TAG);
    }

    public static Builder builder(Object o) {
        return new Builder(o);
    }


    //---------------------------------------------------------------------

    public static class SubLog {

        private LoggerPrinter printer;

        private int logLevel = VERBOSE;
        private String TAG;
        private String realTAG = null;

        private SubLog(Builder builder) {


            if (builder.SUB_TAG == null || builder.SUB_TAG.length() < 1) {
                TAG = YxfLog.realTAG;
            } else {
                int length = builder.SUB_TAG.length();
                if (length < builder.TAGLength) {
                    TAG = MAIN_TAG + "." + builder.SUB_TAG + getBlank(builder.TAGLength - length);
                } else if (length == builder.TAGLength) {
                    TAG = MAIN_TAG + "." + builder.SUB_TAG;
                } else {
                    TAG = MAIN_TAG + "." + builder.SUB_TAG.substring(builder.TAGLength);
                }
            }
            realTAG = TAG;
            PrettyFormatStrategy strategy = PrettyFormatStrategy.newBuilder()
                    .methodOffset(builder.methodOffset + 1)
                    .showThreadInfo(builder.isShowThreadInfo)
                    .methodCount(builder.methodCount)
                    .tag(TAG)
                    .build();
            LogAdapter adapter = new AndroidLogAdapter(strategy);
            logLevel = builder.logLevel;
            printer = new LoggerPrinter().setLogLevel(logLevel);
            printer.addAdapter(adapter);
        }

        public Printer t(@Nullable String tag) {
            return printer.t(tag);
        }

        /**
         * General log function that accepts all configurations as parameter
         */
        public void log(int priority, @Nullable String tag, @Nullable String message, @Nullable Throwable throwable) {
            printer.log(priority, tag, message, throwable);
        }

        public void d(@NonNull String message, @Nullable Object... args) {
            printer.d(message, args);
        }

        public void d(@Nullable Object object) {
            printer.d(object);
        }

        public void e(@NonNull String message, @Nullable Object... args) {
            printer.e(null, message, args);
        }

        public void e(@Nullable Throwable throwable, @NonNull String message, @Nullable Object... args) {
            printer.e(throwable, message, args);
        }

        public void i(@NonNull String message, @Nullable Object... args) {
            printer.i(message, args);
        }

        public void v(@NonNull String message, @Nullable Object... args) {
            printer.v(message, args);
        }

        public void w(@NonNull String message, @Nullable Object... args) {
            printer.w(message, args);
        }

        /**
         * Tip: Use this for exceptional situations to log
         * ie: Unexpected errors etc
         */
        public void wtf(@NonNull String message, @Nullable Object... args) {
            printer.wtf(message, args);
        }

        /**
         * Formats the given json content and print it
         */
        public void json(@Nullable String json) {
            printer.json(json);
        }

        /**
         * Formats the given xml content and print it
         */
        public void xml(@Nullable String xml) {
            printer.xml(xml);
        }

        //--------------------simple log-----------------------------


        public void sv(String message) {
            if (logLevel <= VERBOSE) {
                Log.v(realTAG, message);
            }
        }

        public void sv(String message, Throwable throwable) {
            if (logLevel <= VERBOSE) {
                Log.v(realTAG, message, throwable);
            }
        }

        public void sd(String message) {
            if (logLevel <= DEBUG) {
                Log.d(realTAG, message);
            }
        }

        public void sd(String message, Throwable throwable) {
            if (logLevel <= DEBUG) {
                Log.d(realTAG, message, throwable);
            }
        }

        public void si(String message) {
            if (logLevel <= INFO) {
                Log.i(realTAG, message);
            }
        }

        public void si(String message, Throwable throwable) {
            if (logLevel <= INFO) {
                Log.i(realTAG, message, throwable);
            }
        }

        public void sw(String message) {
            if (logLevel <= WARN) {
                Log.w(realTAG, message);
            }
        }

        public void sw(String message, Throwable throwable) {
            if (logLevel <= WARN) {
                Log.w(realTAG, message, throwable);
            }
        }

        public void se(String message) {
            if (logLevel <= ERROR) {
                Log.e(realTAG, message);
            }
        }

        public void se(String message, Throwable throwable) {
            if (logLevel <= ERROR) {
                Log.e(realTAG, message, throwable);
            }
        }

    }

    public static class Builder {
        private String SUB_TAG = "";
        private int methodCount = YxfLog.defaultMethodCount;
        private int methodOffset = YxfLog.defaultMethodOffset;
        private boolean isShowThreadInfo = YxfLog.defaultIsShowThreadInfo;
        private int logLevel = YxfLog.defaultLogLevel;
        private int TAGLength = YxfLog.defaultTAGLength;

        public Builder(String SUB_TAG) {
            this.SUB_TAG = SUB_TAG;
            init();
        }

        public Builder(Object o) {
            String name = o.getClass().getName();
            String[] strings = name.split("\\.");
            if (strings != null && strings.length > 0) {
                name = strings[strings.length - 1];
            }
            this.SUB_TAG = name;
            init();
        }

        public Builder() {
            init();
        }

        private void init() {

        }

        public Builder setMethodCount(int methodCount) {
            this.methodCount = methodCount;
            return this;
        }

        public Builder setMethodOffset(int methodOffset) {
            this.methodOffset = methodOffset;
            return this;
        }

        public void setTAGLength(int TAGLength) {
            this.TAGLength = TAGLength;
        }

        public Builder setShowThreadInfo(boolean showThreadInfo) {
            isShowThreadInfo = showThreadInfo;
            return this;
        }

        public Builder setLogLevel(int logLevel) {
            this.logLevel = logLevel;
            return this;
        }

        public SubLog create() {
            return new SubLog(this);
        }
    }


    private static class AndroidLogAdapter implements LogAdapter {

        @NonNull
        private final FormatStrategy formatStrategy;

        public AndroidLogAdapter() {
            this.formatStrategy = PrettyFormatStrategy.newBuilder().build();
        }

        public AndroidLogAdapter(@NonNull FormatStrategy formatStrategy) {
            this.formatStrategy = Utils.checkNotNull(formatStrategy);
        }

        @Override
        public boolean isLoggable(int priority, @Nullable String tag) {
            return true;
        }

        @Override
        public void log(int priority, @Nullable String tag, @NonNull String message) {
            formatStrategy.log(priority, tag, message);
        }

    }

    private static class PrettyFormatStrategy implements FormatStrategy {

        /**
         * Android's max limit for a log entry is ~4076 bytes,
         * so 4000 bytes is used as chunk size since default charset
         * is UTF-8
         */
        private static final int CHUNK_SIZE = 4000;

        /**
         * The minimum stack trace index, starts at this class after two native calls.
         */
        private static final int MIN_STACK_OFFSET = 5;

        /**
         * Drawing toolbox
         */
        private static final char TOP_LEFT_CORNER = '┌';
        private static final char BOTTOM_LEFT_CORNER = '└';
        private static final char MIDDLE_CORNER = '├';
        private static final char HORIZONTAL_LINE = '│';
        private static final String DOUBLE_DIVIDER = "────────────────────────────────────────────────────────";
        private static final String SINGLE_DIVIDER = "┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄";
        private static final String TOP_BORDER = TOP_LEFT_CORNER + DOUBLE_DIVIDER + DOUBLE_DIVIDER;
        private static final String BOTTOM_BORDER = BOTTOM_LEFT_CORNER + DOUBLE_DIVIDER + DOUBLE_DIVIDER;
        private static final String MIDDLE_BORDER = MIDDLE_CORNER + SINGLE_DIVIDER + SINGLE_DIVIDER;

        private final int methodCount;
        private final int methodOffset;
        private final boolean showThreadInfo;
        @NonNull
        private final LogStrategy logStrategy;
        @Nullable
        private final String tag;

        private PrettyFormatStrategy(@NonNull Builder builder) {
            Utils.checkNotNull(builder);

            methodCount = builder.methodCount;
            methodOffset = builder.methodOffset;
            showThreadInfo = builder.showThreadInfo;
            logStrategy = builder.logStrategy;
            tag = builder.tag;
        }

        @NonNull
        public static Builder newBuilder() {
            return new Builder();
        }

        @Override
        public void log(int priority, @Nullable String onceOnlyTag, @NonNull String message) {
            Utils.checkNotNull(message);

            String tag = formatTag(onceOnlyTag);

            logTopBorder(priority, tag);
            logHeaderContent(priority, tag, methodCount);

            //get bytes of message with system's default charset (which is UTF-8 for Android)
            byte[] bytes = message.getBytes();
            int length = bytes.length;
            if (length <= CHUNK_SIZE) {
                if (methodCount > 0) {
                    logDivider(priority, tag);
                }
                logContent(priority, tag, message);
                logBottomBorder(priority, tag);
                return;
            }
            if (methodCount > 0) {
                logDivider(priority, tag);
            }
            for (int i = 0; i < length; i += CHUNK_SIZE) {
                int count = Math.min(length - i, CHUNK_SIZE);
                //create a new String with system's default charset (which is UTF-8 for Android)
                logContent(priority, tag, new String(bytes, i, count));
            }
            logBottomBorder(priority, tag);
        }

        private void logTopBorder(int logType, @Nullable String tag) {
            logChunk(logType, tag, TOP_BORDER);
        }

        @SuppressWarnings("StringBufferReplaceableByString")
        private void logHeaderContent(int logType, @Nullable String tag, int methodCount) {
            StackTraceElement[] trace = Thread.currentThread().getStackTrace();
            if (showThreadInfo) {
                logChunk(logType, tag, HORIZONTAL_LINE + " Thread: " + Thread.currentThread().getName());
                logDivider(logType, tag);
            }
            String level = "";

            int stackOffset = getStackOffset(trace) + methodOffset;

            //corresponding method count with the current stack may exceeds the stack trace. Trims the count
            if (methodCount + stackOffset > trace.length) {
                methodCount = trace.length - stackOffset - 1;
            }

            for (int i = methodCount; i > 0; i--) {
                int stackIndex = i + stackOffset;
                if (stackIndex >= trace.length) {
                    continue;
                }
                StringBuilder builder = new StringBuilder();
                builder.append(HORIZONTAL_LINE)
                        .append(' ')
                        .append(level)
                        .append(getSimpleClassName(trace[stackIndex].getClassName()))
                        .append(".")
                        .append(trace[stackIndex].getMethodName())
                        .append(" ")
                        .append(" (")
                        .append(trace[stackIndex].getFileName())
                        .append(":")
                        .append(trace[stackIndex].getLineNumber())
                        .append(")");
                level += "   ";
                logChunk(logType, tag, builder.toString());
            }
        }

        private void logBottomBorder(int logType, @Nullable String tag) {
            logChunk(logType, tag, BOTTOM_BORDER);
        }

        private void logDivider(int logType, @Nullable String tag) {
            logChunk(logType, tag, MIDDLE_BORDER);
        }

        private void logContent(int logType, @Nullable String tag, @NonNull String chunk) {
            Utils.checkNotNull(chunk);

            String[] lines = chunk.split(System.getProperty("line.separator"));
            for (String line : lines) {
                logChunk(logType, tag, HORIZONTAL_LINE + " " + line);
            }
        }

        private void logChunk(int priority, @Nullable String tag, @NonNull String chunk) {
            Utils.checkNotNull(chunk);

            logStrategy.log(priority, tag, chunk);
        }

        private String getSimpleClassName(@NonNull String name) {
            Utils.checkNotNull(name);

            int lastIndex = name.lastIndexOf(".");
            return name.substring(lastIndex + 1);
        }

        /**
         * Determines the starting index of the stack trace, after method calls made by this class.
         *
         * @param trace the stack trace
         * @return the stack offset
         */
        private int getStackOffset(@NonNull StackTraceElement[] trace) {
            Utils.checkNotNull(trace);

            for (int i = MIN_STACK_OFFSET; i < trace.length; i++) {
                StackTraceElement e = trace[i];
                String name = e.getClassName();
                if (!name.equals(LoggerPrinter.class.getName()) && !name.equals(YxfLog.class.getName())) {
                    return --i;
                }
            }
            return -1;
        }

        @Nullable
        private String formatTag(@Nullable String tag) {
            if (!Utils.isEmpty(tag) && !Utils.equals(this.tag, tag)) {
                return this.tag + "-" + tag;
            }
            return this.tag;
        }

        public static class Builder {
            int methodCount = 2;
            int methodOffset = 0;
            boolean showThreadInfo = true;
            @Nullable
            LogStrategy logStrategy;
            @Nullable
            String tag = "PRETTY_LOGGER";

            private Builder() {
            }

            @NonNull
            public Builder methodCount(int val) {
                methodCount = val;
                return this;
            }

            @NonNull
            public Builder methodOffset(int val) {
                methodOffset = val;
                return this;
            }

            @NonNull
            public Builder showThreadInfo(boolean val) {
                showThreadInfo = val;
                return this;
            }

            @NonNull
            public Builder logStrategy(@Nullable LogStrategy val) {
                logStrategy = val;
                return this;
            }

            @NonNull
            public Builder tag(@Nullable String tag) {
                this.tag = tag;
                return this;
            }

            @NonNull
            public PrettyFormatStrategy build() {
                if (logStrategy == null) {
                    logStrategy = new LogcatLogStrategy();
                }
                return new PrettyFormatStrategy(this);
            }
        }

    }

    private static class LogcatLogStrategy implements LogStrategy {

        static final String DEFAULT_TAG = "NO_TAG";

        @Override
        public void log(int priority, @Nullable String tag, @NonNull String message) {
            Utils.checkNotNull(message);

            if (tag == null) {
                tag = DEFAULT_TAG;
            }

            Log.println(priority, tag, message);
        }
    }


    private static class Utils {

        private Utils() {
            // Hidden constructor.
        }

        /**
         * Returns true if the string is null or 0-length.
         *
         * @param str the string to be examined
         * @return true if str is null or zero length
         */
        static boolean isEmpty(CharSequence str) {
            return str == null || str.length() == 0;
        }

        /**
         * Returns true if a and b are equal, including if they are both null.
         * <p><i>Note: In platform versions 1.1 and earlier, this method only worked well if
         * both the arguments were instances of String.</i></p>
         *
         * @param a first CharSequence to check
         * @param b second CharSequence to check
         * @return true if a and b are equal
         * <p>
         * NOTE: Logic slightly change due to strict policy on CI -
         * "Inner assignments should be avoided"
         */
        static boolean equals(CharSequence a, CharSequence b) {
            if (a == b) return true;
            if (a != null && b != null) {
                int length = a.length();
                if (length == b.length()) {
                    if (a instanceof String && b instanceof String) {
                        return a.equals(b);
                    } else {
                        for (int i = 0; i < length; i++) {
                            if (a.charAt(i) != b.charAt(i)) return false;
                        }
                        return true;
                    }
                }
            }
            return false;
        }

        /**
         * Copied from "android.util.Log.getStackTraceString()" in order to avoid usage of Android stack
         * in unit tests.
         *
         * @return Stack trace in form of String
         */
        static String getStackTraceString(Throwable tr) {
            if (tr == null) {
                return "";
            }

            // This is to reduce the amount of log spew that apps do in the non-error
            // condition of the network being unavailable.
            Throwable t = tr;
            while (t != null) {
                if (t instanceof UnknownHostException) {
                    return "";
                }
                t = t.getCause();
            }

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            tr.printStackTrace(pw);
            pw.flush();
            return sw.toString();
        }

        static String logLevel(int value) {
            switch (value) {
                case VERBOSE:
                    return "VERBOSE";
                case DEBUG:
                    return "DEBUG";
                case INFO:
                    return "INFO";
                case WARN:
                    return "WARN";
                case ERROR:
                    return "ERROR";
                case ASSERT:
                    return "ASSERT";
                default:
                    return "UNKNOWN";
            }
        }

        public static String toString(Object object) {
            if (object == null) {
                return "null";
            }
            if (!object.getClass().isArray()) {
                return object.toString();
            }
            if (object instanceof boolean[]) {
                return Arrays.toString((boolean[]) object);
            }
            if (object instanceof byte[]) {
                return Arrays.toString((byte[]) object);
            }
            if (object instanceof char[]) {
                return Arrays.toString((char[]) object);
            }
            if (object instanceof short[]) {
                return Arrays.toString((short[]) object);
            }
            if (object instanceof int[]) {
                return Arrays.toString((int[]) object);
            }
            if (object instanceof long[]) {
                return Arrays.toString((long[]) object);
            }
            if (object instanceof float[]) {
                return Arrays.toString((float[]) object);
            }
            if (object instanceof double[]) {
                return Arrays.toString((double[]) object);
            }
            if (object instanceof Object[]) {
                return Arrays.deepToString((Object[]) object);
            }
            return "Couldn't find a correct type for the object";
        }

        @NonNull
        static <T> T checkNotNull(@Nullable final T obj) {
            if (obj == null) {
                throw new NullPointerException();
            }
            return obj;
        }
    }

    private static class LoggerPrinter implements Printer {

        /**
         * It is used for json pretty print
         */
        private static final int JSON_INDENT = 2;

        /**
         * Provides one-time used tag for the log message
         */
        private final ThreadLocal<String> localTag = new ThreadLocal<>();

        private final List<LogAdapter> logAdapters = new ArrayList<>();

        private int logLevel = VERBOSE;

        public LoggerPrinter setLogLevel(int level) {
            logLevel = level;
            return this;
        }

        @Override
        public Printer t(String tag) {
            if (tag != null) {
                localTag.set(tag);
            }
            return this;
        }

        @Override
        public void d(@NonNull String message, @Nullable Object... args) {
            log(DEBUG, null, message, args);
        }

        @Override
        public void d(@Nullable Object object) {
            log(DEBUG, null, Utils.toString(object));
        }

        @Override
        public void e(@NonNull String message, @Nullable Object... args) {
            e(null, message, args);
        }

        @Override
        public void e(@Nullable Throwable throwable, @NonNull String message, @Nullable Object... args) {
            log(ERROR, throwable, message, args);
        }

        @Override
        public void w(@NonNull String message, @Nullable Object... args) {
            log(WARN, null, message, args);
        }

        @Override
        public void i(@NonNull String message, @Nullable Object... args) {
            log(INFO, null, message, args);
        }

        @Override
        public void v(@NonNull String message, @Nullable Object... args) {
            log(VERBOSE, null, message, args);
        }

        @Override
        public void wtf(@NonNull String message, @Nullable Object... args) {
            log(ASSERT, null, message, args);
        }

        @Override
        public void json(@Nullable String json) {
            if (Utils.isEmpty(json)) {
                d("Empty/Null json content");
                return;
            }
            try {
                json = json.trim();
                if (json.startsWith("{")) {
                    JSONObject jsonObject = new JSONObject(json);
                    String message = jsonObject.toString(JSON_INDENT);
                    d(message);
                    return;
                }
                if (json.startsWith("[")) {
                    JSONArray jsonArray = new JSONArray(json);
                    String message = jsonArray.toString(JSON_INDENT);
                    d(message);
                    return;
                }
                e("Invalid Json");
            } catch (JSONException e) {
                e("Invalid Json");
            }
        }

        @Override
        public void xml(@Nullable String xml) {
            if (Utils.isEmpty(xml)) {
                d("Empty/Null xml content");
                return;
            }
            try {
                Source xmlInput = new StreamSource(new StringReader(xml));
                StreamResult xmlOutput = new StreamResult(new StringWriter());
                Transformer transformer = TransformerFactory.newInstance().newTransformer();
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
                transformer.transform(xmlInput, xmlOutput);
                d(xmlOutput.getWriter().toString().replaceFirst(">", ">\n"));
            } catch (TransformerException e) {
                e("Invalid xml");
            }
        }

        @Override
        public synchronized void log(int priority,
                                     @Nullable String tag,
                                     @Nullable String message,
                                     @Nullable Throwable throwable) {
            if (throwable != null && message != null) {
                message += " : " + Utils.getStackTraceString(throwable);
            }
            if (throwable != null && message == null) {
                message = Utils.getStackTraceString(throwable);
            }
            if (Utils.isEmpty(message)) {
                message = "Empty/NULL log message";
            }

            for (LogAdapter adapter : logAdapters) {
                if (adapter.isLoggable(priority, tag)) {
                    adapter.log(priority, tag, message);
                }
            }
        }

        @Override
        public void clearLogAdapters() {
            logAdapters.clear();
        }

        @Override
        public void addAdapter(@NonNull LogAdapter adapter) {
            logAdapters.add(Utils.checkNotNull(adapter));
        }

        /**
         * This method is synchronized in order to avoid messy of logs' order.
         */
        private synchronized void log(int priority,
                                      @Nullable Throwable throwable,
                                      @NonNull String msg,
                                      @Nullable Object... args) {
            if (priority < logLevel) {
                return;
            }
            Utils.checkNotNull(msg);

            String tag = getTag();
            String message = createMessage(msg, args);
            log(priority, tag, message, throwable);
        }

        /**
         * @return the appropriate tag based on local or global
         */
        @Nullable
        private String getTag() {
            String tag = localTag.get();
            if (tag != null) {
                localTag.remove();
                return tag;
            }
            return null;
        }

        @NonNull
        private String createMessage(@NonNull String message, @Nullable Object... args) {
            return args == null || args.length == 0 ? message : String.format(message, args);
        }
    }


    interface LogAdapter {

        boolean isLoggable(int priority, @Nullable String tag);

        void log(int priority, @Nullable String tag, @NonNull String message);
    }


    interface FormatStrategy {

        void log(int priority, @Nullable String tag, @NonNull String message);
    }

    interface LogStrategy {

        void log(int priority, @Nullable String tag, @NonNull String message);
    }

    interface Printer {

        void addAdapter(@NonNull LogAdapter adapter);

        Printer t(@Nullable String tag);

        void d(@NonNull String message, @Nullable Object... args);

        void d(@Nullable Object object);

        void e(@NonNull String message, @Nullable Object... args);

        void e(@Nullable Throwable throwable, @NonNull String message, @Nullable Object... args);

        void w(@NonNull String message, @Nullable Object... args);

        void i(@NonNull String message, @Nullable Object... args);

        void v(@NonNull String message, @Nullable Object... args);

        void wtf(@NonNull String message, @Nullable Object... args);

        /**
         * Formats the given json content and print it
         */
        void json(@Nullable String json);

        /**
         * Formats the given xml content and print it
         */
        void xml(@Nullable String xml);

        void log(int priority, @Nullable String tag, @Nullable String message, @Nullable Throwable throwable);

        void clearLogAdapters();
    }


}
