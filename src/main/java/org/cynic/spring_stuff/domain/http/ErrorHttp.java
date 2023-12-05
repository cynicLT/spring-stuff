package org.cynic.spring_stuff.domain.http;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import java.io.Serial;
import java.io.Serializable;

public record ErrorHttp(String code, Object... values) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ErrorHttp errorHttp = (ErrorHttp) o;

        return new EqualsBuilder().append(code, errorHttp.code).append(values, errorHttp.values).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(code).append(values).toHashCode();
    }
}
