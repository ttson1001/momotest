package test.momo.demo.processor;



import test.momo.demo.config.Environment;
import test.momo.demo.enums.ConfirmRequestType;
import test.momo.demo.enums.Language;
import test.momo.demo.models.ConfirmRequest;
import test.momo.demo.models.ConfirmResponse;
import test.momo.demo.models.HttpResponse;
import test.momo.demo.shared.constants.Parameter;
import test.momo.demo.shared.exception.MoMoException;
import test.momo.demo.shared.utils.Encoder;
import test.momo.demo.shared.utils.LogUtils;


public class ConfirmTransaction extends AbstractProcess<ConfirmRequest, ConfirmResponse> {
    public ConfirmTransaction(Environment environment) {
        super(environment);
    }

    public static ConfirmResponse process(Environment env, String orderId, String requestId, String amount, ConfirmRequestType requestType, String description) throws Exception {
        try {
            ConfirmTransaction m2Processor = new ConfirmTransaction(env);

            ConfirmRequest request = m2Processor.createConfirmTransactionRequest(orderId, requestId, amount, requestType, description);
            ConfirmResponse response = m2Processor.execute(request);

            return response;
        } catch (Exception exception) {
            LogUtils.error("[ConfirmTransactionProcess] "+ exception);
        }
        return null;
    }

    @Override
    public ConfirmResponse execute(ConfirmRequest request) throws MoMoException {
        try {

            String payload = getGson().toJson(request, ConfirmRequest.class);

            HttpResponse response = execute.sendToMoMo(environment.getMomoEndpoint().getConfirmUrl(), payload);

            if (response.getStatus() != 200) {
                throw new MoMoException("[ConfirmTransactionResponse] [" + request.getOrderId() + "] -> Error API");
            }

            System.out.println("uweryei7rye8wyreow8: "+ response.getData());

            ConfirmResponse confirmResponse = getGson().fromJson(response.getData(), ConfirmResponse.class);
            String responserawData =   Parameter.ORDER_ID + "=" + confirmResponse.getOrderId() +
                    "&" + Parameter.MESSAGE + "=" + confirmResponse.getMessage() +
                    "&" + Parameter.RESULT_CODE + "=" + confirmResponse.getResultCode();

            LogUtils.info("[ConfirmTransactionResponse] rawData: " + responserawData);

            return confirmResponse;

        } catch (Exception exception) {
            LogUtils.error("[ConfirmTransactionResponse] "+ exception);
            throw new IllegalArgumentException("Invalid params confirm MoMo Request");
        }
    }

    public ConfirmRequest createConfirmTransactionRequest(String orderId, String requestId, String amount, ConfirmRequestType requestType, String description) {

        try {
            String requestRawData = new StringBuilder()
                    .append(Parameter.ACCESS_KEY).append("=").append(partnerInfo.getAccessKey()).append("&")
                    .append(Parameter.AMOUNT).append("=").append(amount).append("&")
                    .append(Parameter.DESCRIPTION).append("=").append(description).append("&")
                    .append(Parameter.ORDER_ID).append("=").append(orderId).append("&")
                    .append(Parameter.PARTNER_CODE).append("=").append(partnerInfo.getPartnerCode()).append("&")
                    .append(Parameter.REQUEST_ID).append("=").append(requestId).append("&")
                    .append(Parameter.REQUEST_TYPE).append("=").append(requestType.getConfirmRequestType())
                    .toString();

            String signRequest = Encoder.signHmacSHA256(requestRawData, partnerInfo.getSecretKey());
            LogUtils.debug("[ConfirmRequest] rawData: " + requestRawData + ", [Signature] -> " + signRequest);

            return new ConfirmRequest(partnerInfo.getPartnerCode(), orderId, requestId, Language.EN, Long.valueOf(amount), "", ConfirmRequestType.CAPTURE, signRequest);
        } catch (Exception e) {
            LogUtils.error("[ConfirmResponse] "+ e);
        }

        return null;
    }
}
