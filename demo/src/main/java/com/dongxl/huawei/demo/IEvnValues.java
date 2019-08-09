package com.dongxl.huawei.demo;

/**
 * 环境变量配置，私钥需要放在服务器端，这里只是给个处理示例 | environment variable configuration, the private key needs to be placed on the server side, here is just a sample processing
 */
public interface IEvnValues {
    String game_priv_key = "xxx very long game private key xxx";
    String game_public_key = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQD2zL2j7JsygP6k+0FX5ewrHmw1puikjhbdX1t2GWFAwjOiW4u+ycmvKvPs4ETjba1i7+M35nkiEI3wE2TP+GfMJLcE+5txkJ0sEOqIuvsYAgyZLwf64AoPcgQN50BZO8GFXuHmOG+8Z4nUa2A3/vvMHGWlVOo5ujkoTLj5j0tNIQIDAQAB";

    String pay_priv_key = "xxx very long pay private key xxx";
    String pay_pub_key = "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAIL/7zgG9KCjm5IeGFzq8oVaxCqFJ9+m/3rGMXU2p9K+bHLPR1m3c9TMZRGjkZbTZ0G/VLPO6BxiP+w+VM+Z3fECAwEAAQ==";

    String appId = "100579575";
    String cpId = "10086000000000293";
}
