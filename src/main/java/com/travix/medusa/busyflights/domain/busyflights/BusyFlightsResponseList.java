package com.travix.medusa.busyflights.domain.busyflights;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class BusyFlightsResponseList {

    private List<BusyFlightsResponse> data;

}
