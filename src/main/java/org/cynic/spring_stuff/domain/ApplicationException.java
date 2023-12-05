package org.cynic.spring_stuff.domain;

import java.io.Serial;
import java.util.StringJoiner;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;


public final class ApplicationException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;
    private final String code;
    private final transient Object[] values;

    public ApplicationException(final String code, Throwable e, final Object... values) {
        super(code, e);

        this.code = code;
        this.values = ObjectUtils.cloneIfPossible(values);
    }

    public ApplicationException(final String code, final Object... values) {
        this(code, null, values);
    }

    public String getCode() {
        return code;
    }

    public Object[] getValues() {
        return ObjectUtils.cloneIfPossible(values);
    }

    @Override
    public String getMessage() {
        return new StringJoiner(",")
            .merge(new StringJoiner("=").add("code").add(code))
            .merge(new StringJoiner("=").add("values").add(ArrayUtils.toString(values)))
            .toString();
    }
}
