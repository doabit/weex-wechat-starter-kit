<template>
  <div class="wrapper">
    <image :src="logoUrl" class="logo"></image>
    <text class="title login" @click="login">WeChat Login</text>
    <text class="title pay"  @click="pay">WeChat Pay</text>
    <text class="title pay"  @click="shareText">Text Share</text>
    <text class="title pay"  @click="shareImage">Image Share</text>
    <text class="title pay"  @click="shareVideo">Video Share</text>
    <text class="title pay"  @click="shareWebPage">WebPage Share</text>
  </div>
</template>

<style>
  .wrapper { align-items: center; margin-top: 120px; }
  .title { padding-top:40px; padding-bottom: 40px; font-size: 48px; }
  .logo { width: 360px; height: 156px; }
  .desc { padding-top: 20px; color:#888; font-size: 24px;}
</style>

<script>
  var wechat = weex.requireModule('wechat');
  var stream = weex.requireModule('stream');
  var imageUrl = 'http://img1.vued.vanthink.cn/vued08aa73a9ab65dcbd360ec54659ada97c.png';
  // var globalEvent = weex.requireModule('globalEvent');

  // globalEvent.addEventListener("wx_login", function (e) {
  //   console.log("get geolocation", e)
  // });
  //

  export default {
    data: {
      logoUrl: 'http://img1.vued.vanthink.cn/vued08aa73a9ab65dcbd360ec54659ada97c.png',
    },
    created() {
      wechat.registerApp("wxa96ad00ad9b64cd9", function(data) {
        console.log(data, "wx register")
      })
    },
    methods: {
      shareText: function(e) {
        wechat.shareToTimeLine({
          type: "text",
          content: "文字分享"
        }, function(data) {
          console.log("text shared", data)
        })
      },

      shareImage: function(e) {
        wechat.shareToTimeLine({
          type: "image",
          image: imageUrl
        }, function(data) {
          console.log("image shared", data)
        })
      },

      shareVideo: function(e) {
        wechat.shareToTimeLine({
          type: "video",
          title: '视频分享标题',
          content: "视频分享内容",
          image: imageUrl,
          url: 'https://v.qq.com/x/cover/m4cz4v1n0av4a8k/x00223sb1nm.html?new=1'
        }, function(data) {
          console.log("video shared", data)
        })
      },

      shareWebPage: function(e) {
        wechat.shareToTimeLine({
          type: "webpage",
          title: '网页分享标题',
          content: "网页分享内容",
          image: imageUrl,
          url: 'http://github.com/doabit'
        }, function(data) {
          console.log("web page shared", data)
        })
      },
      pay: function (e) {
        stream.fetch({
               method: 'POST',
               url: 'http://xiao.tunnel.doabit.com/wx_app_pay',
               type: "json"
           }, function(resData){
              if (resData.ok) {
                var data = resData.data;
               wechat.pay({
                   // 微信支付所需必要参数，参考官方文档
                   appid: data.appid,
                   sign: data.sign,
                   timestamp: data.timestamp,
                   noncestr: data.noncestr,
                   partnerid: data.partnerid,
                   prepayid: data.prepayid,
                   packageValue:data.package
               }, function(resData){
                   // 支付结果
                   console.log(resData)
               })
             } else {
               console.log(resData.statusText)
             }
           })
      },
      login: function (e) {
        wechat.login({}, function(data) {
          console.log(data)
        })
      }
    }
  }
</script>
