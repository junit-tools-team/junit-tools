package org.junit.tools.configuration.base;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.xml.bind.annotation.XmlEnumValue;

/**
 * The test-priority. Default is Testprio.DEFAULT. For example this annotation
 * can be used for different test-suites and test-executions.
 * 
 * @author Robert Streng
 */
@Documented
@Retention(SOURCE)
@Target({ TYPE })
public @interface Testprio {

	TestprioValue prio() default TestprioValue.DEFAULT;

	public enum TestprioValue {

		@XmlEnumValue("high")
		HIGH("high"), @XmlEnumValue("default")
		DEFAULT("default"), @XmlEnumValue("low")
		LOW("low");

		private final String value;

		TestprioValue(String v) {
			value = v;
		}

		public String value() {
			return value;
		}

		public static TestprioValue fromValue(String v) {
			for (TestprioValue c : TestprioValue.values()) {
				if (c.value.equals(v)) {
					return c;
				}
			}
			throw new IllegalArgumentException(v);
		}

	}
}
