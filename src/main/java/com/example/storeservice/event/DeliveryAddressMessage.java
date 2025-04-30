package com.example.storeservice.event;

public record DeliveryAddressMessage(
        String addressStart,
        Double addressStartLat,
        Double addressStartLan,
        String addressDestination,
        Double addressDestinationLat,
        Double addressDestinationLan
) {}
