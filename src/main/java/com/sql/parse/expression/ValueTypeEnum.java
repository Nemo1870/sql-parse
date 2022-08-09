package com.sql.parse.expression;

/**
 * @date 2021/1/25 11:16
 * @desc 数据类型枚举值
 */
public enum ValueTypeEnum {
    NUMBER(0, "Number"),

    STRING(1, "String"),

    DATE(2, "Date"),

    TIME(3, "Time"),

    DATETIME(4, "DateTime"),

    BOOLEAN(5, "Boolean"),

    FUNCTION(6, "Function"),

    NULL(7, "Null"),

    OTHER(7, "Other");

    /**
     * 状态值
     */
    private int value;

    /**
     * 状态描述
     */
    private String desc;

    /**
     * 根据状态值和状态描述构造状态枚举
     *
     * @param value 状态值
     * @param desc  状态描述
     */
    ValueTypeEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    /**
     * 获取当前状态枚举的整型值
     *
     * @return
     */
    public int resolve() {
        return this.value;
    }

    /**
     * 将整型状态值转换为FmExportStatus，如果无法转换，将抛出IllegalArgumentException异常
     *
     * @param value 整型状态值
     * @return
     */
    public static ValueTypeEnum resolve(int value) {
        for (ValueTypeEnum status : ValueTypeEnum.values()) {
            if (status.value == value) {
                return status;
            }
        }
        throw new IllegalArgumentException("未找到[" + value + "]对应的枚举值。");
    }

    public String getDesc() {
        return desc;
    }
}
