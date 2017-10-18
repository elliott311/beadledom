package com.cerner.beadledom.json.pagination.service;

import com.cerner.beadledom.jaxrs.GenericResponse;
import com.cerner.beadledom.json.pagination.annotations.PaginationLimit;
import com.cerner.beadledom.json.pagination.annotations.PaginationOffset;
import com.cerner.beadledom.json.pagination.model.OffsetPaginatedList;

import com.wordnik.swagger.annotations.ApiParam;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

public class OffsetPaginationLinkTestMethods {

  public GenericResponse<OffsetPaginatedList<Object>> paginatedListDefaults(
      @ApiParam(value = "Unique reference to a System")
      @PathParam(value = "system") String system,
      @QueryParam(value = "offset") @DefaultValue(value = "0") Integer offset,
      @QueryParam(value = "limit") @DefaultValue(value = "20") Integer limit) {

    return null;
  }

  public GenericResponse<OffsetPaginatedList<Object>> paginatedListOverrideOffset(
      @ApiParam(value = "Unique reference to a System")
      @PathParam(value = "system") String system,
      @PaginationOffset @QueryParam(value = "start") @DefaultValue(value = "0") Integer start,
      @QueryParam(value = "limit") @DefaultValue(value = "20") Integer limit) {

    return null;
  }

  public GenericResponse<OffsetPaginatedList<Object>> paginatedListOverrideLimit(
      @ApiParam(value = "Unique reference to a System")
      @PathParam(value = "system") String system,
      @QueryParam(value = "offset") @DefaultValue(value = "0") Integer offset,
      @PaginationLimit @QueryParam(value = "pagesize") @DefaultValue(value = "20") Integer size) {

    return null;
  }
}
