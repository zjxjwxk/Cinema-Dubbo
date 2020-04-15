package com.stylefeng.guns.rest.modular.service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.dubbo.rpc.RpcContext;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.stylefeng.guns.api.alipay.AlipayServiceApi;
import com.stylefeng.guns.api.alipay.vo.AlipayInfoVO;
import com.stylefeng.guns.api.alipay.vo.AlipayResultVO;
import com.stylefeng.guns.api.order.OrderServiceApi;
import com.stylefeng.guns.api.order.vo.OrderVO;
import com.stylefeng.guns.rest.common.util.FTPUtil;
import com.stylefeng.guns.rest.modular.alipay.config.Configs;
import com.stylefeng.guns.rest.modular.alipay.model.ExtendParams;
import com.stylefeng.guns.rest.modular.alipay.model.GoodsDetail;
import com.stylefeng.guns.rest.modular.alipay.model.builder.AlipayTradePrecreateRequestBuilder;
import com.stylefeng.guns.rest.modular.alipay.model.builder.AlipayTradeQueryRequestBuilder;
import com.stylefeng.guns.rest.modular.alipay.model.result.AlipayF2FPrecreateResult;
import com.stylefeng.guns.rest.modular.alipay.model.result.AlipayF2FQueryResult;
import com.stylefeng.guns.rest.modular.alipay.service.AlipayMonitorService;
import com.stylefeng.guns.rest.modular.alipay.service.AlipayTradeService;
import com.stylefeng.guns.rest.modular.alipay.service.impl.AlipayMonitorServiceImpl;
import com.stylefeng.guns.rest.modular.alipay.service.impl.AlipayTradeServiceImpl;
import com.stylefeng.guns.rest.modular.alipay.service.impl.AlipayTradeWithHBServiceImpl;
import com.stylefeng.guns.rest.modular.alipay.utils.ZxingUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zjxjwxk
 */
@Slf4j
@Component
@Service(interfaceClass = AlipayServiceApi.class, mock = "com.stylefeng.guns.api.alipay.AlipayServiceMock")
public class DefaultAlipayServiceImpl implements AlipayServiceApi {

    @Reference(interfaceClass = OrderServiceApi.class, check = false, group = "order2020")
    private OrderServiceApi orderServiceApi;

    @Autowired
    private FTPUtil ftpUtil;

    /**
     * 支付宝当面付2.0服务
     */
    private static AlipayTradeService tradeService;

    /**
     * 支付宝当面付2.0服务（集成了交易保障接口逻辑）
     */
    private static AlipayTradeService tradeWithHBService;

    /**
     * 支付宝交易保障接口服务，供测试接口api使用
     */
    private static AlipayMonitorService monitorService;

    static {
        /* 一定要在创建AlipayTradeService之前调用Configs.init()设置默认参数
           Configs会读取classpath下的zfbinfo.properties文件配置信息，如果找不到该文件则确认该文件是否在classpath目录
         */
        Configs.init("zfbinfo.properties");

        /* 使用Configs提供的默认参数
           AlipayTradeService可以使用单例或者为静态成员对象，不需要反复new
         */
        tradeService = new AlipayTradeServiceImpl.ClientBuilder().build();

        // 支付宝当面付2.0服务（集成了交易保障接口逻辑）
        tradeWithHBService = new AlipayTradeWithHBServiceImpl.ClientBuilder().build();

        // 如果需要在程序中覆盖Configs提供的默认参数, 可以使用ClientBuilder类的setXXX方法修改默认参数 否则使用代码中的默认设置
        monitorService = new AlipayMonitorServiceImpl.ClientBuilder()
                .setGatewayUrl("http://mcloudmonitor.com/gateway.do").setCharset("GBK")
                .setFormat("json").build();
    }

    @Override
    public AlipayInfoVO getQRCode(String orderId) {
        // 通过远程参数验证用户
        if (checkUserId(orderId)) {
            return null;
        }
        // 获取二维码地址
        String filePath = tradePrecreate(orderId);
        // 如果地址为空，则表示获取二维码不成功
        if (filePath == null || filePath.trim().length() == 0) {
            return null;
        } else {
            AlipayInfoVO alipayInfoVO = new AlipayInfoVO();
            alipayInfoVO.setOrderId(orderId);
            alipayInfoVO.setQRCodeAddress(filePath);
            return alipayInfoVO;
        }
    }

    @Override
    public AlipayResultVO getOrderStatus(String orderId) {
        // 通过远程参数验证用户
        if (checkUserId(orderId)) {
            return null;
        }
        boolean ifPaySuccess = tradeQuery(orderId);
        if (ifPaySuccess) {
            AlipayResultVO alipayResultVO = new AlipayResultVO();
            alipayResultVO.setOrderId(orderId);
            alipayResultVO.setOrderStatus(1);
            alipayResultVO.setOrderMsg("支付成功");
            return alipayResultVO;
        } else {
            return null;
        }
    }

    /**
     * 当面付2.0生成支付二维码（预下单）
     * @param orderId 订单编号
     * @return 二维码地址，若预下单不成功，则为null
     */
    private String tradePrecreate(String orderId) {
        OrderVO orderVO = orderServiceApi.getOrderInfoById(orderId);
        if (orderVO == null) {
            return null;
        }

        String filePath = null;

        // (必填) 商户网站订单系统中唯一订单号，64个字符以内，只能包含字母、数字、下划线，
        // 需保证商户系统端不能重复，建议通过数据库sequence生成，
        String outTradeNo = orderId;

        // (必填) 订单标题，粗略描述用户的支付目的。如“xxx品牌xxx门店当面付扫码消费”
        String subject = "Cinema影院购票业务";

        // (必填) 订单总金额，单位为元，不能超过1亿元
        // 如果同时传入了【打折金额】,【不可打折金额】,【订单总金额】三者,则必须满足如下条件:【订单总金额】=【打折金额】+【不可打折金额】
        String totalAmount = orderVO.getOrderPrice();

        // (可选) 订单不可打折金额，可以配合商家平台配置折扣活动，如果酒水不参与打折，则将对应金额填写至此字段
        // 如果该值未传入,但传入了【订单总金额】,【打折金额】,则该值默认为【订单总金额】-【打折金额】
        String undiscountableAmount = "0";

        // 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号)
        // 如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
        String sellerId = "";

        // 订单描述，可以对交易或商品进行一个详细地描述，比如填写"购买商品2件共15.00元"
        String body = "购买电影票共消费" + orderVO.getOrderPrice() + "元";

        // 商户操作员编号，添加此参数可以为商户操作员做销售统计
        String operatorId = "operator 1";

        // (必填) 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
        String storeId = "Zjxjwxk's Cinema";

        // 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)，详情请咨询支付宝技术支持
        ExtendParams extendParams = new ExtendParams();
        extendParams.setSysServiceProviderId("2088100200300400500");

        // 支付超时，定义为120分钟
        String timeoutExpress = "120m";

        // 商品明细列表，需填写购买商品详细信息，
            List<GoodsDetail> goodsDetailList = new ArrayList<>();

        // 创建扫码支付请求builder，设置请求参数
        AlipayTradePrecreateRequestBuilder builder = new AlipayTradePrecreateRequestBuilder()
                .setSubject(subject).setTotalAmount(totalAmount).setOutTradeNo(outTradeNo)
                .setUndiscountableAmount(undiscountableAmount).setSellerId(sellerId).setBody(body)
                .setOperatorId(operatorId).setStoreId(storeId).setExtendParams(extendParams)
                .setTimeoutExpress(timeoutExpress)
                //                .setNotifyUrl("http://www.test-notify-url.com")//支付宝服务器主动通知商户服务器里指定的页面http路径,根据需要设置
                .setGoodsDetailList(goodsDetailList);

        AlipayF2FPrecreateResult result = tradeService.tradePrecreate(builder);
        switch (result.getTradeStatus()) {
            case SUCCESS:
                log.info("支付宝预下单成功: )");

                AlipayTradePrecreateResponse response = result.getResponse();

                // 需要修改为运行机器上的路径
                filePath = String.format("/Users/zjxjwxk/Desktop/qr-%s.png",
                        response.getOutTradeNo());
                String fileName = String.format("qr-%s.png", response.getOutTradeNo());
                log.info("filePath:" + filePath);
                File qrCodeImge = ZxingUtils.getQRCodeImge(response.getQrCode(), 256, filePath);

                // FTP文件上传
                boolean ifSuccess = ftpUtil.uploadFile(fileName, qrCodeImge);
                if (!ifSuccess) {
                    filePath = "";
                    log.error("二维码上传失败");
                }
                break;

            case FAILED:
                log.error("支付宝预下单失败!!!");
                break;

            case UNKNOWN:
                log.error("系统异常，预下单状态未知!!!");
                break;

            default:
                log.error("不支持的交易状态，交易返回异常!!!");
                break;
        }
        return filePath;
    }

    /**
     * 测试当面付2.0查询订单
     */
    private boolean tradeQuery(String orderId) {
        boolean ifPaySuccess = false;
        // (必填) 商户订单号，通过此商户订单号查询当面付的交易状态
        String outTradeNo = orderId;

        // 创建查询请求builder，设置请求参数
        AlipayTradeQueryRequestBuilder builder = new AlipayTradeQueryRequestBuilder()
                .setOutTradeNo(outTradeNo);

        AlipayF2FQueryResult result = tradeService.queryTradeResult(builder);
        switch (result.getTradeStatus()) {
            case SUCCESS:
                log.info("查询返回该订单支付成功: )");
                // 当订单支付成功时，修改订单状态为1
                ifPaySuccess = orderServiceApi.paySuccess(orderId);
                break;

            case FAILED:
                log.error("查询返回该订单支付失败或被关闭!!!");
                break;

            case UNKNOWN:
                log.error("系统异常，订单支付状态未知!!!");
                break;

            default:
                log.error("不支持的交易状态，交易返回异常!!!");
                break;
        }
        return ifPaySuccess;
    }

    /**
     * 通过隐式参数检查当前处理订单的用户是否为当前登录用户
     * @param orderId 订单编号
     * @return 用户验证是否成功
     */
    private boolean checkUserId(String orderId) {
        // 获取隐式参数用户编号userId
        String userId = RpcContext.getContext().getAttachment("userId");
        Integer orderUser = orderServiceApi.getUserIdById(orderId);
        return orderUser == null || Integer.parseInt(userId) != orderUser;
    }
}
