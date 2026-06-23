package com.langcenter.assetmanagement.dto.transaction;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BorrowRequest {

    @NotNull(message = "ID tài sản không được để trống")
    private Integer assetId;

    @NotNull(message = "Số lượng mượn không được để trống")
    @Min(value = 1, message = "Số lượng mượn phải lớn hơn 0")
    private Integer quantity;

    @Future(message = "Ngày dự kiến trả phải ở tương lai")
    private LocalDateTime expectedReturn;

    private String note;
}
