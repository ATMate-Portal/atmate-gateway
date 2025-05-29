package com.atmate.portal.gateway.atmategateway.database.dto;

public record LoginResponse(String token, UserDetailsDTO user) {
}
