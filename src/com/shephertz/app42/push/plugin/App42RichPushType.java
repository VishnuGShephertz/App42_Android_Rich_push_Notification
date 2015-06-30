/**
 * -----------------------------------------------------------------------
 *     Copyright © 2015 ShepHertz Technologies Pvt Ltd. All rights reserved.
 * -----------------------------------------------------------------------
 */
package com.shephertz.app42.push.plugin;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Vishnu Garg
 * 
 */
public enum App42RichPushType {
	Html("html"), Image("image"), Video("video"), Text("text"), YouTubeVideo(
			"youTubeVideo"), OpenUrl("openUrl");
	private String value;

	private App42RichPushType(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
	private static final Map<String, App42RichPushType> codeMap = new HashMap<String, App42RichPushType>();
    static {
        // populating the map
        for (App42RichPushType adCode : values()) {
        	codeMap.put(adCode.getValue(), adCode);
        }
    }
    public static App42RichPushType getByValue(String value) {
        return codeMap.get(value);
    }
}
