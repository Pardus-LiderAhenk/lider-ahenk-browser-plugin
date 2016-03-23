package tr.org.liderahenk.browser.util;

import java.io.IOException;
import java.util.Set;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import tr.org.liderahenk.browser.constants.BrowserConstants;
import tr.org.liderahenk.browser.model.BrowserPreference;
import tr.org.liderahenk.liderconsole.core.model.Profile;

/**
 * Utility class that can be used to manage browser preferences
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public class BrowserUtil {

	/**
	 * 
	 * @param profile
	 * @return
	 */
	public static Set<BrowserPreference> getPreferences(Profile profile) {
		if (profile != null && profile.getProfileData() != null) {
			String setJsonStr = profile.getProfileData().get(BrowserConstants.PREFERENCES_MAP_KEY).toString();
			try {
				ObjectMapper mapper = new ObjectMapper();
				Set<BrowserPreference> preferences = mapper.readValue(setJsonStr,
						new TypeReference<Set<BrowserPreference>>() {
						});
				return preferences;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 
	 * @param preferences
	 * @param preferenceName
	 * @return
	 */
	public static String getPreferenceValue(Set<BrowserPreference> preferences, String preferenceName) {
		if (preferences != null) {
			for (BrowserPreference pref : preferences) {
				if (pref != null && pref.getPreferenceName() != null
						&& pref.getPreferenceName().equals(preferenceName)) {
					return pref.getValue();
				}
			}
		}
		return null;
	}

}
