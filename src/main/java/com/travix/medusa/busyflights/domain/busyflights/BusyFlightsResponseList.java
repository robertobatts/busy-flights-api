package com.travix.medusa.busyflights.domain.busyflights;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Data
@Builder
@Jacksonized
public class BusyFlightsResponseList {

    private List<BusyFlightsResponse> data;

}
