package com.trustai.userservice.user.dto;

import com.trustai.userservice.user.entity.Kyc;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class KycDocumentRequest {
    Kyc.KycDocumentType documentType;
    private String documentLink;
}
