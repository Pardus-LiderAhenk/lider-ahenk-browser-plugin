package tr.org.liderahenk.browser.tabs;

import org.eclipse.swt.widgets.Composite;

import tr.org.liderahenk.liderconsole.core.model.Profile;

public interface ISettingsTab {
	public void createInputs(Composite tabComposite, Profile browserProfile) throws Exception;

	public void addValuesToList(Profile profile) throws Exception;
}
