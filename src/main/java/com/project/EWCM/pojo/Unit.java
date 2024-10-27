package com.project.EWCM.pojo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bson.types.ObjectId;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Unit {

    @JsonSerialize(using = ToStringSerializer.class)
    @NotNull(message = "Unit id cannot be null")
    private ObjectId id;
    private String unitName;
    @NotNull(message = "Unit level cannot be null")
    private int unitLevel;
}
