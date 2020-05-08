describe('Administration walkthrough', () => {
  before(() => {
    cy.demoStudentLoginSuggestion();
    cy.createSuggestion('TITLE', 'SUGESTAO', 'OPTION');
  });

  afterEach(() => {
    cy.contains('Logout').click();
  });

  it('Quick Approve a Suggestion', () => {
     cy.demoTeacherLoginSuggestion();
    cy.QuickApproveSuggestion('SUGESTAO');
  });

  it('Quick Reject a Suggestion', () => {
   cy.demoTeacherLoginSuggestion();
    cy.QuickRejectSuggestion('SUGESTAO');
  });

  it('Show a Suggestion and reject', () => {
   cy.demoTeacherLoginSuggestion();
    cy.ShowSuggestion('SUGESTAO');
    cy.RejectSuggestion();
    cy.CloseSuggestion();
  });

  it('Show a Suggestion and approve', () => {
    cy.demoTeacherLoginSuggestion();
    cy.ShowSuggestion('SUGESTAO');
    cy.ApproveSuggestion();
    cy.CloseSuggestion();
  });

  it('Show a Suggestion, approve and change into question', () => {
    cy.demoStudentLoginSuggestion();
    cy.createSuggestion('TITLE', 'SUGESTAO§§§§§§§21312', 'OPTION');
    cy.demoTeacherLoginSuggestion();
    cy.ShowSuggestion('SUGESTAO§§§§§§§21312');
    cy.ApproveSuggestion();
    cy.CloseSuggestion();
    cy.demoTeacherLoginSuggestion();
    cy.changeToQuestion('SUGESTAO§§§§§§§21312');
  });
});
