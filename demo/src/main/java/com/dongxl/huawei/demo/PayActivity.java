package com.dongxl.huawei.demo;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.dongxl.huawei.hms.agent.HMSAgent;
import com.dongxl.huawei.hms.agent.pay.PaySignUtil;
import com.dongxl.huawei.hms.agent.pay.handler.GetOrderHandler;
import com.dongxl.huawei.hms.agent.pay.handler.GetProductDetailsHandler;
import com.dongxl.huawei.hms.agent.pay.handler.GetPurchaseInfoHandler;
import com.dongxl.huawei.hms.agent.pay.handler.PayHandler;
import com.dongxl.huawei.hms.agent.pay.handler.ProductPayHandler;
import com.example.jpushdemo.R;
import com.huawei.hms.support.api.entity.pay.OrderRequest;
import com.huawei.hms.support.api.entity.pay.PayReq;
import com.huawei.hms.support.api.entity.pay.PayStatusCodes;
import com.huawei.hms.support.api.entity.pay.ProductDetail;
import com.huawei.hms.support.api.entity.pay.ProductDetailRequest;
import com.huawei.hms.support.api.entity.pay.ProductFailObject;
import com.huawei.hms.support.api.entity.pay.ProductPayRequest;
import com.huawei.hms.support.api.entity.pay.PurchaseInfo;
import com.huawei.hms.support.api.entity.pay.PurchaseInfoRequest;
import com.huawei.hms.support.api.pay.OrderResult;
import com.huawei.hms.support.api.pay.PayResultInfo;
import com.huawei.hms.support.api.pay.ProductDetailResult;
import com.huawei.hms.support.api.pay.ProductPayResultInfo;
import com.huawei.hms.support.api.pay.PurchaseInfoResult;

import java.security.SecureRandom;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PayActivity  extends AgentBaseActivity {

    private static final String pay_priv_key = IEvnValues.pay_priv_key;
    private static final String pay_pub_key = IEvnValues.pay_pub_key;

    private static final String appId = IEvnValues.appId;
    private static final String cpId = IEvnValues.cpId;

    private Spinner spinnerDetail;
    private List<String> detailList;
    private ArrayAdapter<String> adapterDetail;

    private Spinner spinnerPMSPay;
    private List<String> pmsPayList;
    private ArrayAdapter<String> adapterPMSPay;

    /**
     * 未确认支付缓存，key：requestId  value：False | Unacknowledged payment cache, Key:requestid Value:false
     */
    private static final String UNCHECK_PAYREQUESTID_FILE = "pay_request_ids";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        setTabBtnClickListener();
        Button btn = (Button) findViewById(R.id.btn_iap);
        if (btn != null) {
            btn.setTextColor(Color.RED);
            btn.setEnabled(false);
        }

        findViewById(R.id.btn_pay).setOnClickListener(this);
        findViewById(R.id.btn_checkpay).setOnClickListener(this);
        findViewById(R.id.btn_getallproductdetail).setOnClickListener(this);
        findViewById(R.id.btn_purchaseinfo).setOnClickListener(this);

        // 初始化pms查询商品信息列表 | Initializing the PMS for a list of product information
        spinnerDetail = (Spinner) findViewById(R.id.spinner_getdetail);
        detailList = new ArrayList<String>();
        detailList.add("select product to getdetail:");
        detailList.add("PmsTestProduct0001");
        detailList.add("PmsTestProduct0002");
        detailList.add("PmsTestProduct0003");
        detailList.add("PmsTestProduct0004");
        detailList.add("PmsTestProduct0005");
        detailList.add("PmsTestProduct0006");
        adapterDetail= new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, detailList);
        adapterDetail.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDetail.setAdapter(adapterDetail);
        spinnerDetail.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    String productNo = pmsPayList.get(position);
                    getProductDetail(productNo);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // 初始化pms支付列表
        spinnerPMSPay = (Spinner) findViewById(R.id.spinner_pmspay);
        pmsPayList = new ArrayList<String>();
        pmsPayList.add("select product to pay:");
        pmsPayList.add("PmsTestProduct0001");
        pmsPayList.add("PmsTestProduct0002");
        pmsPayList.add("PmsTestProduct0003");
        pmsPayList.add("PmsTestProduct0004");
        pmsPayList.add("PmsTestProduct0005");
        pmsPayList.add("PmsTestProduct0006");
        adapterPMSPay= new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, pmsPayList);
        adapterPMSPay.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPMSPay.setAdapter(adapterPMSPay);
        spinnerPMSPay.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    String productNo = pmsPayList.get(position);
                    pmsPay(productNo);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    /**
     * 普通支付示例 | Common payment Examples
     */
    private void pay() {
        showLog("pay: begin");
        EditText etAmount = (EditText) findViewById(R.id.et_amount);
        float amount = 0;
        if (etAmount != null) {
            String text = etAmount.getText().toString();
            if (!TextUtils.isEmpty(text)) {
                try {
                    amount = Float.valueOf(text);
                } catch (NumberFormatException e) {
                    showLog("price format error");
                }
            }
        }

        PayReq payReq = createPayReq(amount);
        HMSAgent.Pay.pay(payReq, new PayHandler() {
            @Override
            public void onResult(int retCode, PayResultInfo payInfo) {
                if (retCode == HMSAgent.AgentResultCode.HMSAGENT_SUCCESS && payInfo != null) {
                    boolean checkRst = PaySignUtil.checkSign(payInfo, pay_pub_key);
                    showLog("pay: onResult: pay success and checksign=" + checkRst);
                    if (checkRst) {
                        // 支付成功并且验签成功，发放商品 | Payment successful and successful verification, distribution of goods
                    } else {
                        // 签名失败，需要查询订单状态：对于没有服务器的单机应用，调用查询订单接口查询；其他应用到开发者服务器查询订单状态。| Signature failed, need to query order status: For stand-alone applications without servers, call Query order interface query, other application to the Developer Server query order status.
                    }
                } else if (retCode == HMSAgent.AgentResultCode.ON_ACTIVITY_RESULT_ERROR
                        || retCode == PayStatusCodes.PAY_STATE_TIME_OUT
                        || retCode == PayStatusCodes.PAY_STATE_NET_ERROR) {
                    // 需要查询订单状态：对于没有服务器的单机应用，调用查询订单接口查询；其他应用到开发者服务器查询订单状态。 | Pay failed, need to query order status: For stand-alone applications without servers, call Query order interface query, other application to the Developer Server query order status.
                } else {
                    showLog("pay: onResult: pay fail=" + retCode);
                    // 其他错误码意义参照支付api参考 | Other error code meaning reference payment API reference
                }
            }
        });

        // 将requestid缓存，供查询订单 | RequestID Cache for Query order
        addRequestIdToCache(payReq.getRequestId());
    }

    /**
     * PMS支付示例 | PMS Payment Example
     */
    private void pmsPay(String productNo) {
        showLog("PMS pay: begin");

        ProductPayRequest payReq = createProductPayReq(productNo);
        HMSAgent.Pay.productPay(payReq, new ProductPayHandler() {
            @Override
            public void onResult(int retCode, ProductPayResultInfo payInfo) {
                if (retCode == HMSAgent.AgentResultCode.HMSAGENT_SUCCESS && payInfo != null) {
                    boolean checkRst = PaySignUtil.checkSign(payInfo, pay_pub_key);
                    showLog("PMS pay: onResult: pay success and checksign=" + checkRst + "\n");
                    if (checkRst) {
                        // 支付成功并且验签成功，发放商品 | Payment successful and successful verification, distribution of goods
                    } else {
                        // 签名失败，需要查询订单状态：对于没有服务器的单机应用，调用查询订单接口查询；其他应用到开发者服务器查询订单状态。| Signature failed, need to query order status: For stand-alone applications without servers, call Query order interface query, other application to the Developer Server query order status.
                    }
                } else if (retCode == HMSAgent.AgentResultCode.ON_ACTIVITY_RESULT_ERROR
                        || retCode == PayStatusCodes.PAY_STATE_TIME_OUT
                        || retCode == PayStatusCodes.PAY_STATE_NET_ERROR) {
                    showLog("PMS pay: onResult: need wait for result, rstcode=" + retCode + "\n");
                    // 需要查询订单状态：对于没有服务器的单机应用，调用查询订单接口查询；其他应用到开发者服务器查询订单状态。| Need to query order status: For stand-alone applications without servers, call Query order interface query, other application to Developer Server query order status.
                } else {
                    showLog("PMS pay: onResult: pay fail=" + retCode + "\n");
                    // 其他错误码意义参照支付api参考 | Other error code meaning reference payment API reference
                }
            }
        });

        // 将requestid缓存，供查询订单 | RequestID Cache for Query order
        addRequestIdToCache(payReq.getRequestId());
    }

    /**
     * 获取pms商品信息 | Get PMS Commodity Information
     * @param productNos pms商品编码，多个以“|”分割，最多支持20个。 | PMS commodity codes, multiple with "|" Split, supports up to 20
     */
    private void getProductDetail(String productNos) {
        showLog("getProductDetails: begin");

        ProductDetailRequest request = new ProductDetailRequest();

        /**
         * 生成requestId | Generate RequestID
         */
        DateFormat format = new java.text.SimpleDateFormat("yyyyMMddhhmmssSSS");
        int random= new SecureRandom().nextInt() % 100000;
        random = random < 0 ? -random : random;
        String requestId = format.format(new Date());
        requestId = String.format("%s%05d", requestId, random);

        // 商户id，又名“cpid”，“支付id”。来源于华为开发者联盟，移动应用详情页。| Merchant ID, also known as "Cpid", "Payment ID". From the Huawei Developer Alliance, mobile application Details page.
        request.merchantId = cpId;

        // 应用id。来源于华为开发者联盟，移动应用详情页。| The application ID. From the Huawei Developer Alliance, mobile application Details page.
        request.applicationID = appId;

        // 商户订单号。来源：开发者应用在发起请求前生成，用来唯一标识一次请求。注意：该字段中不能包含特殊字符，包括# " & / ? $ ^ *:) \ < > , | .,不支持中文。| Merchant Order number. Source: The developer application is generated before the request is initiated to uniquely identify the request. Note: This field cannot contain special characters, including # "&/? $ ^ *:) \ < >, |., not supported in Chinese
        request.requestId = requestId;

        // 商品编码。需要获取价格信息的productNo列表，多个商品编码以竖线分割，一次查询最多支持20个No。| Product Code. Need to get the price information of the Productno list, multiple product codes are divided by vertical bar, one query supports up to 20 No.
        // 注意：所查询的productNo必须属于对应的packageName,且在应用内唯一。| Note: The queried productno must belong to the corresponding PackageName and be unique within the application.
        request.productNos = productNos;

        HMSAgent.Pay.getProductDetails(request, new GetProductDetailsHandler() {
            @Override
            public void onResult(int rst, ProductDetailResult result) {
                if (result != null) {
                    showLog("getProductDetails: getRequestId=" + result.getRequestId() + " rstCode=" + rst);
                    List<ProductDetail> productDetailsLst = result.getProductList();
                    if (productDetailsLst != null) {
                        for (int i=0; i<productDetailsLst.size(); i++) {
                            ProductDetail detail = productDetailsLst.get(i);
                            showLog("getProductDetails: No=" + detail.getProductNo()
                                    + "\n Title=" + detail.getProductName()
                                    + "\n Desc=" + detail.getProductDesc()
                                    + "\n Price=" + detail.getPrice() + "\n");
                        }
                    }
                    List<ProductFailObject> productDetailsFailLst = result.getFailList();
                    if (productDetailsFailLst != null) {
                        for (int i=0; i<productDetailsFailLst.size(); i++) {
                            ProductFailObject detailFail = productDetailsFailLst.get(i);
                            showLog("getProductDetailsFail: No=" + detailFail.getProductNo() + "  rstCode=" + detailFail.getCode()+"  msg=" + detailFail.getMsg() + "\n");
                        }
                    }
                } else {
                    showLog("getProductDetails: error=" + rst + "\n");
                }
            }
        });
    }

    /**
     * 查询未完成订单示例 | Query Incomplete Orders Sample
     */
    private void checkPay() {

        // 取出所有未确认订单 | Remove all unacknowledged orders
        SharedPreferences sp = getSharedPreferences(UNCHECK_PAYREQUESTID_FILE, 0);
        Map<String, ?> allUnCheckedPays = sp.getAll();
        Set<? extends Map.Entry<String, ?>> setAllUnChecked = allUnCheckedPays.entrySet();

        boolean hasUnConfirmedPayRecorder = false;

        // 循环查询 | Circular Query
        for (Map.Entry<String, ?> ele : setAllUnChecked) {
            if (ele == null) {
                continue;
            }

            Object valueObj = ele.getValue();
            if (valueObj != null && valueObj instanceof Boolean) {
                Boolean valueBoolean = (Boolean) valueObj;
                if (!valueBoolean) {
                    String reqId = ele.getKey();
                    getPayDetail(reqId);
                    hasUnConfirmedPayRecorder = true;
                }
            }
        }

        if (!hasUnConfirmedPayRecorder) {
            showLog("checkPay: no pay to check");
        }
    }

    private void getPurchaseInfo(final long pageNo){
        final PurchaseInfoRequest req = new PurchaseInfoRequest();
        req.setAppId(IEvnValues.appId);
        req.setMerchantId(IEvnValues.cpId);
        req.setPriceType("3");
        req.setPageNo(pageNo);
        req.setTs(System.currentTimeMillis());

        //对单机应用可以直接调用此方法对请求信息签名，非单机应用一定要在服务器端储存签名私钥，并在服务器端进行签名操作。| For stand-alone applications, this method can be called directly to the request information signature, not stand-alone application must store the signature private key on the server side, and sign operation on the server side.
        //在服务端进行签名的cp可以将getStringForSign返回的待签名字符串传给服务端进行签名 | The CP, signed on the server side, can pass the pending signature string returned by Getstringforsign to the service side for signature
        req.setSign(PaySignUtil.rsaSign(PaySignUtil.getStringForSign(req), pay_priv_key));
        HMSAgent.Pay.getPurchaseInfo(req, new GetPurchaseInfoHandler() {
            @Override
            public void onResult(int rst, PurchaseInfoResult result) {
                if (result != null) {
                    if (rst == HMSAgent.AgentResultCode.HMSAGENT_SUCCESS) {
                        boolean checkSign = PaySignUtil.checkSign(result, pay_pub_key);
                        showLog("getPurchaseInfo: pageNo="+pageNo+" checkSign=" + checkSign);

                        if (checkSign) {
                            List<PurchaseInfo> purchaseInfos = result.getPurchaseInfoList();
                            if (purchaseInfos != null) {
                                for (PurchaseInfo info : purchaseInfos) {
                                    String requestId = info.getRequestId();
                                    String appid = info.getAppId();
                                    String cpid = info.getMerchantId();
                                    String productId = info.getProductId();
                                    String tradeTime = info.getTradeTime();

                                    showLog("getPurchaseInfo: tradeTime=" + tradeTime
                                            + "\n\t productId=" + productId
                                            + "\n\t cpid=" + cpid
                                            + "\n\t appid=" + appid
                                            + "\n\t requestId=" + requestId);
                                }
                            }

                            long pageCount = result.getPageCount();
                            if (pageNo < pageCount) {
                                // 请求下一页数据
                                getPurchaseInfo(pageNo+1);
                            }
                        } else {
                            // 验签失败，请检查公钥是否正确。如果确认无误，请联系华为方支持
                        }
                    } else {
                        showLog("getPurchaseInfo: pageNo="+pageNo+" result=null code=" + rst + " errMsg=" + result.getStatus().getStatusMessage());
                    }
                } else {
                    showLog("getPurchaseInfo: pageNo="+pageNo+" result=null code=" + rst);
                }
            }
        });
    }

    /**
     * 查询支付订单状态 | Query Payment order Status
     * @param reqId 要查询的商品订单号 | Product order number to query
     */
    private void getPayDetail(final String reqId) {
        OrderRequest or = new OrderRequest();

        showLog("checkPay: begin=" + reqId);
        or.setRequestId(reqId);
        or.setTime(String.valueOf(System.currentTimeMillis()));
        or.setKeyType("1");
        or.setMerchantId(cpId);

        //对查询订单请求信息进行签名,建议CP在服务器端储存签名私钥，并在服务器端进行签名操作。| To sign the query order request information, it is recommended that CP store the signature private key on the server side and sign the operation on the server side.
        //在服务端进行签名的cp可以将getStringForSign返回的待签名字符串传给服务端进行签名 | The CP, signed on the server side, can pass the pending signature string returned by Getstringforsign to the service side for signature
        or.sign = PaySignUtil.rsaSign(PaySignUtil.getStringForSign(or), pay_priv_key);
        HMSAgent.Pay.getOrderDetail(or, new GetOrderHandler() {
            @Override
            public void onResult(int retCode, OrderResult checkPayResult) {
                showLog("checkPay: requId="+reqId+"  retCode=" + retCode);
                if (checkPayResult != null && checkPayResult.getReturnCode() == retCode) {
                    // 处理支付业务返回码 | Processing Payment Business return code
                    if (retCode == HMSAgent.AgentResultCode.HMSAGENT_SUCCESS) {
                        boolean checkRst = PaySignUtil.checkSign(checkPayResult, pay_pub_key);
                        if (checkRst) {
                            // 支付成功，发放对应商品 | Pay success, distribute the corresponding goods
                            showLog("checkPay: Pay successfully, distribution of goods");
                        } else {
                            // 验签失败，当支付失败处理 | Verification fails when payment fails to deal with
                            showLog("checkPay: Failed to verify signature, pay failed");
                        }

                        // 不需要再查询 | No more queries
                        removeCacheRequestId(checkPayResult.getRequestId());
                    } else if (retCode == PayStatusCodes.ORDER_STATUS_HANDLING
                            || retCode == PayStatusCodes.ORDER_STATUS_UNTREATED
                            || retCode == PayStatusCodes.PAY_STATE_TIME_OUT) {
                        // 未处理完，需要重新查询。如30分钟后再次查询。超过24小时当支付失败处理 | Not finished processing, you need to requery. such as 30 minutes after the query again. More than 24 hours when payment fails to handle
                        showLog("checkPay: Pay failed. errorCode="+retCode+"  errMsg=" + checkPayResult.getReturnDesc());
                    } else if (retCode == PayStatusCodes.PAY_STATE_NET_ERROR) {
                        // 网络失败，需要重新查询 | Network failure, need to Requery
                        showLog("checkPay: A network problem caused the payment to fail. errorCode="+retCode+"  errMsg=" + checkPayResult.getReturnDesc());
                    } else {
                        // 支付失败，不需要再查询 | Payment failed, no more queries required
                        showLog("checkPay: Pay failed. errorCode="+retCode+"  errMsg=" + checkPayResult.getReturnDesc());
                        removeCacheRequestId(reqId);
                    }
                } else {
                    // 没有结果回来，需要重新查询。如30分钟后再次查询。超过24小时当支付失败处理 | No results back, you need to requery. such as 30 minutes after the query again. More than 24 hours when payment fails to handle
                    showLog("checkPay: Pay failed. errorCode="+retCode);
                }
            }
        });
    }

    private void addRequestIdToCache(String requestId) {
        SharedPreferences sp = getSharedPreferences("pay_request_ids", 0);
        sp.edit().putBoolean(requestId, false).commit();
    }

    private void removeCacheRequestId(String reqId) {
        SharedPreferences sp = getSharedPreferences("pay_request_ids", 0);
        sp.edit().remove(reqId).commit();
    }

    /**
     * 创建普通支付请求对象 | Create an ordinary Payment request object
     * @param totalAmount 要支付的金额 | Amount to pay
     * @return 普通支付请求对象 | Ordinary Payment Request Object
     */
    private PayReq createPayReq(float totalAmount) {
        PayReq payReq = new PayReq();

        /**
         * 生成requestId | Generate RequestID
         */
        DateFormat format = new java.text.SimpleDateFormat("yyyyMMddhhmmssSSS");
        int random= new SecureRandom().nextInt() % 100000;
        random = random < 0 ? -random : random;
        String requestId = format.format(new Date());
        requestId = String.format("%s%05d", requestId, random);

        /**
         * 生成总金额 | Generate Total Amount
         */
        String amount = String.format("%.2f", totalAmount);

        //商品名称 | Product Name
        payReq.productName = "test product";
        //商品描述 | Product Description
        payReq.productDesc = "test product description";
        // 商户ID，来源于开发者联盟，也叫“支付id” | Merchant ID, from the Developer Alliance, also known as "Payment ID"
        payReq.merchantId = cpId;
        // 应用ID，来源于开发者联盟 | Application ID, from the Developer Alliance
        payReq.applicationID = appId;
        // 支付金额 | Amount paid
        payReq.amount = amount;
        // 支付订单号 | Payment order Number
        payReq.requestId = requestId;
        // 国家码 | Country code
        payReq.country = "CN";
        //币种 | Currency
        payReq.currency = "CNY";
        // 渠道号 | Channel number
        payReq.sdkChannel = 1;
        // 回调接口版本号 | Callback Interface Version number
        payReq.urlVer = "2";

        // 商户名称，必填，不参与签名。会显示在支付结果页面 | Merchant name, must be filled out, do not participate in the signature. will appear on the Pay results page
        payReq.merchantName = "XXX Company (set as your company name here)";
        //分类，必填，不参与签名。该字段会影响风控策略 | Categories, required, do not participate in the signature. This field affects wind control policy
        // X4：主题,X5：应用商店,	X6：游戏,X7：天际通,X8：云空间,X9：电子书,X10：华为学习,X11：音乐,X12 视频, | X4: Theme, X5: App Store, X6: Games, X7: Sky Pass, X8: Cloud Space, X9: ebook, X10: Huawei Learning, X11: Music, X12 video,
        // X31 话费充值,X32 机票/酒店,X33 电影票,X34 团购,X35 手机预购,X36 公共缴费,X39 流量充值 | X31, X32 air tickets/hotels, X33 movie tickets, X34 Group purchase, X35 mobile phone advance, X36 public fees, X39 flow Recharge
        payReq.serviceCatalog = "X6";
        //商户保留信息，选填不参与签名，支付成功后会华为支付平台会原样 回调CP服务端 | The merchant retains the information, chooses not to participate in the signature, the payment will be successful, the Huawei payment platform will be back to the CP service
        payReq.extReserved = "Here to fill in the Merchant reservation information";

        //对单机应用可以直接调用此方法对请求信息签名，非单机应用一定要在服务器端储存签名私钥，并在服务器端进行签名操作。| For stand-alone applications, this method can be called directly to the request information signature, not stand-alone application must store the signature private key on the server side, and sign operation on the server side.
        // 在服务端进行签名的cp可以将getStringForSign返回的待签名字符串传给服务端进行签名 | The CP, signed on the server side, can pass the pending signature string returned by Getstringforsign to the service side for signature
        payReq.sign = PaySignUtil.rsaSign(PaySignUtil.getStringForSign(payReq), pay_priv_key);

        return payReq;
    }

    /**
     * 创建pms支付的请求对象 | Create a Request object for PMS payments
     * @param productNo 商品编码 | Product Code
     * @return pms支付请求对象 | PMS Payment Request Object
     */
    private ProductPayRequest createProductPayReq(String productNo) {
        ProductPayRequest payReq = new ProductPayRequest();

        /**
         * 生成requestId | Generate RequestID
         */
        DateFormat format = new java.text.SimpleDateFormat("yyyyMMddhhmmssSSS");
        int random= new SecureRandom().nextInt() % 100000;
        random = random < 0 ? -random : random;
        String requestId = format.format(new Date());
        requestId = String.format("%s%05d", requestId, random);

        // 商户ID，来源于开发者联盟，也叫“支付id”、“cpid” | Merchant ID, from the Developer Alliance, also known as "Payment ID", "cpid"
        payReq.merchantId = cpId;
        // 应用ID，来源于开发者联盟 | Application ID, from the Developer Alliance
        payReq.applicationID = appId;

        // 商品编号 | productNo
        payReq.productNo = productNo;

        // 支付订单号 | requestId
        payReq.requestId = requestId;
        // 渠道号 | sdkChannel
        payReq.sdkChannel = 1;
        // 回调接口版本号 | Callback Interface Version number
        payReq.urlVer = "2";

        // 商户名称，必填，不参与签名。会显示在支付结果页面 | Merchant name, must be filled out, do not participate in the signature. will appear on the Pay results page
        payReq.merchantName = "XXX Company (set as your company name here)";
        //分类，必填，不参与签名。该字段会影响风控策略 | Categories, required, do not participate in the signature. This field affects wind control policy
        // X4：主题,X5：应用商店,	X6：游戏,X7：天际通,X8：云空间,X9：电子书,X10：华为学习,X11：音乐,X12 视频, | X4: Theme, X5: App Store, X6: Games, X7: Sky Pass, X8: Cloud Space, X9: ebook, X10: Huawei Learning, X11: Music, X12 video,
        // X31 话费充值,X32 机票/酒店,X33 电影票,X34 团购,X35 手机预购,X36 公共缴费,X39 流量充值 | X31, X32 air tickets/hotels, X33 movie tickets, X34 Group purchase, X35 mobile phone advance, X36 public fees, X39 flow Recharge
        payReq.serviceCatalog = "X5";
        //商户保留信息，选填不参与签名，支付成功后会华为支付平台会原样 回调CP服务端 | The merchant retains the information, chooses not to participate in the signature, the payment will be successful, the Huawei payment platform will be back to the CP service
        payReq.extReserved = "Here to fill in the Merchant reservation information";

        //对单机应用可以直接调用此方法对请求信息签名，非单机应用一定要在服务器端储存签名私钥，并在服务器端进行签名操作。| For stand-alone applications, this method can be called directly to the request information signature, not stand-alone application must store the signature private key on the server side, and sign operation on the server side.
        //在服务端进行签名的cp可以将getStringForSign返回的待签名字符串传给服务端进行签名 | The CP, signed on the server side, can pass the pending signature string returned by Getstringforsign to the service side for signature
        payReq.sign = PaySignUtil.rsaSign(PaySignUtil.getStringForSign(payReq), pay_priv_key);

        return payReq;
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_iap) {
            // 本页面切换到自身的按钮事件不需要处理 | "This page switches to itself" button event does not need to be handled
            return;
        } else if (!onTabBtnClickListener(id)) {
            // 如果不是tab切换按钮则处理业务按钮事件 | Handle Business button events without the TAB toggle button
//            switch (id) {
//                case R.id.btn_pay:
//                    pay();
//                    break;
//                case R.id.btn_checkpay:
//                    checkPay();
//                    break;
//                case R.id.btn_getallproductdetail:
//                    getProductDetail("PmsTestProduct0001|PmsTestProduct0002|PmsTestProduct0003|PmsTestProduct0004|PmsTestProduct0005|PmsTestProduct0006");
//                    break;
//                case R.id.btn_purchaseinfo:
//                    getPurchaseInfo(1);
//                    break;
//                default:
//            }
        }
    }
}
