<template>
  <div class="wrapper">
    <image :src="logoUrl" class="logo"></image>
    <text class="title login" @click="login">WeChat Login</text>
    <text class="title pay"  @click="pay">WeChat Pay</text>
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
  var stream = weex.requireModule('stream')
var globalEvent = weex.requireModule('globalEvent');

        globalEvent.addEventListener("wx_login", function (e) {
          console.log("get geolocation", e)
        });


  export default {
    data: {
      logoUrl: 'http://img1.vued.vanthink.cn/vued08aa73a9ab65dcbd360ec54659ada97c.png',
      target: 'World'
    },
    created() {
      console.log("start")
      wechat.registerApp("wxa96ad00ad9b64cd9", function(data) {
        console.log(data, "wx register")
      })
    },
    methods: {
      pay: function (e) {
        stream.fetch({
               method: 'POST',
               url: 'http://xiao.tunnel.doabit.com/wx_app_pay',
               type: "json"
           }, function(resData){
               var data = resData.data;
               // resData 数据
               console.log(data)
               // 示例
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
                   // console.log('success')
               })
           })
      },
      login: function (e) {
        wechat.login({"xiao": 'pay'}, function(data) {
          console.log(data)
        })
        // dxevent.auth({"xiao": "deng"}, function(data) {
        //   console.log(data)
        // })
      }
    }
  }
</script>
