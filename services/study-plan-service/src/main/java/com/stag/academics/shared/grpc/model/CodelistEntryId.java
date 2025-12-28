package com.stag.academics.shared.grpc.model;

/// **Codelist Entry ID**
///
/// Composite identifier for codelist entries combining domain and low value.
/// Used as a map key for codelist meaning lookups.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
public record CodelistEntryId(
    String domain,
    String lowValue
) {

}
