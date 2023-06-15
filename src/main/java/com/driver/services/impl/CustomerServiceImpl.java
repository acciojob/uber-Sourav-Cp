package com.driver.services.impl;

import com.driver.model.*;
import com.driver.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.driver.repository.CustomerRepository;
import com.driver.repository.DriverRepository;
import com.driver.repository.TripBookingRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CustomerServiceImpl implements CustomerService {

	@Autowired
	CustomerRepository customerRepository2;

	@Autowired
	DriverRepository driverRepository2;

	@Autowired
	TripBookingRepository tripBookingRepository2;

	@Override
	public void register(Customer customer) {
		//Save the customer in database
		customerRepository2.save(customer);
	}

	@Override
	public void deleteCustomer(Integer customerId) {
		// Delete customer without using deleteById function
        Customer customer = customerRepository2.findById(customerId).get();
		customerRepository2.delete(customer);
	}

	@Override
	public TripBooking bookTrip(int customerId, String fromLocation, String toLocation, int distanceInKm) throws Exception{
		//Book the driver with lowest driverId who is free (cab available variable is Boolean.TRUE). If no driver is available, throw "No cab available!" exception
		//Avoid using SQL query
		Driver driver1 = null;
		int driverId = Integer.MAX_VALUE;
		List<Driver> driverList = driverRepository2.findAll();
		for(Driver driver : driverList) {
			if (driver.getCab().getAvailable() == true && driverId > driver.getDriverId()) {
				driver1 = driver;
			}
		}
	   if(driver1 == null) throw new Exception("No cab available!");

	   TripBooking tripBooking = new TripBooking();

	   tripBooking.setFromLocation(fromLocation);
	   tripBooking.setToLocation(toLocation);
	   tripBooking.setDistanceInKm(distanceInKm);

	   int fare = distanceInKm*10;
	   tripBooking.setBill(fare);
	   tripBooking.setStatus(TripStatus.CONFIRMED);

	   Customer customer = customerRepository2.findById(customerId).get();

	   tripBooking.setCustomer(customer);
	   tripBooking.setDriver(driver1);

	   tripBooking = tripBookingRepository2.save(tripBooking);

	   customer.getTripBookingList().add(tripBooking);
	   driver1.getTripBookingList().add(tripBooking);

	   driverRepository2.save(driver1);
	   customerRepository2.save(customer);

	   return tripBooking;

	}

	@Override
	public void cancelTrip(Integer tripId){
		//Cancel the trip having given trip Id and update TripBooking attributes accordingly
        TripBooking tripBooking = tripBookingRepository2.findById(tripId).get();
		tripBooking.setStatus(TripStatus.CANCELED);

		tripBooking.setBill(0);
		Cab cab = tripBooking.getDriver().getCab();
		cab.setAvailable(true);

		tripBookingRepository2.save(tripBooking);
	}

	@Override
	public void completeTrip(Integer tripId){
		//Complete the trip having given trip Id and update TripBooking attributes accordingly

	}
}
