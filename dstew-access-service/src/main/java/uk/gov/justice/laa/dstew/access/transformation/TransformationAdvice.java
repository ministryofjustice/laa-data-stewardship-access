package uk.gov.justice.laa.dstew.access.transformation;

import static uk.gov.justice.laa.dstew.access.shared.security.SecurityUtils.getAuthorities;

import java.util.Collection;
import java.util.List;
import org.springframework.core.MethodParameter;
import org.springframework.core.ResolvableType;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * Intercept controller responses and apply cross-cutting transformations,
 * such as redaction or anonymization (for GDPR or any other reason).
 */
@RestControllerAdvice
class TransformationAdvice implements ResponseBodyAdvice<Object> {
  private final TransformerRegistry transformerRegistry;

  TransformationAdvice(final TransformerRegistry transformerRegistry) {
    this.transformerRegistry = transformerRegistry;
  }

  @Override
  public boolean supports(final MethodParameter returnType,
                          final Class<? extends HttpMessageConverter<?>> converterType) {
    return transformerRegistry.hasTransformer(returnType.getParameterType()) || supportsCollection(returnType);
  }

  private boolean supportsCollection(final MethodParameter returnType) {
    final var resolvable = ResolvableType.forMethodParameter(returnType);
    if (!Collection.class.isAssignableFrom(resolvable.toClass())) {
      return false;
    }
    final var elementType = resolvable.getGeneric(0).resolve();
    return elementType != null && transformerRegistry.hasTransformer(elementType);
  }

  @Override
  public Object beforeBodyWrite(final Object body, final MethodParameter returnType,
                                final MediaType contentType, final Class<? extends HttpMessageConverter<?>> converterType,
                                final ServerHttpRequest request, final ServerHttpResponse response) {
    if (body == null || (body instanceof List<?> list && list.isEmpty())) {
      return body;
    }
    final var authorities = getAuthorities();
    if (body instanceof List<?> list) {
      final var first = list.getFirst();
      final var maybeTransformer = transformerRegistry.getTransformer(first.getClass());
      if (maybeTransformer.isPresent()) {
        @SuppressWarnings("unchecked") final var transformer = (ResponseTransformer<Object>) maybeTransformer.get();
        return list.stream()
            .map(item -> transformer.transform(item, authorities))
            .toList();
      }
      return body;
    } else {
      return transformerRegistry.getTransformer(body.getClass())
          .map(transformer -> {
            @SuppressWarnings("unchecked")
            var objTransformer = (ResponseTransformer<Object>) transformer;
            return objTransformer.transform(body, authorities);
          })
          .orElse(body);
    }
  }
}
