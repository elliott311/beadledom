package com.cerner.beadledom.json.pagination.parameters;

import com.cerner.beadledom.json.common.model.JsonError;

import java.util.Optional;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Base class to be used for JAX-RS method parameters.
 *
 * <p>Provides a hook for the validation of parameters as well as enforcing a consistent error
 * structure.
 *
 * @author Brian van de Boogaard
 * @since 2.2
 */
public abstract class AbstractParameter<T> {
  private T value;
  private final String originalParameter;

  /**
   * Constructor for instances of {@link AbstractParameter}.
   *
   * @param param the parameter to parse
   */
  public AbstractParameter(String param) {
    this.originalParameter = param;
  }

  /**
   * Returns the parsed value.
   */
  public T getValue() {
    try {
      value = parse(originalParameter);
    } catch (InvalidParameterException e) {
      throw new WebApplicationException(onError(originalParameter, e));
    }
    return value;
  }

  /**
   * Returns the original parameter.
   */
  public String getOriginalParameter() {
    return originalParameter;
  }

  /**
   * Returns the value wrapped in an {@link Optional}.
   */
  public Optional<T> asOptional() {
    return Optional.ofNullable(value);
  }

  /**
   * Returns true if the value is null; false otherwise.
   */
  public boolean isNull() {
    return value == null;
  }

  /**
   * Returns true if the value is non-null; false otherwise.
   */
  public boolean exists() {
    return !isNull();
  }

  /**
   * Convert the parameter from the request into type T.
   *
   * @param param the parameter to parse
   * @return converted parameter
   * @throws InvalidParameterException is thrown if parsing fails
   */
  protected abstract T parse(String param) throws InvalidParameterException;

  protected Response onError(String param, InvalidParameterException e) {
    return Response.status(
        Response.Status.BAD_REQUEST)
        .type(MediaType.APPLICATION_JSON)
        .entity(getError(e))
        .build();
  }

  protected JsonError getError(InvalidParameterException e) {
    return JsonError.builder()
        .code(400)
        .message(e.getMessage())
        .errors(e.getErrorDetails())
        .build();
  }
}