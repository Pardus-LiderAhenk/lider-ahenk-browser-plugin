package tr.org.liderahenk.browser.tabs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import tr.org.liderahenk.browser.dialogs.NewBlockSiteListItemDialog;
import tr.org.liderahenk.browser.i18n.Messages;
import tr.org.liderahenk.browser.model.BlockSiteURL;
import tr.org.liderahenk.browser.model.BrowserPreference;
import tr.org.liderahenk.browser.util.BrowserUtil;
import tr.org.liderahenk.browser.util.PreferenceNames;
import tr.org.liderahenk.liderconsole.core.model.Profile;
import tr.org.liderahenk.liderconsole.core.utils.SWTResourceManager;

public class BlockSiteSettingsTab implements ISettingsTab {

	private Button btnEnableBlockSite;
	private Button btnEnableWarningMessages;
	private Button btnEnableLinkRemoval;
	private Button btnBlacklist;
	private Button btnWhitelist;
	private Label lblList;
	private TableViewer tblVwrUrl;
	private Profile browserProfile;

	private List<BlockSiteURL> blockedUrlList;
	private List<BlockSiteURL> allowedUrlList;
	private BlockSiteURL selectedUrl;

	private static final String separator = "|||";
	private static final String separatorRegex = "\\|\\|\\|";

	private static final String blacklistRadioVal = "blacklistRadio";
	private static final String whitelistRadioVal = "whitelistRadio";

	@Override
	public void createInputs(Composite tabComposite, Profile browserProfile) throws Exception {

		this.browserProfile = browserProfile;

		Group group = new Group(tabComposite, SWT.BORDER_DOT);
		GridData gd = new GridData(SWT.LEFT, SWT.CENTER, false, false);
		gd.heightHint = 800;
		gd.widthHint = 500;
		group.setBounds(5, 5, 800, 480);
		group.setLayout(new GridLayout(2, false));
		group.setLayoutData(gd);

		((ScrolledComposite) tabComposite).setContent(group);
		((ScrolledComposite) tabComposite).setExpandVertical(true);
		((ScrolledComposite) tabComposite).setExpandHorizontal(true);
		((ScrolledComposite) tabComposite).setMinSize(400, 500);

		Set<BrowserPreference> preferences = BrowserUtil.getPreferences(browserProfile);

		Label lblEnableFunctions = new Label(group, SWT.NONE);
		lblEnableFunctions.setFont(SWTResourceManager.getFont("Sans", 9, SWT.BOLD));
		lblEnableFunctions.setText(Messages.getString("ENABLE_FUNCTIONS_LABEL"));
		new Label(group, SWT.NONE);

		btnEnableBlockSite = new Button(group, SWT.CHECK);
		btnEnableBlockSite.setText(Messages.getString("ENABLE_BLOCK_SITE_BTN"));
		String val = BrowserUtil.getPreferenceValue(preferences, PreferenceNames.ENABLE_BLOCK_SITE);
		btnEnableBlockSite.setSelection("true".equalsIgnoreCase(val));
		new Label(group, SWT.NONE);

		btnEnableWarningMessages = new Button(group, SWT.CHECK);
		btnEnableWarningMessages.setText(Messages.getString("ENABLE_WARNING_MESSAGES_BTN"));
		val = BrowserUtil.getPreferenceValue(preferences, PreferenceNames.ENABLE_WARNING_MESSAGES);
		btnEnableWarningMessages.setSelection("true".equalsIgnoreCase(val));
		new Label(group, SWT.NONE);

		btnEnableLinkRemoval = new Button(group, SWT.CHECK);
		btnEnableLinkRemoval.setText(Messages.getString("ENABLE_LINK_REMOVAL_BTN"));
		val = BrowserUtil.getPreferenceValue(preferences, PreferenceNames.ENABLE_LINK_REMOVAL);
		btnEnableLinkRemoval.setSelection("true".equalsIgnoreCase(val));
		new Label(group, SWT.NONE);

		btnBlacklist = new Button(group, SWT.RADIO);
		btnBlacklist.setText(Messages.getString("BLACKLIST_LABEL"));
		val = BrowserUtil.getPreferenceValue(preferences, PreferenceNames.BLOCK_SITE_LIST_TYPE);
		btnBlacklist.setSelection(val == null || blacklistRadioVal.equalsIgnoreCase(val));
		btnBlacklist.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleRadioButton();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		btnWhitelist = new Button(group, SWT.RADIO);
		btnWhitelist.setText(Messages.getString("WHITELIST_LABEL"));
		val = BrowserUtil.getPreferenceValue(preferences, PreferenceNames.BLOCK_SITE_LIST_TYPE);
		btnWhitelist.setSelection(whitelistRadioVal.equalsIgnoreCase(val));
		btnWhitelist.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleRadioButton();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		lblList = new Label(group, SWT.NONE);
		lblList.setFont(SWTResourceManager.getFont("Sans", 9, SWT.BOLD));
		lblList.setText(Messages.getString("BLACKLIST"));
		new Label(group, SWT.NONE);

		createListButtons(group);
		createListTable(group, preferences);

		handleRadioButton();
	}

	private void createListButtons(Group parentGroup) {

		Group group = new Group(parentGroup, SWT.NONE);
		group.setLayout(new GridLayout(2, false));
		group.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		group.setBounds(0, 0, 68, 68);

		Button btnAddPref = new Button(group, SWT.NONE);
		btnAddPref.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		btnAddPref.setText(Messages.getString("ADD"));
		btnAddPref.setImage(new Image(group.getDisplay(), this.getClass().getResourceAsStream("/icons/add.png")));
		btnAddPref.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				NewBlockSiteListItemDialog dialog = new NewBlockSiteListItemDialog(
						Display.getCurrent().getActiveShell(), getSelf());
				dialog.create();
				dialog.open();
			}
		});

		Button btnRemovePref = new Button(group, SWT.NONE);
		btnRemovePref.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		btnRemovePref.setImage(new Image(group.getDisplay(), this.getClass().getResourceAsStream("/icons/remove.png")));
		btnRemovePref.setText(Messages.getString("REMOVE"));
		btnRemovePref.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				if (tblVwrUrl.getTable().getSelectionIndex() > -1) {
					List<BlockSiteURL> list = btnBlacklist.getSelection() ? blockedUrlList : allowedUrlList;
					list.remove(tblVwrUrl.getTable().getSelectionIndex());
					tblVwrUrl.setInput(list);
					tblVwrUrl.refresh();
				}
			}
		});

	}

	private void createListTable(final Group parentGroup, Set<BrowserPreference> preferences) {

		tblVwrUrl = new TableViewer(parentGroup,
				SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		tblVwrUrl.setContentProvider(new ArrayContentProvider());
		tblVwrUrl.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		final Table table = tblVwrUrl.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.horizontalSpan = 2;
		data.heightHint = 100;
		table.setLayoutData(data);

		createTableColumns();

		setBlockedUrlList(getURLItems(preferences, browserProfile, true));
		setAllowedUrlList(getURLItems(preferences, browserProfile, false));
		tblVwrUrl.setInput(btnBlacklist.getSelection() ? getBlockedUrlList() : getAllowedUrlList());
		tblVwrUrl.refresh();

		tblVwrUrl.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) tblVwrUrl.getSelection();
				Object firstElement = selection.getFirstElement();
				if (firstElement instanceof BlockSiteURL) {
					setSelectedUrl((BlockSiteURL) firstElement);
				}
			}
		});

		tblVwrUrl.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				NewBlockSiteListItemDialog dialog = new NewBlockSiteListItemDialog(parentGroup.getShell(),
						getSelectedUrl(), getSelf(), true);
				dialog.create();
				dialog.open();
			}
		});

	}

	/**
	 * Converts 'extensions.BlockSite.blacklist' or
	 * 'extensions.BlockSite.whitelist' preference strings into a list of
	 * BlockSiteURL objects. Decides which preference to use according to
	 * useBlacklist parameter
	 * 
	 * @param preferences
	 * @param browserProfile
	 * @param useBlacklist
	 * @return
	 */
	private List<BlockSiteURL> getURLItems(Set<BrowserPreference> preferences, Profile browserProfile,
			boolean useBlacklist) {
		String urlStr = BrowserUtil.getPreferenceValue(preferences,
				useBlacklist ? PreferenceNames.BLACK_LIST : PreferenceNames.WHITE_LIST);
		String descStr = BrowserUtil.getPreferenceValue(preferences,
				useBlacklist ? PreferenceNames.BLACK_LIST_DESC : PreferenceNames.WHITE_LIST_DESC);
		return splitListItems(urlStr, descStr);
	}

	private List<BlockSiteURL> splitListItems(String urlStr, String descStr) {
		List<BlockSiteURL> listItems = new ArrayList<BlockSiteURL>();
		if (urlStr != null && !urlStr.isEmpty()) {
			String[] urlItems = urlStr.split(separatorRegex);
			String[] descItems = descStr.split(separatorRegex);
			for (int i = 0; i < urlItems.length; i++) {
				BlockSiteURL listItem = new BlockSiteURL(urlItems[i], i < descItems.length ? descItems[i] : null);
				listItems.add(listItem);
			}
		}
		return listItems;
	}

	private BlockSiteSettingsTab getSelf() {
		return this;
	}

	private void createTableColumns() {

		String[] titles = { Messages.getString("URL"), Messages.getString("DESCRIPTION") };
		int[] bounds = { 300, 100 };

		TableViewerColumn preferenceNameColumn = createTableViewerColumn(titles[0], bounds[0], tblVwrUrl);
		preferenceNameColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof BlockSiteURL)
					return ((BlockSiteURL) element).getURL();
				return Messages.getString("UNTITLED");
			}
		});

		TableViewerColumn valueColumn = createTableViewerColumn(titles[1], bounds[1], tblVwrUrl);
		valueColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof BlockSiteURL)
					return ((BlockSiteURL) element).getDescription();
				return Messages.getString("UNTITLED");
			}
		});

	}

	private TableViewerColumn createTableViewerColumn(String title, int bound, TableViewer viewer) {
		final TableViewerColumn viewerColumn = new TableViewerColumn(viewer, SWT.NONE);
		final TableColumn column = viewerColumn.getColumn();
		column.setText(title);
		column.setWidth(bound);
		column.setResizable(true);
		column.setMoveable(false);
		column.setAlignment(SWT.LEFT);
		return viewerColumn;
	}

	private void handleRadioButton() {
		lblList.setText(
				btnBlacklist.getSelection() ? Messages.getString("BLACKLIST") : Messages.getString("WHITELIST"));
		tblVwrUrl.setInput(btnBlacklist.getSelection() ? getBlockedUrlList() : getAllowedUrlList());
		tblVwrUrl.refresh();
	}

	@Override
	public void addValuesToList(Profile profile) throws Exception {
		Set<BrowserPreference> preferences = new HashSet<BrowserPreference>();
		preferences.add(new BrowserPreference(PreferenceNames.ENABLE_BLOCK_SITE,
				btnEnableBlockSite.getSelection() ? "true" : "false"));
		preferences.add(new BrowserPreference(PreferenceNames.ENABLE_WARNING_MESSAGES,
				btnEnableWarningMessages.getSelection() ? "true" : "false"));
		preferences.add(new BrowserPreference(PreferenceNames.ENABLE_LINK_REMOVAL,
				btnEnableLinkRemoval.getSelection() ? "true" : "false"));
		preferences.add(new BrowserPreference(PreferenceNames.BLOCK_SITE_LIST_TYPE,
				btnBlacklist.getSelection() ? blacklistRadioVal : whitelistRadioVal));
		// Create/update blocked URL preference
		List<String> urlPref = new ArrayList<String>();
		List<String> descPref = new ArrayList<String>();
		for (BlockSiteURL url : getBlockedUrlList()) {
			urlPref.add(url.getURL());
			descPref.add(url.getDescription() != null ? url.getDescription() : "");
		}
		preferences.add(new BrowserPreference(PreferenceNames.BLACK_LIST, StringUtils.join(urlPref, separator)));
		preferences.add(new BrowserPreference(PreferenceNames.BLACK_LIST_DESC, StringUtils.join(descPref, separator)));
		// Create/update allowed URL preference
		urlPref = new ArrayList<String>();
		descPref = new ArrayList<String>();
		for (BlockSiteURL url : getAllowedUrlList()) {
			urlPref.add(url.getURL());
			descPref.add(url.getDescription() != null ? url.getDescription() : "");
		}
		preferences.add(new BrowserPreference(PreferenceNames.WHITE_LIST, StringUtils.join(urlPref, separator)));
		preferences.add(new BrowserPreference(PreferenceNames.WHITE_LIST_DESC, StringUtils.join(descPref, separator)));
		BrowserUtil.setPreferences(profile, preferences);
	}

	public void addRecordToURLTable(List<BlockSiteURL> list) {
		if (btnBlacklist.getSelection()) {
			setBlockedUrlList(list);
		} else {
			setAllowedUrlList(list);
		}
		tblVwrUrl.setInput(list);
		tblVwrUrl.refresh();
	}

	public BlockSiteURL getSelectedUrl() {
		return selectedUrl;
	}

	public void setSelectedUrl(BlockSiteURL selectedUrl) {
		this.selectedUrl = selectedUrl;
	}

	public List<BlockSiteURL> getBlockedUrlList() {
		return blockedUrlList;
	}

	public void setBlockedUrlList(List<BlockSiteURL> blockedUrlList) {
		this.blockedUrlList = blockedUrlList;
	}

	public List<BlockSiteURL> getAllowedUrlList() {
		return allowedUrlList;
	}

	public void setAllowedUrlList(List<BlockSiteURL> allowedUrlList) {
		this.allowedUrlList = allowedUrlList;
	}

	public Button getBtnBlacklist() {
		return btnBlacklist;
	}

	public void setBtnBlacklist(Button btnBlacklist) {
		this.btnBlacklist = btnBlacklist;
	}

	public TableViewer getTblVwrUrl() {
		return tblVwrUrl;
	}

	public void setTblVwrUrl(TableViewer tblVwrUrl) {
		this.tblVwrUrl = tblVwrUrl;
	}

}
