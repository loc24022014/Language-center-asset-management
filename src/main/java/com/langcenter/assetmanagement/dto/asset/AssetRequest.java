package com.langcenter.assetmanagement.dto.asset;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class AssetRequest {

    @NotBlank(message = "Mã tài sản không được để trống")
    @Size(max = 50, message = "Mã tài sản tối đa 50 ký tự")
    private String assetCode;

    @NotBlank(message = "Tên tài sản không được để trống")
    @Size(max = 200, message = "Tên tài sản tối đa 200 ký tự")
    private String name;

    private String category;
    private String description;

    @NotNull(message = "Tổng số lượng không được để trống")
    @Min(value = 0, message = "Số lượng phải lớn hơn hoặc bằng 0")
    private Integer totalQuantity;

    private String unit;
    private String location;
    private String status;
}
