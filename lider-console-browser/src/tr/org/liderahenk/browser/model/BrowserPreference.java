package tr.org.liderahenk.browser.model;

import java.io.Serializable;

public class BrowserPreference implements Serializable {

	private static final long serialVersionUID = 2671227394653786584L;

	private String preferenceName;

	private String value;

	public BrowserPreference(String preferenceName, String value) {
		this.preferenceName = preferenceName;
		this.value = value;
	}

	public BrowserPreference() {
	}

	public String getPreferenceName() {
		return preferenceName;
	}

	public void setPreferenceName(String preferenceName) {
		this.preferenceName = preferenceName;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
