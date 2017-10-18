package com.cerner.beadledom.json.pagination.parameters;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.Pattern;

/**
 * Represent the offset parameter used for pagination.
 *
 * <p>The validation rules needed for a offset parameter are contained here
 * so that the same rules may be applied consistently across all paged
 * resources.
 *
 * @author Brian van de Boogaard
 * @since 2.2
 */
@ApiModel
public class OffsetParameter extends AbstractParameter<Long> {

  @ApiModelProperty(value = "Number of items to offset the response by.", dataType = "int",
      allowableValues = "range[0, " + Long.MAX_VALUE + "]")
  @Pattern(regexp = "^0$|^[1-9][0-9]*$", message = "offset must be greater than or equal to zero")
  private final String offset;

  /**
   * Creates an instance of {@link OffsetParameter}.
   *
   * @param param the offset value from a request
   */
  public OffsetParameter(String param) {
    super(param);
    this.offset = param;
  }

  @Override
  protected Long parse(String param) throws InvalidParameterException {
    Long offset;
    try {
      offset = Long.parseLong(this.offset);
    } catch (NumberFormatException e) {
      throw InvalidParameterException
          .create("Invalid type for 'offset': " + this.offset + " - int is required.");
    }

    if (offset < 0) {
      throw InvalidParameterException
          .create(
              "Invalid value for 'offset': " + this.offset + " - positive value or zero required.");
    }

    return offset;
  }
}