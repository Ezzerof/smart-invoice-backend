package com.smartinvoice.company;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "invoice.company")
public class CompanyProperties {
    private String name;
    private String address;
    private String city;
    private String country;
    private String postCode;
    private String phone;
    private String email;
    private String website;
    private BankDetails bank;

    @Getter
    @Setter
    public static class BankDetails {
        private String holder;
        private String account;
        private String sortCode;

    }
}
