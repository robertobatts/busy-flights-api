package com.travix.medusa.busyflights.properties;

import com.travix.medusa.busyflights.enums.Supplier;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import java.util.Map;

@Getter
@AllArgsConstructor
@ConstructorBinding
@ConfigurationProperties("find-flights")
public class FindFlightsProperties {
    private Map<Supplier, SupplierProperties> suppliers;

    public SupplierProperties getSupplierProperties(Supplier supplier) {
        return suppliers.get(supplier);
    }
}
