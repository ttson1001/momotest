package test.momo.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import test.momo.demo.config.Environment;
import test.momo.demo.enums.RequestType;
import test.momo.demo.models.PaymentResponse;
import test.momo.demo.models.Response;
import test.momo.demo.processor.CreateOrderMoMo;
import test.momo.demo.shared.utils.LogUtils;

@RestController
public class momo {

    @PostMapping("/create")
    public ResponseEntity<String> aBoolean (){
        LogUtils.init();
        String requestId = String.valueOf(System.currentTimeMillis());
        String orderId = String.valueOf(System.currentTimeMillis());
        Long transId = 2L;
        long amount = 50000;

        String partnerClientId = "partnerClientId";
        String orderInfo = "Pay With MoMo";
        String returnURL = "https://google.com.vn";
        String notifyURL = "https://google.com.vn";
        String callbackToken = "callbackToken";
        String token = "";

        Environment environment = Environment.selectEnv("dev");


//      Remember to change the IDs at enviroment.properties file

        /***
         * create payment with capture momo wallet
         */
        PaymentResponse captureWalletMoMoResponse  = null;
        try {
            captureWalletMoMoResponse = CreateOrderMoMo.process(environment, orderId, requestId, Long.toString(amount), orderInfo, returnURL, notifyURL, "", RequestType.CAPTURE_WALLET, null);
        } catch (Exception e) {
             e.printStackTrace();
        }

        return ResponseEntity.ok().body(captureWalletMoMoResponse.getPayUrl());
    }
}
