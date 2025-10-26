package com.magic.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(LimitAccesses.class)
public @interface LimitAccess {
	
	public LimitType type();

	public int limit();

	public TimeUnit timeUnit();
}