package com.ink.workflow.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "monitor_execute")
public class MonitorExecute {
    public static final String COL_ID = "id";
    public static final String COL_MONITOR_ID = "monitor_id";
    public static final String COL_EXECUTE_ID = "execute_id";
    @TableId(value = "id", type = IdType.INPUT)
    private Long id;
    @TableField(value = "monitor_id")
    private Long monitorId;
    @TableField(value = "execute_id")
    private Long executeId;
}
