package org.springframework.samples.petclinic.security;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * See <a href="https://docs.spring.io/spring-security/site/docs/5.2.x/reference/html/authorization.html#method-security-meta-annotations">
 * <b>Method Security Meta Annotations</b></a>
 */
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasRole(@roles.VET_ADMIN)")
public @interface HasRoleVetAdmin {
}
