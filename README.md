# 文件解析

## API使用


1. 创建SiteFileReader对象

```java
SiteFileReader reader = new SiteFileReader(file);
```

2. 读取内容.

> 读取文件有两种方式，比较简单的是直接遍历,适用于普通场景，
```java

for (WaveformData waveformData : new SiteFileReader(file)) {
        System.out.println(waveformData);
    }
```

> 第二种方式是增加监听器,适用于复杂逻辑和解耦
```java

SiteFileListener listener = (data)-> System.out.println(data);
SiteFileReader reader = new SiteFileReader(file);
reader.addListener(listener);
reader.read();

```

## 配置
SiteFileReader如果buffer容量装不下一条完整数据，可以自动扩容。 
如果不想要这个功能，可以设置关闭这个功能，如果有buffer装不下的情况会报错

```java
SiteFileReader reader = new SiteFileReader(file);
reader.disableAutoExpand();

```
