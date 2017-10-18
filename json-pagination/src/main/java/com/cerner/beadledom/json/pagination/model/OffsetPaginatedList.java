package com.cerner.beadledom.json.pagination.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;

import java.io.Serializable;
import java.util.List;

import javax.annotation.Nullable;

/**
 * Offset based pagination list. APIs that return a response of this type will be paginated
 * by the {@Link OffsetPaginatedListWriterInterceptor}.
 */
@AutoValue
@JsonDeserialize(
    builder = AutoValue_OffsetPaginatedList.Builder.class
)
public abstract class OffsetPaginatedList<T> implements
    Serializable {
  /**
   * Returns a list of items for the current page.
   */
  @JsonProperty("items")
  public abstract List<T> items();

  /**
   * Returns true if there are additional items on another page; false if not; may be null if
   * {@code totalResults} is set instead.
   */
  @Nullable
  @JsonProperty("hasMore")
  public abstract Boolean hasMore();

  /**
   * Returns the total number of items across all pages; null if total is unknown.
   */
  @Nullable
  @JsonProperty("totalResults")
  public abstract Long totalResults();

  /**
   * Creates a builder for {@link OffsetPaginatedList}.
   *
   * @return instance of {@link OffsetPaginatedList.Builder}
   */
  public static <T> OffsetPaginatedList.Builder<T> builder() {
    return new AutoValue_OffsetPaginatedList.Builder<T>();
  }

  @AutoValue.Builder
  public abstract static class Builder<T> {

    @JsonProperty("items")
    public abstract OffsetPaginatedList.Builder<T> items(List<T> items);

    @JsonProperty("hasMore")
    public abstract OffsetPaginatedList.Builder<T> hasMore(Boolean hasMore);

    @JsonProperty("totalResults")
    public abstract OffsetPaginatedList.Builder<T> totalResults(Long totalResults);

    public abstract OffsetPaginatedList<T> build();
  }
}