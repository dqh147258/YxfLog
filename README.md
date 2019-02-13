# YxfLog

[ ![Download](https://api.bintray.com/packages/dqh147258/android/YxfLog/images/download.svg?version=1.0.0) ](https://bintray.com/dqh147258/android/YxfLog/1.0.0/link)

一个支持多TAG的由logger扩展出来的log框架


## logger的缺点
[logger](https://github.com/orhanobut/logger)是一个很优秀Android log框架,
但是也有让人非常不爽的缺点,比如

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
1. 添加jcenter依赖

```
compile 'com.yxf:log:1.0.0'
```

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

## 致谢
[logger](https://github.com/orhanobut/logger)

## 关联文章
[自定义一个自己的Log](https://blog.csdn.net/dqh147258/article/details/79774898)