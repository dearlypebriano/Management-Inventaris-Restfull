package com.management.ManagementInventaris.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class BussinesException extends RuntimeException {

    private final String msg;
}