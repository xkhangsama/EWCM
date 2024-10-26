package com.project.EWCM.pojo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Account {
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id;
    private String username;
    private String fullName;
    private String email;
    private String type;
}
