package org.xwalk.core.internal;

public @interface XWalkAPI {
    boolean createExternally() default false;

    boolean createInternally() default false;

    boolean delegate() default false;

    boolean disableReflectMethod() default false;

    Class<?> extendClass() default Object.class;

    Class<?> impl() default Object.class;

    Class<?> instance() default Object.class;

    boolean isConst() default false;

    boolean noInstance() default false;

    String[] postBridgeLines() default {};

    String[] postWrapperLines() default {};

    String[] preWrapperLines() default {};

    boolean reservable() default false;
}
