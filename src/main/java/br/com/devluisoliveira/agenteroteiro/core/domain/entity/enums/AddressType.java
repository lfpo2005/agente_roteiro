package br.com.devluisoliveira.agenteroteiro.core.domain.entity.enums;

public enum AddressType {
    CHARGE(0),
    COMMERCIAL(1),
    DELIVERY(2),
    OTHER(3);

    private final int code;

    AddressType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static AddressType fromCode(int code) {
        for (AddressType type : AddressType.values()) {
            if (type.getCode() == code) {
                return type;
            }
        }
        throw new IllegalArgumentException("No enum constant AddressType." + code);
    }
}

