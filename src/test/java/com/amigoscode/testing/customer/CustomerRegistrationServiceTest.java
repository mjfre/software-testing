package com.amigoscode.testing.customer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

class CustomerRegistrationServiceTest {

    @Mock
    private CustomerRepository customerRepository;
    private CustomerRegistrationService underTest;

    @Captor
    private ArgumentCaptor<Customer> customerArgumentCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        underTest = new CustomerRegistrationService(customerRepository);
    }

    @Test
    void itShouldSaveNewCustomer() {
        //Given
        String phoneNumber = "000099";
        Customer customer = new Customer(UUID.randomUUID(), "Marion", phoneNumber);

        //... a request
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer);

        //... no customer with phone number
        given(customerRepository.selectCustomerByPhoneNumbers(phoneNumber)).willReturn(Optional.empty());

        //When
        underTest.registerNewCustomer(request);

        //Then
        then(customerRepository).should().save(customerArgumentCaptor.capture());
        Customer customerArgumentCaptorValue = customerArgumentCaptor.getValue();
        assertThat(customerArgumentCaptorValue).isEqualToComparingFieldByField(customer);
    }

    @Test
    void itShouldSaveNewCustomerWhenIdIsNull() {
        //Given
        String phoneNumber = "000099";
        Customer customer = new Customer(null, "Marion", phoneNumber);

        //... a request
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer);

        //... no customer with phone number
        given(customerRepository.selectCustomerByPhoneNumbers(phoneNumber)).willReturn(Optional.empty());

        //When
        underTest.registerNewCustomer(request);

        //Then
        then(customerRepository).should().save(customerArgumentCaptor.capture());
        Customer customerArgumentCaptorValue = customerArgumentCaptor.getValue();
        assertThat(customerArgumentCaptorValue)
                .isEqualToIgnoringGivenFields(customer, "id");
        assertThat(customerArgumentCaptorValue.getId()).isNotNull();
    }

    @Test
    void itShouldNotSaveCustomerWhenCustomerExists() {
        //Given
        String phoneNumber = "000099";
        UUID id = UUID.randomUUID();
        Customer customer = new Customer(id, "Marion", phoneNumber);

        //... a request
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer);

        //... no customer with phone number
        given(customerRepository.selectCustomerByPhoneNumbers(phoneNumber))
                .willReturn(Optional.of(customer));

        //When
        underTest.registerNewCustomer(request);

        //Then
        then(customerRepository).should(never()).save(any());
        then(customerRepository).should().selectCustomerByPhoneNumbers(phoneNumber);
        then(customerRepository).shouldHaveNoMoreInteractions();
    }

    @Test
    void itShouldThrowWhenPhoneNumberIsTaken() {
        //Given
        String phoneNumber = "000099";
        UUID id = UUID.randomUUID();
        Customer customer = new Customer(id, "Marion", phoneNumber);
        Customer customer2 = new Customer(
                UUID.randomUUID(),
                "Steve",
                "000099"
        );

        //... a request
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(customer);

        //... found customer with phone number

        given(customerRepository.selectCustomerByPhoneNumbers(phoneNumber))
                .willReturn(Optional.of(customer2));

        //When
        //Then
        assertThatThrownBy(() -> underTest.registerNewCustomer(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(String.format("phone number [%s] is taken", phoneNumber));

        //Finally
        then(customerRepository).should(never()).save(any(Customer.class));
    }
}