package com.amigoscode.testing.customer;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.Valid;

public class CustomerRegistrationRequest {

    private final Customer customer;

    public CustomerRegistrationRequest(
            @Valid @JsonProperty("customer") Customer customer) {
        this.customer = customer;
    }

    public Customer getCustomer() {
        return customer;
    }

    @Override
    public String toString() {
        return "CustomerRegistrationRequest{" +
                "customer=" + customer +
                '}';
    }

}
