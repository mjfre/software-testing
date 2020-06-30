package com.amigoscode.testing.customer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class CustomerRegistrationService {

    private final CustomerRepository customerRepository;

    @Autowired
    public CustomerRegistrationService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public void registerNewCustomer(CustomerRegistrationRequest request) {

        Customer current = request.getCustomer();
        //if phone number is taken

        Optional<Customer> optionalCustomer = customerRepository.selectCustomerByPhoneNumbers(current.getPhoneNumber());
        if (optionalCustomer.isPresent()) {
            if (current.getId().equals(optionalCustomer.get().getId())) {
                return;
            }
            throw new IllegalStateException(String.format("phone number [%s] is taken", current.getPhoneNumber()));
        }

        if(request.getCustomer().getId() == null){
            request.getCustomer().setId(UUID.randomUUID());
        }

        request.getCustomer().getId();
        customerRepository.save(current);

    }


}
