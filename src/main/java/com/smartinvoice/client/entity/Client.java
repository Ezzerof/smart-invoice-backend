package com.smartinvoice.client.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.smartinvoice.invoice.entity.Invoice;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "clients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String companyName;
    private String address;
    private String city;
    private String country;
    private String postcode;
    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Invoice> invoices;
}
