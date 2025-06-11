package uk.gov.justice.laa.dstew.access.transformation;

import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;

/**
 * Assume that all response transformers care about user roles.
 *
 * @param <T> The response type
 */
interface ResponseTransformer<T> {
  T transform(T response, Collection<? extends GrantedAuthority> authorities);
}
