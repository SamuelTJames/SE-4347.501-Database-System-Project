package com.se4347.database_system_project.api.dto;

import java.math.BigDecimal;

public record FareSummary(String code, BigDecimal amount, String restriction) {}
