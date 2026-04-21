package com.hms.service.enums;

import lombok.Getter;

@Getter
public enum ChannelTypes {

	WEB("WEB"), MOBILE("MOBILE");

	private final String channelName;

	ChannelTypes(String channelName) {
		this.channelName = channelName;
	}

	public String getChannelName() {
		return channelName;
	}
}
