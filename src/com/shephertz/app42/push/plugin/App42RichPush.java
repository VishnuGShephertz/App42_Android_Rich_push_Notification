/**
 * -----------------------------------------------------------------------
 *     Copyright © 2015 ShepHertz Technologies Pvt Ltd. All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.shephertz.app42.push.plugin;

/**
 * @author Vishnu Garg
 * 
 */
public enum App42RichPush {
	Alert("alert"), Type("type"), Content("content");
	private String value;

	private App42RichPush(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
