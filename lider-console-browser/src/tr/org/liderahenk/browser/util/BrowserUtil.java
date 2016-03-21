package tr.org.liderahenk.browser.util;

import java.io.IOException;
import java.util.Set;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

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
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public static Set<BrowserPreference> getPreferences(Profile profile)
			throws JsonParseException, JsonMappingException, IOException {
		if (profile != null && profile.getProfileData() != null) {
			Set<BrowserPreference> preferences = new ObjectMapper().readValue(profile.getProfileData(), 0,
					profile.getProfileData().length, new TypeReference<Set<BrowserPreference>>() {
					});
			return preferences;
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
		for (BrowserPreference pref : preferences) {
			if (pref != null && pref.getPreferenceName() != null && pref.getPreferenceName().equals(preferenceName)) {
				return pref.getValue();
			}
		}
		return null;
	}

	/**
	 * Set specified preferences with values in the provided profile
	 * 
	 * @param profile
	 * @param preferenceName
	 * @param value
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonGenerationException
	 */
	public static void setPreferences(Profile profile, Set<BrowserPreference> preferences)
			throws JsonGenerationException, JsonMappingException, IOException {
		if (profile != null) {
			Set<BrowserPreference> existingPreferences = getPreferences(profile);
			if (existingPreferences.addAll(preferences)) {
				byte[] profileData = new ObjectMapper().writeValueAsBytes(existingPreferences);
				profile.setProfileData(profileData);
			}
		}
	}

}
