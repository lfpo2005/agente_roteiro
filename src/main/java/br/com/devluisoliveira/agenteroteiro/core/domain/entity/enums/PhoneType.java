package br.com.devluisoliveira.agenteroteiro.core.domain.entity.enums;

public enum PhoneType {
    HOME(0),
    MOBILE(1),
    DELIVERY(2),
    COMMERCIAL(3),
    OTHER(4);

    private final int code;

    PhoneType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static PhoneType fromCode(int code) {
        for (PhoneType type : PhoneType.values()) {
            if (type.getCode() == code) {
                return type;
            }
        }
        throw new IllegalArgumentException("No enum constant PhoneType." + code);
    }
}

