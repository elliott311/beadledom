package com.cerner.beadledom.json.pagination.parameters;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.Pattern;

/**
 * Represent the limit parameter used for pagination.
 *
 * <p>The validation rules needed for a limit parameter are contained here
 * so that the same rules may be applied consistently across all paged
 * resources.
 *
 * @author Brian van de Boogaard
 * @since 2.2
 */
@ApiModel
public class LimitParameter extends AbstractParameter<Integer> {

  @ApiModelProperty(value = "Total number of items to return in the response.", dataType = "int",
      allowableValues = "range[0, 100]")
  @Pattern(regexp = "^[1-9][0-9]?$|^100$", message = "limit must be an integer between 1 and 100")
  private final String limit;

  public static final int DEFAULT_LIMIT = 20;

  /**
   * Creates an instance of {@link LimitParameter}.
   *
   * @param param the limit value from a request
   */
  public LimitParameter(String param) {
    super(param);
    this.limit = param;
  }

  @Override
  protected Integer parse(String param) throws InvalidParameterException {
    Integer limit;
    try {
      limit = Integer.parseInt(this.limit);
    } catch (NumberFormatException e) {
      throw InvalidParameterException
          .create("Invalid type for 'limit': " + this.limit + " - int is required.");
    }

    if (limit < 0 || limit > 100) {
      throw InvalidParameterException.create(
          "Invalid value for 'limit': " + this.limit + "  - value between 0 and 100 is required.");
    }

    return limit;
  }
}