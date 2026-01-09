package com.stag.identity.person.model;

/// **Address Type Enum**
///
/// Address classification for permanent and temporary addresses in the Czech Republic and foreign countries.
/// Used for address categorization and routing logic.
///
/// @author Jakub Pavlíček
/// @version 1.0.0
public enum AddressType {
    /// Permanent address
    PERMANENT,
    /// Temporary address
    TEMPORARY,
    /// Foreign permanent address
    FOREIGN_PERMANENT,
    /// Foreign temporary address
    FOREIGN_TEMPORARY
}
