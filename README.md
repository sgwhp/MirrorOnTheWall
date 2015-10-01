# MirrorOnTheWall
## 前言
创意来源：[HomeMirror](https://github.com/HannahMitt/HomeMirror)
原文提到了一个东西，tow-way mirror（双向镜）。这个创意就是利用了单向镜（双向镜）光线强的一面无法看到光线弱的一面这个原理来实现遮挡住镜子后面的平板而同时又能显示屏幕上面的字。看过《红番区》的应该知道单向镜是什么了。
原作已经很好了，为什么要重做一个呢？首先，HomeMirror使用的天气接口在国内使用并不是十分准确；第二，没有什么方便的自定义功能，加上平板位于镜子后面，无法通过触摸屏幕来对其进行设置。所以本作品主要对以上两点做一些小修改：使用国内的天气接口，获取的天气信息更加准确；加入语音识别功能，语音添加提醒。
## 准备工作
### 材料
![](https://github.com/sgwhp/MirrorOnTheWall/raw/master/art/1.jpg)
 1. 单向膜*2，单向镜这个东西不太好买，网上没看到有。某宝上面搜索，只有单向膜，贴在玻璃上的。如果买不到单向镜的，可以考虑买单向膜+透明玻璃或塑料板代替。某宝上很多卖家都提供免费裁剪，可以根据实际需求购买合适的膜。由于单向膜的反射率不是特别高，建议玻璃上贴两层，效果比较好。
 2. 玻璃/塑料板，尺寸跟单向膜要一样，必须透明。
 3. 平板，轻一点的比较好。
 4. 小刀和塑料刮板，一般卖单向膜的会送。
 5. 双面胶布，需要粘性比较强的
 6. 黑色卡纸一张
###  拼装
 7. 一般玻璃上贴单向膜需要在玻璃和膜上都洒洗洁精水，贴之前确保玻璃表面干净，贴上后再用刮板把气泡刮走，这个步骤一定要仔细，不然水干了之后会出现很多气泡。
 8. 下载apk并安装运行。[motw.apk](https://github.com/sgwhp/MirrorOnTheWall/raw/master/motw.apk)
![](https://github.com/sgwhp/MirrorOnTheWall/raw/master/art/3.jpg)
 10. 最终效果：
 ![](https://github.com/sgwhp/MirrorOnTheWall/raw/master/art/2.jpg)
### 语音控制
按下音量+、-键即可语音收入。
 1. 新建提醒：如明天早上8点提醒我下午开会、1小时后提醒我开会、每周一提醒我上班等。
 2. 删除提醒：语音输入删除提醒，会清空已显示的提醒，如果还有未执行到的提醒，会提示是否一并删除。
### 编译
由于本项目用到了百度的语音识别以及地图api，需要它的key。
创建百度地图开发平台的应用[（地址）](http://lbsyun.baidu.com/apiconsole/key?application=key)。开通语音识别服务和离线识别授权[（地址）](http://yuyin.baidu.com/app)，并在自定义设置中选择语义解析设置的日期提醒，保存。在res目录下创建key.xml文件，把刚才创建应用的相关信息编辑进去：
```
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="appId"></string>
    <string name="appKey"></string>
    <string name="secretKey"></string>
</resources>
```
