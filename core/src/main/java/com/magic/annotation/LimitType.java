package com.magic.annotation;

import java.lang.annotation.Annotation;
import java.util.concurrent.TimeUnit;

/**
 * @author 限频方式
 */
public enum LimitType {

	TOKEN,
	URI;

	public static final LimitAccess DEFAULT_TOKEN_ACCESS = new LimitAccess() {

		public Class<? extends Annotation> annotationType() {
			return LimitAccess.class;
		}

		public LimitType type() {
			return LimitType.TOKEN;
		}

		public int limit() {
			return 10;
		}

		public TimeUnit timeUnit() {
			return TimeUnit.SECONDS;
		}
	};


	public static final LimitAccess DEFAULT_URI_ACCESS = new LimitAccess() {

		public Class<? extends Annotation> annotationType() {
			return LimitAccess.class;
		}

		public LimitType type() {
			return LimitType.URI;
		}

		public int limit() {
			return 10;
		}

		public TimeUnit timeUnit() {
			return TimeUnit.SECONDS;
		}
	};
}