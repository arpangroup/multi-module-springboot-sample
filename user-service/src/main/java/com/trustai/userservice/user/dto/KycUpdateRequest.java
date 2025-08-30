package com.trustai.userservice.user.dto;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class KycUpdateRequest {
    private String email;
    private String phone;
    private String address;

    private String birthdate;
    private String gender;

    private List<KycDocumentRequest> documents;
}
