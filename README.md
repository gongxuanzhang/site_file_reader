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
