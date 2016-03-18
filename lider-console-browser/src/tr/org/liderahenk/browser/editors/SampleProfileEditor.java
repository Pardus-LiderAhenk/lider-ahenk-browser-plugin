package tr.org.liderahenk.browser.editors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

public class SampleProfileEditor extends EditorPart {
	
	public SampleProfileEditor() {
		super();
	}
	
	@Override
	public void createPartControl(final Composite parent) {
		/*
		 * Auto-generated.
		 */
	}
	
	/**
	 * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override	
	public void doSave(IProgressMonitor monitor) {
		/*
		 * Auto-generated.
		 */
	}

	/**
	 * @see org.eclipse.ui.part.EditorPart#doSaveAs()
	 */
	@Override	
	public void doSaveAs() {
		/*
		 * Auto-generated.
		 */
	}

	/**
	 * @see org.eclipse.ui.part.EditorPart#init(org.eclipse.ui.IEditorSite, org.eclipse.ui.IEditorInput)
	 */
	@Override	
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		setSite(site);
		setInput(input);
		setPartName(input.getName());
	}
	
	@Override 
	public void dispose() {
		/*
		 * Auto-generated.
		 */
	}
	
	@Override
	public boolean isDirty() {
		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}
	
	@Override
	public void setFocus() {
		/*
		 * Auto-generated.
		 */	
	}
	
}