# 自定义一个自己的Log(原谅我懒直接把博客拉过来当说明)
## 目录
[TOC]
## Android Log简介
Android 中`android.util`包下有个`Log`类,支持Verbose,Debug,Info,Warn,Error 5个等级的Log打印,然而这个类功能略简陋,只支持简单的信息输出,而且每次必须带一个TAG参数,发布的时候要关闭调试信息也不方便.其实最关键的是这个Log不够酷_(:з」∠)_

## 来看看github中有哪些酷酷的Log开源库
### [XLog](https://github.com/elvishew/xLog)
这是国内一个开发者开发的一个功能非常完善的一个Log库,可以说是很酷了.
### [LogUtils](https://github.com/pengwei1024/LogUtils)
这个和上面的差不多,也是国内一个开发者写的.
### [logger](https://github.com/orhanobut/logger)
这个是国外大神写的,以上两个Log应该也借鉴了这个开源库,可以说是鼻祖级别了.在此前大半年的开发中我都是使用的这个开源库来打Log.

源代码设计模式用的不错
## 这些炫酷的Log的原理解析
这些Log可以打印代码所在行数,栈信息,线程信息等.那他们使用了什么方法呢?

其实很简单

只用了两个方法而已

```
StackTraceElement[] elements = Thread.currentThread().getStackTrace();//获得执行栈信息,比如类,行数,方法名等
```

和

```
Thread.currentThread().getId();//获得线程Id
```
以一定格式打印,Android Studio会解析栈的打印信息,然后我们点击就可以准确定位到行数了.


## logger的缺点
- 无法定义多个TAG,一个程序只能有一个TAG,有时候看Log区分度低不好看.
- 貌似不支持打印等级控制

## YxfLog简介
忍logger不能有多个TAG很久了,所以前段时间下定决心决定自己改一个,由此YxfLog便出现了.

### 功能简介
- YxfLog基于logger修改,由于觉得磁盘Log目前需求不大,故而把磁盘储存功能删除,支持除了磁盘储存的其他功能.
- 可以定义主TAG和子TAG
- 可以限制打印等级
- 可以new 出子Log,以实现不同地方的不同打印效果
- YxfLog只有一个文件,所以非常方便的可以移植到另一个项目,gradle依赖确实爽,然而对于我这个从事系统源码编译的,想用gradle编译?呵呵,还是乖乖写Android.mk吧,所以感觉直接将一个文件复制过去还是非常爽的( • ̀ω•́ )✧

### 使用
1. 将YxfLog文件复制到自己项目中
2. 设置可选初始化设置
```
//在最先初始化的类中加入如下代码,以下参数皆为可选参数
    static {
        YxfLog.setMainTag("Main"); // 设置主TAG,默认TAG: Yxf
        YxfLog.setDefaultIsShowThreadInfo(false);//设置是否显示线程信息,默认false
        YxfLog.setDefaultLogLevel(YxfLog.VERBOSE);//设置打印等级,VERBOSE,Debug,Info,Warn,Error,None,在YxfLog中有定义,默认Verbose,设置为None便会关闭打印
        YxfLog.setDefaultMethodCount(2);//设置打印方法,默认2
        YxfLog.setDefaultMethodOffset(0);//设置方法偏移值,默认0
        YxfLog.setDefaultTAGLength(20);//设置子TAG的长度,默认20
    }
```
3. 主Log的使用
```
        //
    YxfLog.d("It is a main log message");//带框的详细Log
    
    YxfLog.sw("It is a simple warming");//原生Log(simple log)
    
```

    打印效果如下
    
![打印效果](http://resource-1255703580.cossh.myqcloud.com/YxfLog/YxfLog_d_sw.png)

4. 子Log的使用
```
//
    YxfLog.SubLog log = YxfLog.builder("SubTAG")
                .setMethodCount(3)
                .setLogLevel(YxfLog.VERBOSE)
                .setMethodOffset(0)
                .setShowThreadInfo(true)
                .create();//创建子Log对象
    log.d("It is a sub message");//带框的详细Log
    log.se("It is a simple error message of sub log");//原生Log(simple log)
```

打印效果如下
    
![打印效果](http://resource-1255703580.cossh.myqcloud.com/YxfLog/YxfLog_SubLog_i_se.png)

## YxfLog源码及测试例子地址
[https://github.com/dqh147258/YxfLog](https://github.com/dqh147258/YxfLog)



## Android Studio Logcat的颜色设置
我的Logcat也是很炫的有没有ヽ(°▽、°)ﾉ

你们的Logcat是不是还是这样的呢

![image](http://resource-1255703580.cossh.myqcloud.com/YxfLog/YxfLog_default_logcat.png)

我们再来干一件酷酷的事情吧,把Logcat颜色改成酷酷的( • ̀ω•́ )✧

左上角,点击File-->Settings

然后在左上角的搜索框中输入Logcat

![image](http://resource-1255703580.cossh.myqcloud.com/YxfLog/YxfLog_Settings_search_Logcat.png)

取消Use inherited attributes

![image](http://resource-1255703580.cossh.myqcloud.com/YxfLog/YxfLog_Settings_Logcat_unchecked.png)

选择颜色

![image](http://resource-1255703580.cossh.myqcloud.com/YxfLog/YxfLog_Settings_Logcat_set_color.png)

放出楼主的颜色设置

Debug   -- 2695c6
Error   -- ff231c
Info    -- abff72
Verbose -- ffffff
Warning -- ff28fc

应用

![image](http://resource-1255703580.cossh.myqcloud.com/YxfLog/YxfLog_Settings_Logcat_apply.png)

再放一次最终效果图
![image](http://resource-1255703580.cossh.myqcloud.com/YxfLog/YxfLog_SubLog_i_se.png)



