package com.spendit.benfit_service_project.service;

import predef_java.UserId;
import java.util.List;
import java.util.Set;

public record UnassignResult(Set<UserId> success, List<OperationError> errors) { }
