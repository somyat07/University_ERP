package edu.univ.erp.service;


public enum RegistrationResult {
    SUCCESS,
    SECTION_FULL,
    ALREADY_REGISTERED,
    SECTION_NOT_FOUND,
    STUDENT_NOT_FOUND,
    MAINTENANCE_ON,
    DEADLINE_PASSED, // <---
    UNKNOWN_ERROR
}