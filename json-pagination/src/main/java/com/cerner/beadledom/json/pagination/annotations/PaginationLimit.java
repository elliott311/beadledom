package com.cerner.beadledom.json.pagination.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to indicate that the parameter annotated should be used as the limit parameter
 * when calculating pagination links.  The intent is to allow the consumer to name their limit
 * parameter something other than "limit" and still be able to paginate using that parameter.
 * This annotation is NOT required if the consumer uses "limit" as the limit parameter name.
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface PaginationLimit {
}