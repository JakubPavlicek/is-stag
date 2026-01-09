package com.stag.identity.shared.grpc.model;

/// **Codelist Domain Enum**
///
/// Enumeration of all codelist domains used in the user service for looking up localized meanings.
/// Maps to Oracle database CISA_DOMENY table entries.
/// Used for person titles, gender, marital status, citizenship, and banking.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
public enum CodelistDomain {
    /// Academic title prefix (e.g., "Ing.", "Dr.")
    TITUL_PRED,
    /// Academic title suffix (e.g., "Ph.D.", "CSc.")
    TITUL_ZA,
    /// Gender (male/female)
    POHLAVI,
    /// Marital status
    STAV,
    /// Citizenship qualifier
    KVANT_OBCAN,
    /// Czech bank codes
    CIS_BANK,
    /// Euro account bank codes
    CIS_BANK_EURO
}
