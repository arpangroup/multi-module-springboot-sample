package com.trustai.userservice.user.provider;//package com.trustai.userservice.user.provider;
//
//import com.trustai.common.client.TransactionClient;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//
//import java.math.BigDecimal;
//
//@Service
//public class RemoteTransactionClient implements TransactionClient {
//    private final RestTemplate restTemplate;
//
//
//    public RemoteTransactionClient(RestTemplate restTemplate) {
//        this.restTemplate = restTemplate;
//    }
//
//    @Override
//    public BigDecimal getDepositBalance(Long userId) {
//        return restTemplate.getForObject("http://transaction-service/balance/" + userId, BigDecimal.class);
//    }
//}
