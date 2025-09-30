package com.ynov.devnotebook.ui.sections;

import com.ynov.devnotebook.ContentService;
import com.ynov.devnotebook.ui.SectionPanel;

/**
 * RefactoringSection: section panel for refactoring best practices.
 */
public class RefactoringSection extends SectionPanel {
    
    public RefactoringSection(ContentService contentService) {
        super(contentService, ContentService.KEY_REFACTORING);
    }
}
