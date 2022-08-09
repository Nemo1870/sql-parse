package com.sql.parse.expression;

/**
 * @date 2021/1/25 14:45
 * @desc 操作类型枚举值
 */
public enum OperatorTypeEnum {
    EQUALS(0, "="),

    NOT_EQUALS(1, "!="),

    GREATER(2, ">"),

    GREATER_EQUALS(3, ">="),

    MINOR(4, "<"),

    MINOR_EQUALS(5, "<="),

    IS_NULL(6, "IsNull"),

    IS_NOT_NULL(7, "IsNotNull"),

    BETWEEN(8, "Between"),

    IN(9, "In"),

    LIKE(10, "Like"),

    REGEXP(11, "Regexp"),

    OTHER(12, "");

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
    OperatorTypeEnum(int value, String desc) {
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
    public static OperatorTypeEnum resolve(int value) {
        for (OperatorTypeEnum status : OperatorTypeEnum.values()) {
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
