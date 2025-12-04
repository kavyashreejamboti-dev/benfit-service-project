package com.spendit.benfit_service_project.service;

import predef_java.UserId;

public record OperationError(UserId userId, String code, String message) { }
