package com.travix.medusa.busyflights.utils;

import com.travix.medusa.busyflights.enums.Supplier;
import com.travix.medusa.busyflights.properties.FindFlightsProperties;
import com.travix.medusa.busyflights.properties.SupplierProperties;

import java.util.HashMap;
import java.util.Map;

public class TestUtils {

    public static FindFlightsProperties getFindFlightsPropertiesForSupplier(Supplier supplier, int port) {
        Map<Supplier, SupplierProperties> suppliers = new HashMap<>();
        suppliers.put(supplier, new SupplierProperties(String.format("http://localhost:%s", port)));
        return new FindFlightsProperties(suppliers);
    }
}
