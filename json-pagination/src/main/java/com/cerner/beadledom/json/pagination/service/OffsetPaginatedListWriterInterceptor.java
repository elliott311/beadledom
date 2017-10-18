package com.cerner.beadledom.json.pagination.service;

import com.cerner.beadledom.json.pagination.annotations.PaginationLimit;
import com.cerner.beadledom.json.pagination.annotations.PaginationOffset;
import com.cerner.beadledom.json.pagination.model.OffsetPaginatedList;
import com.cerner.beadledom.json.pagination.model.OffsetPaginatedListDto;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;

/**
 * A {@link WriterInterceptor} for adding offset pagination links for {@link OffsetPaginatedList}
 * prior to serialization.
 */

@Provider
public class OffsetPaginatedListWriterInterceptor implements WriterInterceptor {
  @Context
  UriInfo uriInfo;
  @Context
  ResourceInfo resourceInfo;
  private Long offsetDefault = 0L;
  private Long limitDefault = 20L;
  private String offsetParamName = "offset";
  private String limitParamName = "limit";

  @Override
  public void aroundWriteTo(WriterInterceptorContext context)
      throws IOException, WebApplicationException {

    if (context.getEntity() instanceof OffsetPaginatedList) {
      final Method resourceMethod = resourceInfo.getResourceMethod();

      setOffsetParamName(resourceMethod.getParameters());
      setLimitParamName(resourceMethod.getParameters());
      setOffsetAndLimitDefaults(resourceMethod);

      OffsetPaginatedList list = (OffsetPaginatedList) context.getEntity();
      OffsetPaginationLinks links = OffsetPaginationLinks
          .create(
              list, uriInfo, offsetDefault.intValue(), limitDefault.intValue(), offsetParamName,
              limitParamName);

      @SuppressWarnings("unchecked")
      OffsetPaginatedListDto listWithLinks = OffsetPaginatedListDto.builder()
          .items(list.items())
          .hasMore(list.hasMore())
          .totalResults(list.totalResults())
          .firstLink(links.firstLink())
          .lastLink(links.lastLink())
          .prevLink(links.prevLink())
          .nextLink(links.nextLink())
          .build();

      context.setEntity(listWithLinks);
    }

    context.proceed();
  }

  protected void setOffsetParamName(Parameter[] parameters) {
    for (Parameter parameter : parameters) {
      final Annotation offsetAnnotation = parameter.getAnnotation(PaginationOffset.class);
      if (offsetAnnotation != null) {
        if (parameter.getAnnotation(QueryParam.class) != null) {
          offsetParamName =
              ((String) ((QueryParam) parameter.getAnnotation(QueryParam.class)).value());
        }
      }
    }
  }

  protected void setLimitParamName(Parameter[] parameters) {
    for (Parameter parameter : parameters) {
      final Annotation limitAnnotation = parameter.getAnnotation(PaginationLimit.class);
      if (limitAnnotation != null) {
        if (parameter.getAnnotation(QueryParam.class) != null) {
          limitParamName =
              ((String) ((QueryParam) parameter.getAnnotation(QueryParam.class)).value());
        }
      }
    }
  }

  protected void setOffsetAndLimitDefaults(Method resourceMethod) {

    // Find default values for offset and limit based on @DefaultValue annotation.
    for (Annotation[] paramAnnotations : resourceMethod.getParameterAnnotations()) {
      for (Annotation paramAnnotation : paramAnnotations) {
        if (paramAnnotation instanceof QueryParam) {
          final String queryParamName = ((String) ((QueryParam) paramAnnotation).value());

          if (queryParamName.equalsIgnoreCase(offsetParamName)) {
            offsetDefault = getDefault(paramAnnotations);
          } else if (queryParamName.equalsIgnoreCase(limitParamName)) {
            limitDefault = getDefault(paramAnnotations);
          }
        }
      }
    }
  }

  protected Long getDefault(Annotation[] paramAnnotations) {
    for (Annotation annotation : paramAnnotations) {
      if (annotation instanceof DefaultValue) {
        String value = ((String) ((DefaultValue) annotation).value());
        return Long.parseLong(value);
      }
    }
    return null;
  }
}
