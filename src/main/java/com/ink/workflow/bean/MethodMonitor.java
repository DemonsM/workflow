package com.ink.workflow.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "method_monitor")
public class MethodMonitor {
    public static final String COL_ID = "id";
    public static final String COL_CLASS_METHOD = "class_method";
    public static final String COL_REQUEST_ARGS = "request_args";
    public static final String COL_RESPONSE_ARGS = "response_args";
    @TableId(value = "id", type = IdType.INPUT)
    private Long id;
    @TableField(value = "class_method")
    private String classMethod;
    @TableField(value = "request_args")
    private String requestArgs;
    @TableField(value = "response_args")
    private String responseArgs;
}
