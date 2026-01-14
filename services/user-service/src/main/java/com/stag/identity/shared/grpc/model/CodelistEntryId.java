package com.stag.identity.shared.grpc.model;

/// **Codelist Entry Identifier**
///
/// Composite key for identifying a codelist entry by domain and low value (code).
/// Used as a map key for storing and retrieving localized codelist meanings from the codelist-service.
///
/// @param domain Codelist domain
/// @param lowValue Codelist low value (code)
///
/// @author Jakub Pavlíček
/// @version 1.0.0
public record CodelistEntryId(
    String domain,
    String lowValue
) {

}
