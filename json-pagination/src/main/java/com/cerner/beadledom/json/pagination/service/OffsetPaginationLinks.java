package com.cerner.beadledom.json.pagination.service;

import com.cerner.beadledom.json.pagination.model.OffsetPaginatedList;
import com.cerner.beadledom.json.pagination.parameters.LimitParameter;
import com.cerner.beadledom.json.pagination.parameters.OffsetParameter;

import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Given an {@link OffsetPaginatedList} this class encapsulates the pagination links for the list.
 */
class OffsetPaginationLinks {
  private final Logger logger = LoggerFactory.getLogger(OffsetPaginationLinks.class);

  private final UriInfo uriInfo;
  private final Long totalResults;
  private final Boolean hasMore;
  private final Long currentOffset;
  private final Integer currentLimit;
  private final String offsetParamName;
  private final String limitParamName;

  private OffsetPaginationLinks(
      OffsetPaginatedList list, UriInfo uriInfo, int defaultOffset, int defaultLimit,
      String offsetParamName, String limitParamName) {

    this.uriInfo = uriInfo;
    this.totalResults = list.totalResults();
    this.hasMore = list.hasMore();
    this.offsetParamName = offsetParamName;
    this.limitParamName = limitParamName;
    this.currentOffset = currentOffset(uriInfo, defaultOffset);
    this.currentLimit = currentLimit(uriInfo, defaultLimit);
  }

  /**
   * Creates a new instance of {@code OffsetPaginationLinks} for the provided list.
   *
   * @param list the list to provide links for
   * @param uriInfo the {@link UriInfo} for the current request
   * @param defaultOffset the default {@code offset} value to use for pagination links
   * @param defaultLimit the default {@code limit} value to use for pagination links
   * @param offsetParamName the parameter name to be used for offset in pagination links
   * @param limitParamName  the parameter name to be use for limit in pagination links.
   */
  public static OffsetPaginationLinks create(
      OffsetPaginatedList<?> list, UriInfo uriInfo, int defaultOffset, int defaultLimit,
      String offsetParamName, String limitParamName) {

    return new OffsetPaginationLinks(
        list, uriInfo, defaultOffset, defaultLimit, offsetParamName, limitParamName);
  }

  private Long currentOffset(UriInfo uriInfo, Integer offsetLimit) {
    String offset = uriInfo.getQueryParameters().getFirst(offsetParamName);
    return offset != null ? new OffsetParameter(offset).getValue() : offsetLimit.longValue();
  }

  private Integer currentLimit(UriInfo uriInfo, Integer defaultLimit) {
    String limit = uriInfo.getQueryParameters().getFirst(limitParamName);
    return limit != null ? new LimitParameter(limit).getValue() : defaultLimit;
  }

  /**
   * Returns the first page link.
   */
  String firstLink() {
    return urlWithUpdatedPagination(0L, currentLimit);
  }

  /**
   * Returns the last page link; null if no last page link is available.
   */
  String lastLink() {
    if (totalResults == null) {
      return null;
    }

    Long lastOffset;
    if (totalResults % currentLimit == 0L) {
      lastOffset = totalResults - currentLimit;
    } else {
      lastOffset = totalResults / currentLimit * currentLimit;
    }

    return urlWithUpdatedPagination(lastOffset, currentLimit);
  }

  /**
   * Returns the next page link; null if no next page link is available.
   */
  String nextLink() {
    if (!hasNext()) {
      return null;
    }

    return urlWithUpdatedPagination(currentOffset + currentLimit, currentLimit);
  }

  /**
   * Returns the next prev link; null if no prev page link is available.
   */
  String prevLink() {
    if (currentOffset == 0) {
      return null;
    }

    return urlWithUpdatedPagination(Math.max(0, currentOffset - currentLimit), currentLimit);
  }

  boolean hasNext() {
    boolean moreResults = totalResults != null && (currentOffset + currentLimit < totalResults);

    if (totalResults == null) {
      return hasMore != null && hasMore;
    }

    if (hasMore == null) {
      return moreResults;
    }

    if (hasMore != moreResults) {
      logger.warn(
          "Conflict between hasMore [{}] and totalResults [{}] at url [{}]; "
              + "next page link will be generated anyway.",
          hasMore, totalResults, uriInfo.getRequestUri());
    }

    return hasMore || moreResults;
  }

  private String urlWithUpdatedPagination(Long offset, Integer limit) {
    return uriInfo.getRequestUriBuilder()
        .replaceQueryParam(offsetParamName, offset)
        .replaceQueryParam(limitParamName, limit)
        .build().toString();
  }
}