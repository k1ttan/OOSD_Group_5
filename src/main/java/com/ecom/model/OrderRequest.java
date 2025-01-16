package com.ecom.model;

import java.security.KeyStore.PrivateKeyEntry;

import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class OrderRequest {

	private UserDtls user;
	
	@ManyToOne
	private Ticket ticket;
	
	private int quantity;
	
	private String firstName;

	private String lastName;

	private String email;

	private String mobileNo;

	private String address;

	private String city;

	private String state;

	private String pincode;

	private String paymentType;

}
